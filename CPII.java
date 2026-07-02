import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class CPII extends PIIBase {
    protected static final int UNMATCHED = -1;

    /** Size */
    protected final int n;

    CPII(int n) {
        this.n = n;
    }

    final static int[] allUnmatched(int n) {
        int[] out = new int[n];
        Arrays.fill(out, UNMATCHED);
        return out;
    }

    @Override
    final Permutation initiationPhase(int n) {
        if (n != this.n) throw new RuntimeException(); // TODO
        return new Permutation(allUnmatched(n), allUnmatched(n));
    }

    protected static boolean isUnstablePair(Prefs prefs, Permutation mensMatches, int y, int x) {
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
    protected abstract int[] maleDominantUnstablePairs(Prefs prefs, Permutation mensMatches);

    /** Return the male-female-dominant unstable pair for each column, or -1 if no such pair exists.
     */
    protected abstract int[] maleFemaleDominantUnstablePairs(Prefs prefs, int[] maleDominantUnstablePairs);

    record IterResult(Permutation next, boolean done) {}

    // ks is like mensMatches. Returns (next permutation, done)
    private IterResult iterationPhaseImpl(Prefs prefs, Permutation ks) {
        if (n != prefs.n()) throw new RuntimeException(); // TODO
        

        int[] bs = maleDominantUnstablePairs(prefs, ks);
        // System.out.println("bs: " + Arrays.toString(bs));
        int[] cs = maleFemaleDominantUnstablePairs(prefs, bs);
        // System.out.println("cs: " + Arrays.toString(cs));
        
        // start totally empty
        int[] nextMatches = allUnmatched(n);
        int[] nextMatchesInv = allUnmatched(n);

        // A empty iff C empty
        boolean done = true;
        // add C_i
        for (int x = 0; x < n; x++) {
            int y = cs[x];
            if (y != UNMATCHED) {
                done = false;
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

        return new IterResult(new Permutation(nextMatches, nextMatchesInv), done);
    }

    @Override
    final Permutation iterationPhase(Prefs prefs, Permutation ks) {
        return iterationPhaseImpl(prefs, ks).next();
    }

    private static final int numMatched(Permutation p) {
        int count = 0;
        for (int w : p.perm)
            if (w != UNMATCHED)
                count++;
        return count;
    }

    static final BufferedOutputStream out = new BufferedOutputStream(System.out);

    private static void printf(String format, Object... args) throws IOException {
        out.write(String.format(format, args).getBytes());
    }

    /** Count the # of iterations until convergence. `initial` is the initial men's matches */
    final int countIters(Prefs prefs, Permutation initial) throws IOException {
        if (n != prefs.n()) throw new RuntimeException(); // TODO
        Permutation curr = initial;
        // long totalBSize = 0;
        for (int i = 0; ; i++) {
            final int kSize = numMatched(curr);
            final int bSize = n - kSize;
            printf("%d,%d,%d\n", n, i, bSize);

            var result = iterationPhaseImpl(prefs, curr);
            if (result.done()) {
                // if (i != 0)
                //     printf("%.3f\n", totalBSize / (double)i);
                return i;
            }
            // totalBSize += bSize;
            curr = result.next();
        }
    }
}