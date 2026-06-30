import java.util.Arrays;

class CPIISlow extends PIIBase {
    private static final int UNMATCHED = -1;

    static int[] allUnmatched(int n) {
        int[] out = new int[n];
        Arrays.fill(out, UNMATCHED);
        return out;
    }

    @Override
    Permutation initiationPhase(int n) {
        return new Permutation(allUnmatched(n), allUnmatched(n));
    }

    private static boolean isUnstablePair(Prefs prefs, Permutation mensMatches, int y, int x) {
        assert y != UNMATCHED;
        assert x != UNMATCHED;

        int matchedWoman = mensMatches.get(y);
        int matchedMan = mensMatches.getInverse(x);

        // System.out.printf("matchedWoman=%d, matchedMan=%d\n", matchedWoman, matchedMan);

        return (matchedWoman == UNMATCHED || prefs.malePrefs(y, x)   < prefs.malePrefs(y, matchedWoman)) 
            && (matchedMan == UNMATCHED   || prefs.femalePrefs(x, y) < prefs.femalePrefs(x, matchedMan));
    }

    /** Return the male-dominant unstable pair for each row, or -1 if no such pair exists */
    private static int[] maleDominantUnstablePairs(Prefs prefs, Permutation mensMatches) {
        final int n = prefs.n();
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
    private static int[] maleFemaleDominantUnstablePairs(Prefs prefs, int[] maleDominantUnstablePairs) {
        final int n = prefs.n();
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
    
    // ks is like mensMatches
    @Override
    Permutation iterationPhase(Prefs prefs, Permutation ks) {
        int[] bs = maleDominantUnstablePairs(prefs, ks);
        // System.out.println("bs: " + Arrays.toString(bs));
        int[] cs = maleFemaleDominantUnstablePairs(prefs, bs);
        // System.out.println("cs: " + Arrays.toString(cs));
        
        final int n = prefs.n();
        // start totally empty
        int[] nextMatches = allUnmatched(n);
        int[] nextMatchesInv = allUnmatched(n);

        // add C_i
        for (int x = 0; x < n; x++) {
            int y = cs[x];
            if (y != UNMATCHED) {
                nextMatches[y] = x;
                nextMatchesInv[x] = y;
            }
        }

        // add each element of K_i as long as it doesn't share a member with a pair in C_i
        for (int y = 0; y < n; y++) {
            int x = ks.get(y);
            if (x == UNMATCHED) continue;
            if (nextMatches[y] == UNMATCHED && nextMatchesInv[x] == UNMATCHED) {
                nextMatches[y] = x;
                assert nextMatchesInv[x] == UNMATCHED;
                nextMatchesInv[x] = y;
            }
        }

        return new Permutation(nextMatches, nextMatchesInv);
    }
}
