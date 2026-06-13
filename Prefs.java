import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

record Prefs(Permutation[] malePrefs, Permutation[] femalePrefs) {
    
    /** Treat each row of `malePrefs` and `femalePrefs` as a permutation. This convenience constructor
     * expects the rows to be permutations of [1, n], not [0, n).
     */
    Prefs(int[][] malePrefs, int[][] femalePrefs) {
        this(new Permutation[malePrefs.length], new Permutation[malePrefs.length]);

        // assert invariants
        int n = malePrefs.length;
        if (n != femalePrefs.length)
            throw new RuntimeException("Bad number of rows for `femalePrefs`");

        for (int i = 0; i < n; i++) {
            this.malePrefs[i] = new Permutation(decrement(malePrefs[i]));
            this.femalePrefs[i] = new Permutation(decrement(femalePrefs[i]));
        }
    }

    @Override 
    public boolean equals(Object o) {
        return o instanceof Prefs other 
            && Arrays.equals(malePrefs, other.malePrefs) 
            && Arrays.equals(femalePrefs, other.femalePrefs);
    }

    @Override 
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(malePrefs));
    }

    private static int[] decrement(int[] arr) {
        int[] out = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
            out[i] = arr[i] - 1;
        return out;
    }

    public static Prefs random(Random rng, int n) {
        Permutation[] malePrefs = new Permutation[n];
        Permutation[] femalePrefs = new Permutation[n];
        for (int i = 0; i < n; i++) {
            malePrefs[i] = Permutation.random(rng, n);
            femalePrefs[i] = Permutation.random(rng, n);
        }
        return new Prefs(malePrefs, femalePrefs);
    }

    /** 
     * `malePrefs[y][x]` is male `y`'s ranking of woman `x`, a.k.a. the left value at index yx in the 
     * ranking matrix in the original PII paper.
     */
    @Override
    public Permutation[] malePrefs() {
        return malePrefs;
    }

    /** 
     * `femalePrefs[x][y]` is female `x`'s ranking of man `y`, a.k.a. the right value at index yx in the 
     * *transpose* of the ranking matrix in the original PII paper.
     */
    @Override
    public Permutation[] femalePrefs() {
        return femalePrefs;
    }

    public int malePrefs(int man, int woman) {
        return malePrefs[man].get(woman);
    }

    public int femalePrefs(int woman, int man) {
        return femalePrefs[woman].get(man);
    }

    int n() {
        return malePrefs.length;
    }

    /** Print out the preferences matrix with 1-indexed preferences */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int n = n();
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                sb.append("(");
                sb.append(malePrefs(y, x) + 1);
                sb.append(", ");
                sb.append(femalePrefs(x, y) + 1);
                sb.append(") ");
            }
            if (y + 1 < n)
                sb.append("\n");
        }
        return sb.toString();
    }

}
