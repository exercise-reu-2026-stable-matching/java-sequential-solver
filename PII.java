import java.util.Random;

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

    static int[] invertPermutation(int[] perm) {
        int[] out = new int[perm.length];
        for (int i = 0; i < perm.length; i++)
            out[perm[i]] = i;
        return out;
    }

    static int[] randomPermutation(Random rng, int n) {
        int[] out = new int[n];
        for (int i = 0; i < n; i++)
            out[i] = i;
        for (int i = n - 1; i >= 1; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = out[i];
            out[i] = out[j];
            out[j] = tmp;
        }
        return out;
    }

    /* The `int[]`s are maps from men to women */
    int[] initiationPhase(Random rng) {
        return randomPermutation(rng, n);
    }

    /* The `int[]`s are maps from men to women */
    int[] iterationPhase(int[] matches) {
        throw new RuntimeException("Not implemented yet"); // TODO
    }
}