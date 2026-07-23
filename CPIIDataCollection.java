
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CPIIDataCollection extends CPIIFast {
    private final boolean isFemaleMale;
    private final List<IterationStateData> iterationStateDataList;
    private IterationStateData currentIterationStateData;

    CPIIDataCollection(int n, List<IterationStateData> iterationStateDataList, boolean isFemaleMale) {
        super(n);
        this.iterationStateDataList = iterationStateDataList;
        this.isFemaleMale = isFemaleMale;
    }

    private List<Index> unstablePairs(Prefs prefs, Permutation mensMatches) {
        List<Index> unstablePairs = new ArrayList<>();

        for (int m = 0; m < n; m++) {
            Permutation malePrefs = prefs.malePrefs()[m];

            // Exclude women the man can't possibly prefer to his current partner (if we have one)
            int matchedWoman = mensMatches.get(m);
            int matchedRank = matchedWoman == UNMATCHED ? n : malePrefs.get(matchedWoman);

            for (int rank = 0; rank < matchedRank; rank++) {
                int w = malePrefs.getInverse(rank);
                if (isUnstablePair(prefs, mensMatches, m, w)) {
                    unstablePairs.add(new Index(m, w));
                }
            }
        }

        return unstablePairs;
    }

    @Override
    protected int[] maleDominantUnstablePairs(Prefs prefs, Permutation mensMatches) {
        int[] out = super.maleDominantUnstablePairs(prefs, mensMatches);
        addMaleDominantToState(out);
        return out;
    }

    protected int[] maleDominantUnstablePairs(Prefs prefs, Permutation mensMatches, boolean saveToStateData) {
        if (saveToStateData) {
            return maleDominantUnstablePairs(prefs, mensMatches);
        }
        return super.maleDominantUnstablePairs(prefs, mensMatches);
    }

    /** Return the male-dominant unstable pair for each row, or -1 if no such pair exists */
    protected int[] maleDominantUnstablePairsNoProposalRanks(Prefs prefs, Permutation mensMatches) {
        int[] out = allUnmatched(n);

        for (int m = 0; m < n; m++) {
            Permutation malePrefs = prefs.malePrefs()[m];
            
            // Exclude women the man can't possibly prefer to his current partner (if we have one)
            int matchedWoman = mensMatches.get(m);
            int matchedRank = matchedWoman == UNMATCHED ? n : malePrefs.get(matchedWoman);
            
            for (int rank = 0; rank < matchedRank; rank++) {
                int w = malePrefs.getInverse(rank);
                if (isUnstablePair(prefs, mensMatches, m, w)) {
                    out[m] = w;
                    break;
                }
            }
        }
        return out;
    }

    @Override
    protected int[] maleFemaleDominantUnstablePairs(Prefs prefs, int[] maleDominantUnstablePairs) {
        int[] out = super.maleFemaleDominantUnstablePairs(prefs, maleDominantUnstablePairs);
        addMaleFemaleDominantToState(out);
        return out;
    }

    protected int[] maleFemaleDominantUnstablePairs(Prefs prefs, int[] maleDominantUnstablePairs, boolean saveToStateData) {
        if (saveToStateData) {
            return maleFemaleDominantUnstablePairs(prefs, maleDominantUnstablePairs);
        }
        return super.maleFemaleDominantUnstablePairs(prefs, maleDominantUnstablePairs);
    }

    /** Return the female-dominant unstable pair for each column, or -1 if no such pair exists */
    protected int[] femaleDominantUnstablePairs(Prefs prefs, Permutation mensMatches) {
        return maleDominantUnstablePairsNoProposalRanks(prefs.maleFemaleSwap(), mensMatches.inverse());
    }

    /** Return the female-male-dominant unstable pair for each row, or -1 if no such pair exists. */ 
    protected int[] femaleMaleDominantUnstablePairs(Prefs prefs, int[] femaleDominantUnstablePairs) {
        return maleFemaleDominantUnstablePairs(prefs.maleFemaleSwap(), femaleDominantUnstablePairs, false);
    }

    protected Pair<Permutation, Boolean> iterationPhaseImpl(Prefs prefs, Permutation ks, int trialIndex, int iteration) {
        currentIterationStateData = new IterationStateData(prefs.n(), trialIndex, iteration, isFemaleMale);
        iterationStateDataList.add(currentIterationStateData);

        currentIterationStateData.addMatching(ks.toIndices());
        currentIterationStateData.addUnstable(unstablePairs(prefs, ks));

        int[] b_f = femaleDominantUnstablePairs(prefs, ks);
        int[] c_fm = femaleMaleDominantUnstablePairs(prefs, b_f);
        addFemaleDominantToState(b_f);
        addFemaleMaleDominantToState(c_fm);

        return super.iterationPhaseImpl(prefs, ks);
    }

    public int countIters(Prefs prefs, Permutation initial, int trialIndex) {
        // Reset current proposal ranks before each trial
        Arrays.fill(maleCurrentProposalRanks, UNMATCHED);

        if (n != prefs.n()) throw new RuntimeException(); // TODO
        Permutation curr = initial;
        for (int i = 0; ; i++) {
            var result = iterationPhaseImpl(prefs, curr, trialIndex, i);
            if (result.snd()) {
                iterationStateDataList.removeLast();
                return i;
            }
            curr = result.fst();
        }
    }

    private void addMaleDominantToState(int[] maleDominantUnstablePairs) {
        if (isFemaleMale) {
            currentIterationStateData.addFemaleDominant(maleDominantUnstablePairs);
        }
        else {
            currentIterationStateData.addMaleDominant(maleDominantUnstablePairs);
        }
    }

    private void addFemaleDominantToState(int[] femaleDominantUnstablePairs) {
        if (isFemaleMale) {
            currentIterationStateData.addMaleDominant(femaleDominantUnstablePairs);
        }
        else {
            currentIterationStateData.addFemaleDominant(femaleDominantUnstablePairs);
        }
    }

    private void addMaleFemaleDominantToState(int[] maleFemaleDominantUnstablePairs) {
        if (isFemaleMale) {
            currentIterationStateData.addFemaleMaleDominant(maleFemaleDominantUnstablePairs);
        }
        else {
            currentIterationStateData.addMaleFemaleDominant(maleFemaleDominantUnstablePairs);
        }
    }

    private void addFemaleMaleDominantToState(int[] femaleMaleDominantUnstablePairs) {
        if (isFemaleMale) {
            currentIterationStateData.addMaleFemaleDominant(femaleMaleDominantUnstablePairs);
        }
        else {
            currentIterationStateData.addFemaleMaleDominant(femaleMaleDominantUnstablePairs);
        }
    }
}
