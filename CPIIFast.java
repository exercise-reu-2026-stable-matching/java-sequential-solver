
class CPIIFast extends CPII {
    /** In `maleDominantUnstablePairs` we can skip looking at highly-ranked (low rank number) women 
     * we already considered and broke up with. This and ignoring women who won't ever match with us should take the total runtime from O(n^2) per iteration
     * to O(n^2) total
      */
    protected final int[] maleCurrentProposalRanks;

    CPIIFast(int n) {
        super(n);
        maleCurrentProposalRanks = allUnmatched(n);
    }

    /** Return the male-dominant unstable pair for each row, or -1 if no such pair exists */
    @Override
    protected int[] maleDominantUnstablePairs(Prefs prefs, Permutation mensMatches) {
        int[] out = allUnmatched(n);

        for (int m = 0; m < n; m++) {
            Permutation malePrefs = prefs.malePrefs()[m];

            // See note on `maleCurrentProposalRanks`
            int currentRank = maleCurrentProposalRanks[m];
            int startRank = currentRank == UNMATCHED ? 0 : currentRank;
            
            // Exclude women the man can't possibly prefer to his current partner (if we have one)
            int matchedWoman = mensMatches.get(m);
            int matchedRank = matchedWoman == UNMATCHED ? n : malePrefs.get(matchedWoman);
            
            for (int rank = startRank; rank < matchedRank; rank++) {
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
