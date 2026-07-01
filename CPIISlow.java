
class CPIISlow extends CPII {
    
    CPIISlow(int n) {
        super(n);
    }

    /** Return the male-dominant unstable pair for each row, or -1 if no such pair exists */
    @Override
    protected int[] maleDominantUnstablePairs(Prefs prefs, Permutation mensMatches) {
        int[] out = allUnmatched(n);

        for (int m = 0; m < n; m++) {
            for (int w = 0; w < n; w++) {
                if (isUnstablePair(prefs, mensMatches, m, w)) {
                    out[m] = w;
                    break;
                }
            }
        }
        return out;
    }

    /** Return the male-female-dominant unstable pair for each column, or -1 if no such pair exists.
     */
    @Override
    protected int[] maleFemaleDominantUnstablePairs(Prefs prefs, int[] maleDominantUnstablePairs) {
        int[] out = allUnmatched(n); // by column

        for (int y = 0; y < n; y++) {
            int x = maleDominantUnstablePairs[y];
            if (x != UNMATCHED) {
                int oldY = out[x];
                if (oldY == UNMATCHED || prefs.femalePrefs(x, y) < prefs.femalePrefs(x, oldY))
                    out[x] = y;
            }
        }

        return out;
    }
}
