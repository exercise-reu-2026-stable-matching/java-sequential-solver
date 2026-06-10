import java.util.Random;

abstract class PIIBase {
    final Prefs prefs;

    PIIBase(Prefs prefs) {
        this.prefs = prefs;
    }

    Permutation initiationPhase(Random rng) {
        return Permutation.random(rng, prefs.n());
    }

    abstract Permutation iterationPhase(Permutation mensMatches);

    boolean isUnstablePair(Permutation mensMatches, int y, int x) {
        int matchedWoman = mensMatches.get(y);
        int matchedMan = mensMatches.getInverse(x);
        // Do y and x prefer to cheat with each other
        return (prefs.malePrefs(y, x) < prefs.malePrefs(y, matchedWoman)
             && prefs.femalePrefs(x, y) < prefs.femalePrefs(x, matchedMan));
    }

    void checkPermLength(Permutation mensMatches) {
        if (mensMatches.size() != prefs.n())
            throw new RuntimeException("Wrong size for permutation");
    }

    boolean isStableMatching(Permutation mensMatches) {
        for (int y = 0; y < prefs.n(); y++) 
            for (int x = 0; x < prefs.n(); x++)
                if (isUnstablePair(mensMatches, y, x))
                    return false;
        return true;
    }

    final Pair<Permutation, Boolean> runOne(int c, Random rng) {
        Permutation initial = initiationPhase(rng);
        return runOne(c, initial);
    }

    /** Do one `initiationPhase` and at most `c * n` `iterationPhase`s until a stable matching is reached.
     * Returns the output and whether it's a stable matching.
      */
    final Pair<Permutation, Boolean> runOne(int c, Permutation initial) {
        Permutation curr = initial;
        for (int i = 0; i < c * prefs.n(); i++) {
            if (isStableMatching(curr))
                return new Pair<>(curr, true);
            System.out.println("curr: " + curr);
            curr = iterationPhase(curr);
        }
        return new Pair<>(curr, isStableMatching(curr));
    }

    /** Keep running `runOne` until a stable matching is found */
    final Permutation run(int c, Random rng) {
        Pair<Permutation, Boolean> res;
        do {
            res = runOne(c, rng);
        } while (!res.snd());
        return res.fst();
    }
}
