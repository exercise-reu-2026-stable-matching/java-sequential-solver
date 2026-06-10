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

    private static int[] decrement(int[] arr) {
        int[] out = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
            out[i] = arr[i] - 1;
        return out;
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

}
