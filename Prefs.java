import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    boolean isUnstablePair(Permutation mensMatches, int y, int x) {
        int matchedWoman = mensMatches.get(y);
        int matchedMan = mensMatches.getInverse(x);
        // Do y and x prefer to cheat with each other
        return (malePrefs(y, x) < malePrefs(y, matchedWoman)
             && femalePrefs(x, y) < femalePrefs(x, matchedMan));
    }

    boolean isStableMatching(Permutation mensMatches) {
        for (int y = 0; y < n(); y++) 
            for (int x = 0; x < n(); x++)
                if (isUnstablePair(mensMatches, y, x))
                    return false;
        return true;
    }

    void checkPermLength(Permutation mensMatches) {
        if (mensMatches.size() != n())
            throw new RuntimeException("Wrong size for permutation");
    }

    List<Index> nm1GeneratingPairs(Permutation mensMatches) {
        checkPermLength(mensMatches);

        List<Index> out = new ArrayList<>();
        for (int y = 0; y < n(); y++) { // men
            final Permutation row = malePrefs[y];
            int idxOfMinLeftValue = -1;
            for (int x = 0; x < n(); x++) { // women
                if (isUnstablePair(mensMatches, y, x) && (idxOfMinLeftValue == -1 || row.get(x) < row.get(idxOfMinLeftValue)))
                    idxOfMinLeftValue = x;
            }
            if (idxOfMinLeftValue != -1)
                out.add(new Index(y, idxOfMinLeftValue));
        }
        return out;
    }

    List<Index> nm1Pairs(Permutation mensMatches, List<Index> nm1GeneratingPairs) {
        checkPermLength(mensMatches);

        // this is pretty inefficient but it's easy to understand
        Map<Integer, Integer> generatingRowsOfCols = new HashMap<>();
        for (Index i : nm1GeneratingPairs) {
            int y2 = generatingRowsOfCols.getOrDefault(i.x(), -1);
            if (y2 == -1 || femalePrefs(i.x(), i.y()) < femalePrefs(i.x(), y2))
                generatingRowsOfCols.put(i.x(), i.y());
        }
        List<Index> out = new ArrayList<>();
        for (var e : generatingRowsOfCols.entrySet())
            out.add(new Index(e.getValue(), e.getKey()));
        return out;
    }

    /** The permutation that, when applied to the columns of `femalePrefs` (rows in the paper's presentation), yields the identity permutation
     * in the first row.
     */
    Permutation permToFemaleSorted() {
        if (n() == 0)
            return Permutation.identity(0);
        return femalePrefs[0].invert();
    }

    /** Apply `p` to the rows of the preference matrix of pairs, i.e. apply `p` to the order that the rows of `malePrefs`
     * appear (each individual row is unchanged) and, likewise, the order of the columns of `femalePrefs`. Equivalently,
     * we relabel the men everywhere.
     */
    Prefs applyPerm(Permutation p) {
        Permutation[] malePrefs2 = new Permutation[n()];
        // let f = malePrefs
        for (int y = 0; y < n(); y++)
            malePrefs2[p.get(y)] = malePrefs[y]; // send p(y) to f(y)
        Permutation[] femalePrefs2 = new Permutation[n()];
        for (int x = 0; x < n(); x++)
            femalePrefs2[x] = p.compose(femalePrefs[x]); // send (x := f(y)) to p(y)

        return new Prefs(malePrefs2, femalePrefs2);
    }

}
