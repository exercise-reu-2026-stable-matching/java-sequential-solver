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

    private static int nUnstablePairs(PII pii, Permutation mensMatches) {
        int count = 0;
        for (int i = 0; i < pii.prefs.n(); i++)
            for (int j = 0; j < pii.prefs.n(); j++)
                if (pii.isUnstablePair(mensMatches, i, j))
                    count++;
        return count;
    }

    private static boolean isLatinSquare(Permutation[] prefs) {
        // We already have that all rows are permutations
        // Let's check the columns as well

        // for (Permutation )

        final int n = prefs.length;
        for (int x = 0; x < n; x++) {
            boolean[] present = new boolean[n];
            for (int y = 0; y < n; y++) {
                if (present[prefs[y].get(x)]) return false; // duplicate
                present[prefs[y].get(x)] = true;
            }
            // we can't be missing any if we don't have duplicates
        }
        return true;
    }

    public static void main(String[] args) {
        final int n = 4;
        List<Permutation[]> latinSquares =
            allPrefs(n).filter(Main::isLatinSquare).toList();
        Permutation initial = Permutation.identity(n);
        
        System.out.println(latinSquares.size() + " latin squares\n");
        
        int count = 0;
        for (Permutation[] malePrefs : latinSquares)
            for (Permutation[] femalePrefs : latinSquares) {
                Prefs prefs = new Prefs(malePrefs, femalePrefs);
                if (new PII(prefs).runOrCycle(initial).isEmpty()) {
                    System.out.println(prefs + "\n");
                    count++;
                }
            }
        System.out.println("count: " + count);
    }
}
