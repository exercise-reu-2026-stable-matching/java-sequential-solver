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

    static int[] mensFavorites(Prefs prefs) {
        int[] out = new int[prefs.n()];
        for (int i = 0; i < prefs.n(); i++)
            out[i] = prefs.malePrefs()[i].getInverse(0);
        return out;
    }

    static int countMatches(int[] arr) {
        int count = 0;
        for (int x : arr)
            if (x != -1)
                count++;
        return count;
    }

    public static void main(String[] args) {
        Random rng = new Random(1);
        final int nSamples = Integer.parseInt(args[0]);
        final int nSize    = Integer.parseInt(args[1]);
        final int maxIters   = Integer.parseInt(args[2]);

        // average sizes
        double[] kSizes = new double[maxIters], 
                 rSizes = new double[maxIters], 
                 aSizes = new double[maxIters], 
                 bSizes = new double[maxIters], 
                 cSizes = new double[maxIters];
        System.out.println("i,|K|,|R|,|A|,|B|,|C|,|K|/n,|R|/n,|A|/n,|B|/n,|C|/n");

        for (int s = 0; s < nSamples; s++) {
            Prefs prefs = Prefs.random(rng, nSize);
            Permutation curr = Permutation.allUnmatched(nSize);
            int nUnstable = Integer.MAX_VALUE;
            for (int i = 0; i < maxIters; i++) {
                if (nUnstable == 0) {
                    // we can skip work
                    // as, bs, cs, and rs don't have any pairs
                    // ks stay as previous (size n)
                    kSizes[i] += nSize / (double)nSamples;
                    continue;
                }
                nUnstable = CPII.unstablePairs(prefs, curr).size();
                aSizes[i] += nUnstable / (double)nSamples;
                
                int[] bs = CPII.maleDominantUnstablePairs(prefs, curr);
                int[] cs = CPII.maleFemaleDominantUnstablePairs(prefs, bs);

                bSizes[i] += countMatches(bs) / (double)nSamples;
                cSizes[i] += countMatches(cs) / (double)nSamples;

                Pair<Permutation, Integer> result = CPII.iterationPhase(prefs, curr, bs, cs);

                curr = result.fst();

                rSizes[i] += result.snd() / (double)nSamples;
                kSizes[i] += countMatches(curr.perm) / (double)nSamples;
            }
        }
        for (int i = 0; i < maxIters; i++) {
            System.out.printf("%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f\n", 
                i + 1,
                kSizes[i], rSizes[i], aSizes[i], bSizes[i], cSizes[i],
                kSizes[i] / nSize, rSizes[i] / nSize, aSizes[i] / nSize, bSizes[i] / nSize, cSizes[i] / nSize
            );
        }
    }
}
