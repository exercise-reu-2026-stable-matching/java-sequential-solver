record Prefs(int[][] malePrefs, int[][] femalePrefs) {
    // TODO assert invariants in constructor

    /** Don't use */
    private Prefs(){ this(null, null); throw new RuntimeException(); }

    /** 
     * `malePrefs[y][x]` is male `y`'s ranking of woman `x`, a.k.a. the left value at index yx in the 
     * ranking matrix in the original PII paper.
     */
    @Override
    public int[][] malePrefs() {
        return malePrefs;
    }

    /** 
     * `femalePrefs[x][y]` is female `x`'s ranking of man `y`, a.k.a. the right value at index yx in the 
     * *transpose* of the ranking matrix in the original PII paper.
     */
    @Override
    public int[][] femalePrefs() {
        return femalePrefs;
    }

    public int malePrefs(int man, int woman) {
        return malePrefs[man][woman];
    }

    public int femalePrefs(int man, int woman) {
        return femalePrefs[man][woman];
    }

    int n() {
        return malePrefs.length;
    }

}
