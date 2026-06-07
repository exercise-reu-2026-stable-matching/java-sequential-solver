import java.util.Random;
import java.util.ArrayList;
import java.util.List;

class PII {
    /** 
     * `malePrefs[y][x]` is male `y`'s ranking of woman `x`, a.k.a. the left value at index yx in the 
     * ranking matrix in the original PII paper.
     */
    final int[][] malePrefs;
    /** 
     * `femalePrefs[x][y]` is female `x`'s ranking of man `y`, a.k.a. the right value at index yx in the 
     * *transpose* of the ranking matrix in the original PII paper.
     */
    final int[][] femalePrefs;
    final int n;

    PII(int[][] malePrefs, int[][] femalePrefs) {
        this.malePrefs = malePrefs;
        this.femalePrefs = femalePrefs;
        this.n = malePrefs.length;
    }

    static class Permutation {
        private int[] perm;
        
        Permutation(int[] fn) {
            this.perm = fn;
        }
        
        int size() {
            return perm.length;
        }

        int get(int index) {
            return perm[index];
        }

        Permutation invert() {
            int[] out = new int[size()];
            for (int i = 0; i < out.length; i++)
                out[perm[i]] = i;
            return new Permutation(out);
        }

        static Permutation random(Random rng, int n) {
            int[] out = new int[n];
            // Fisher-Yates shuffle
            for (int i = 0; i < n; i++)
                out[i] = i;
            for (int i = n - 1; i >= 1; i--) {
                int j = rng.nextInt(i + 1);
                int tmp = out[i];
                out[i] = out[j];
                out[j] = tmp;
            }
            return new Permutation(out);
        }
    }

    Permutation initiationPhase(Random rng) {
        return Permutation.random(rng, n);
    }

    /** 2D index into an `n` x `n` array */
    static record Index(int y, int x) {}

    List<Index> unstablePairs(Permutation mensMatches) {
        Permutation womensMatches = mensMatches.invert();
        List<Index> out = new ArrayList<>();
        for (int y = 0; y < n; y++) { // men
            int matchedWoman = womensMatches.get(y);
            for (int x = 0; x < n; x++) { // women
                int matchedMan = mensMatches.get(x);
                // Do y and x prefer to cheat with each other
                if (malePrefs[y][x] < malePrefs[y][matchedWoman] && femalePrefs[x][y] < femalePrefs[x][matchedMan])
                    out.add(new Index(y, x));
            }
        }
        return out;
    }

    Permutation iterationPhase(Permutation mensMatches) {
        throw new RuntimeException("Not implemented yet"); // TODO
    }
}