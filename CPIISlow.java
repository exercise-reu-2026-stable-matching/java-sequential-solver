
class CPIISlow extends CPII {
    CPIISlow(Prefs prefs) {
        super(prefs);
    }

    /** Return the male-dominant unstable pair for each row, or -1 if no such pair exists */
    @Override
    protected int[] maleDominantUnstablePairs(Permutation mensMatches) {
        int[] out = allUnmatched(n);

        for (int m = 0; m < n; m++) {
            Permutation malePrefs = prefs.malePrefs()[m];
            for (int rank = 0; rank < n; rank++) {
                int w = malePrefs.getInverse(rank);
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
    protected int[] maleFemaleDominantUnstablePairs(int[] maleDominantUnstablePairs) {
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
