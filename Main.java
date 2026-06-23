import java.util.List;
import java.util.Objects;
import java.util.Random;
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

    static Pair<Boolean, Integer> countCycles(PIIBase pii, Prefs prefs, Permutation init) {
        var result = pii.runOrCycle(prefs, init);
        return new Pair<>(result.fst().isPresent(), result.snd());
    }

    public static void main(String[] args) {
        Random rng = new Random();
        final int nIters       = Integer.parseInt(args[0]);
        final int nSize        = Integer.parseInt(args[1]);
        final String writeFile = args[2];

        PII pii = new PII();
        CPII cpii = new CPII();

        System.out.println("pii_converged,pii_diverged,pii_converged_total_iters,pii_diverged_total_iters,cpii_total_iters");
        Permutation identity = Permutation.identity(nSize);
        Permutation empty = Permutation.allUnmatched(nSize);

        long nPIIConverging = 0, nItersForPIIOfConverging = 0, nItersForPIIOfDiverging = 0,
             nItersForCPII = 0;

        // int j = 0;
        for (int i = 0; i < nIters; i++) {
            Prefs prefs = Prefs.random(rng, nSize);
            var piiResult = countCycles(pii, prefs, identity);
            var cpiiResult = countCycles(cpii, prefs, empty);

            // System.out.println(prefs);

            if (piiResult.fst()) {
                nPIIConverging++;
                nItersForPIIOfConverging += piiResult.snd();
            } else
                nItersForPIIOfDiverging += piiResult.snd();

            assert cpiiResult.fst() : prefs;
            nItersForCPII += cpiiResult.snd();

            // for (; j < PII.stateDataList.size(); j++) {
            //     System.out.println(PII.stateDataList.get(j).converges);
            // }
            // System.out.println();
        }

        System.out.printf("%d,%d,%d,%d,%d\n", 
            nPIIConverging, nIters - nPIIConverging, nItersForPIIOfConverging, nItersForPIIOfDiverging, nItersForCPII);
        
        // System.out.println(PII.stateDataList.get(0).toCSVString());
        PII.toCSV(writeFile);
    }
}
