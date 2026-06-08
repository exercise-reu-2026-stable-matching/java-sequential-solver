import java.util.Random;

abstract class PIIBase {
    /** 
     * `malePrefs[y][x]` is male `y`'s ranking of woman `x`, a.k.a. the left value at index yx in the 
     * ranking matrix in the original PII paper.
     */
    final int[][] malePrefs;
    /** 
     * `femalePrefs[x][y]` is female `x`'s ranking of man `y`, a.k.a. the right value at index yx in the 
     * *transpose* of the ranking matrix in the original PII paper.
     */
    final int[][] femalePrefs;
    final int n;

    PIIBase(int[][] malePrefs, int[][] femalePrefs) {
        this.malePrefs = malePrefs;
        this.femalePrefs = femalePrefs;
        this.n = malePrefs.length;
    }

    Permutation initiationPhase(Random rng) {
        return Permutation.random(rng, n);
    }

    final boolean isUnstable(Permutation mensMatches, Permutation womensMatches, int y, int x) {
        int matchedWoman = womensMatches.get(y);
        int matchedMan = mensMatches.get(x);
        // Do y and x prefer to cheat with each other
        return (malePrefs[y][x] < malePrefs[y][matchedWoman] && femalePrefs[x][y] < femalePrefs[x][matchedMan]);
    }

    final void checkPermLength(Permutation mensMatches) {
        if (mensMatches.size() != n)
            throw new RuntimeException("Wrong size for permutation");
    }

    final boolean isStableMatching(Permutation mensMatches) {
        Permutation womensMatches = mensMatches.invert();
        for (int y = 0; y < n; y++) 
            for (int x = 0; x < n; x++)
                if (isUnstable(mensMatches, womensMatches, y, x))
                    return false;
        return true;
    }

    abstract Permutation iterationPhase(Permutation mensMatches);

    /** Do one `initiationPhase` and at most `c * n` `iterationPhase`s until a stable matching is reached.
     * Returns the output and whether it's a stable matching.
      */
    final Pair<Permutation, Boolean> runOne(int c, Random rng) {
        Permutation curr = initiationPhase(rng);
        for (int i = 0; i < c * n; i++) {
            if (isStableMatching(curr))
                return new Pair<>(curr, true);
            curr = iterationPhase(curr);
        }
        return new Pair<>(curr, isStableMatching(curr));
    }
}
