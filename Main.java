import java.util.stream.Stream;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class Main {

    private static <T, U, R> Stream<R> productMap(Stream<T> ts, Supplier<Stream<U>> us, BiFunction<T, U, R> f) {
        return ts.flatMap(t -> us.get().map(u -> f.apply(t, u)));
    }

    // TODO this has duplicates up to permutation
    private static Stream<Permutation[]> allPrefsGo(int n, int fuel) {
        if (fuel == 0) return Stream.ofNullable(new Permutation[0]);
        Supplier<Stream<Permutation>> allPerms = () -> Permutation.all(n);
        
        return productMap(allPrefsGo(n, fuel - 1), allPerms, (tl, hd) -> {
            Permutation[] next = Arrays.copyOf(tl, tl.length + 1);
            next[next.length - 1] = hd;
            return next;
        });
    }

    private static Stream<Permutation[]> allPrefs(int n) {
        return allPrefsGo(n, n);
    }

    static void searchForCycles(int n, int howMany) {
        Random rng = new Random(n);
        Set<Prefs> cyclePrefs = new HashSet<>();
        Permutation initial = Permutation.identity(n);
        long count = 0;
        
        for (int i = 0; i < howMany; i++) {
            Prefs prefs = Prefs.random(rng, n);
            PII pii = new PII(prefs);
            if (pii.runOrCycle(initial).isEmpty()) {
                cyclePrefs.add(prefs);
                count++;
            }
        }
        for (Prefs p : cyclePrefs)
            System.out.println(p + "\n");
        System.out.println("Count: " + count + ", unique: " + cyclePrefs.size());
    }

    public static void main(String[] args) {
        final int n = Integer.valueOf(args[0]);
        final int howMany = Integer.valueOf(args[1]);
        searchForCycles(n, howMany);
    }
}
