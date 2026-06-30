import java.util.Arrays;

class CPII extends PIIBase {
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

    private final Prefs prefs;
    /** Size */
    private final int n;
    /** In `maleDominantUnstablePairs` we can skip looking at highly-ranked (low rank number) women 
     * we already considered and broke up with. This should take the total runtime from O(n^2) per iteration
     * to O(n^2) total
      */
    private final int[] maleCurrentProposalRanks;

    CPII(Prefs prefs) {
        this.prefs = prefs;
        n = prefs.n();
        maleCurrentProposalRanks = allUnmatched(n);
    }

    /** Return the male-dominant unstable pair for each row, or -1 if no such pair exists */
    private int[] maleDominantUnstablePairs(Permutation mensMatches) {
        int[] out = allUnmatched(n);

        for (int m = 0; m < n; m++) {
            Permutation malePrefs = prefs.malePrefs()[m];
            int currentRank = maleCurrentProposalRanks[m];
            int startRank = currentRank == UNMATCHED ? 0 : currentRank;
            for (int rank = startRank; rank < n; rank++) {
                int w = malePrefs.getInverse(rank);
                if (isUnstablePair(prefs, mensMatches, m, w)) {
                    out[m] = w;
                    maleCurrentProposalRanks[m] = rank;
                    break;
                }
            }
        }
        return out;
    }

    /** Return the male-female-dominant unstable pair for each column, or -1 if no such pair exists.
     */
    private int[] maleFemaleDominantUnstablePairs(int[] maleDominantUnstablePairs) {
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

    static boolean isStableMatching(Prefs prefs, Permutation ks) {
        for (int y = 0; y < prefs.n(); y++) {
            for (int x = 0; x < prefs.n(); x++)
                if (isUnstablePair(prefs, ks, y, x))
                    return false;
        }
        return true;
    }
    
    // ks is like mensMatches
    @Override
    Permutation iterationPhase(Prefs prefs, Permutation ks) {
        int[] bs = maleDominantUnstablePairs(ks);
        // System.out.println("bs: " + Arrays.toString(bs));
        int[] cs = maleFemaleDominantUnstablePairs(bs);
        // System.out.println("cs: " + Arrays.toString(cs));
        
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
