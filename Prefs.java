import java.util.Arrays;

record Prefs(int[][] malePrefs, int[][] femalePrefs) {
    // Checks if prefs are all permutations of [1..n]
    private static void checkPerm(int[] row, int n) {
        if (row.length != n)
            throw new RuntimeException("Bad number of columns");
        
        // each element should be present exactly once
        boolean[] present = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (present[row[i] - 1])
                throw new RuntimeException("Duplicate " + row[i] + " in row " + Arrays.toString(row));
            present[row[i] - 1] = true;
        }
        for (int i = 0; i < n; i++)
            if (!present[i])
                throw new RuntimeException("Missing " + i + " in row " + Arrays.toString(row));
    }
    
    Prefs(int[][] malePrefs, int[][] femalePrefs) {
        this.malePrefs = malePrefs;
        this.femalePrefs = femalePrefs;

        // assert invariants
        int n = malePrefs.length;
        if (n != femalePrefs.length)
            throw new RuntimeException("Bad number of rows for `femalePrefs`");
        for (int i = 0; i < n; i++) {
            checkPerm(malePrefs[i], n);
            checkPerm(femalePrefs[i], n);
        }
    }

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
