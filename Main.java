import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

class Main {
    /** Overwrites with the next sequence in the lexicographic ordering of `[0, k)^n`.
     * Returns true iff we're not at the end of the sequence.
     */
    private static boolean nextSeq(long k, int[] arr) {
        final int n = arr.length;
        for (int i = n - 1; i >= 0; i--) {
            if (arr[i] < k - 1) {
                arr[i]++;
                return true;
            } else {
                arr[i] = 0;
                if (i == 0)
                    return false;
            }
        }
        throw new AssertionError("unreachable!");
    }

    /** The Cartesian product `[0, k)` `n` times total */
    private static Stream<int[]> products(long k, int n) {
        return Stream.iterate(new int[n], arr -> {
            if (nextSeq(k, arr)) return arr; else return null;
        }).takeWhile(Objects::nonNull);
    }

    private static long factorial(int n) {
        long out = 1;
        for (int i = 2; i <= n; i++)
            out *= i;
        return out;
    }

    /** All `(n!)^n` many `n x n` preference matrices */
    private static Stream<Permutation[]> allPrefs(int n) {
        final List<Permutation> allPerms = Permutation.all(n).toList();
        return products(factorial(n), n).map(indices -> {
            Permutation[] out = new Permutation[n];
            for (int i = 0; i < n; i++)
                out[i] = allPerms.get(indices[i]);
            return out;
        });
    }

    private static <T, U, R> Stream<R> productMap(Stream<T> ts, Supplier<Stream<U>> us, BiFunction<T, U, R> f) {
        return ts.flatMap(t -> us.get().map(u -> f.apply(t, u)));
    }

    private static long cyclingCount = 0;

    public static void main(String[] args) {
        // Search over the entire state space. Only really possible for n <= 4, and n = 4 requires a lot of compute
        final int n = 3;

        // Split up the work by having each Java process work on a chunk of the male preferences
        final int chunkSize = Integer.parseInt(args[0]);
        final int processID = Integer.parseInt(args[1]);

        System.out.println("chunkSize = " + chunkSize + "; processID = " + processID);

        Stream<Permutation[]> malePrefs = allPrefs(n).skip((long)chunkSize * processID).limit(chunkSize);
        Supplier<Stream<Permutation[]>> allFemalePrefs = () -> allPrefs(n);
        Stream<Prefs> allPrefs = productMap(malePrefs, allFemalePrefs, Prefs::new);
        Permutation initial = Permutation.identity(n);
        Stream<Prefs> cycling = allPrefs.filter(p -> new PII(p).runOrCycle(initial).isEmpty());
        cycling.forEach(prefs -> {
            System.out.println(prefs + "\n");
            cyclingCount++;
        });
        System.out.println("Count: " + cyclingCount);
    }
}
