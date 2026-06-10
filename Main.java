import java.util.stream.Stream;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.Arrays;
import java.util.List;

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

    public static void main(String[] args) {
        final int n = 3;

        Stream<Permutation[]> allMalePrefs = allPrefs(n);
        Supplier<Stream<Permutation[]>> allFemalePrefs = () -> allPrefs(n);
        Stream<Prefs> allPrefs = productMap(allMalePrefs, allFemalePrefs, Prefs::new);
        Permutation initialMatching = Permutation.identity(n);
        
        List<Prefs> s = allPrefs
            .filter(p -> new PII(p).runOrCycle(initialMatching).isEmpty())
            .toList();
        System.out.println(s.size());
        for (Prefs p : s)
            System.out.println(p + "\n");
    }
}
