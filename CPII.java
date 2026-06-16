import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    static boolean isUnstablePair(Prefs prefs, Permutation mensMatches, int y, int x) {
        assert y != UNMATCHED;
        assert x != UNMATCHED;

        int matchedWoman = mensMatches.get(y);
        int matchedMan = mensMatches.getInverse(x);

        // System.out.printf("matchedWoman=%d, matchedMan=%d\n", matchedWoman, matchedMan);

        return (matchedWoman == UNMATCHED || prefs.malePrefs(y, x)   < prefs.malePrefs(y, matchedWoman)) 
            && (matchedMan == UNMATCHED   || prefs.femalePrefs(x, y) < prefs.femalePrefs(x, matchedMan));
    }

    static List<Index> unstablePairs(Prefs prefs, Permutation mensMatches) {
        final int n = prefs.n();
        List<Index> out = new ArrayList<>();

        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (isUnstablePair(prefs, mensMatches, y, x))
                    out.add(new Index(y, x));
            }
        }

        return out;
    }

    /** Return the male-dominant unstable pair for each row, or -1 if no such pair exists */
    static int[] maleDominantUnstablePairs(Prefs prefs, Permutation mensMatches) {
        final int n = prefs.n();
        int[] out = allUnmatched(n);

        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (isUnstablePair(prefs, mensMatches, y, x)) {
                    // System.out.printf("y=%d, x=%d unstable\n", y, x);
                    int oldX = out[y];
                    if (oldX == UNMATCHED || prefs.malePrefs(y, x) < prefs.malePrefs(y, oldX))
                        out[y] = x;
                }
            }
        }

        return out;
    }

    /** Return the male-female-dominant unstable pair for each column, or -1 if no such pair exists.
     */
    static int[] maleFemaleDominantUnstablePairs(Prefs prefs, int[] maleDominantUnstablePairs) {
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

    static boolean isStableMatching(Prefs prefs, Permutation ks) {
        for (int y = 0; y < prefs.n(); y++) {
            for (int x = 0; x < prefs.n(); x++)
                if (isUnstablePair(prefs, ks, y, x))
                    return false;
        }
        return true;
    }

    /** Returns the next matching and the overlap count (|R|) */
    static Pair<Permutation, Integer> iterationPhase(Prefs prefs, Permutation ks, int[] bs, int[] cs) {
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

        int rCount = 0;
        // add each element of K_i as long as it doesn't share a member with a pair in C_i
        for (int y = 0; y < n; y++) {
            int x = ks.get(y);
            if (x == UNMATCHED) continue;
            if (nextMatches[y] == UNMATCHED && nextMatchesInv[x] == UNMATCHED) {
                nextMatches[y] = x;
                assert nextMatchesInv[x] == UNMATCHED;
                nextMatchesInv[x] = y;
            } else
                rCount++;
        }

        return new Pair<>(new Permutation(nextMatches, nextMatchesInv), rCount);
    }
    
    // ks is like mensMatches
    @Override
    Permutation iterationPhase(Prefs prefs, Permutation ks) {
        int[] bs = maleDominantUnstablePairs(prefs, ks);
        // System.out.println("bs: " + Arrays.toString(bs));
        int[] cs = maleFemaleDominantUnstablePairs(prefs, bs);
        // System.out.println("cs: " + Arrays.toString(cs));
        
        return iterationPhase(prefs, ks, bs, cs).fst();
    }
}
