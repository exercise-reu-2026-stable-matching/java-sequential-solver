import java.util.Random;

interface PIIBase {
    default Permutation initiationPhase(Prefs prefs, Random rng) {
        return Permutation.random(rng, prefs.n());
    }

    // TODO maybe extract some of these to helper functions

    static boolean isUnstablePair(Permutation mensMatches, Prefs prefs, int y, int x) {
        int matchedWoman = mensMatches.getInverse(y);
        int matchedMan = mensMatches.get(x);
        // Do y and x prefer to cheat with each other
        return (prefs.malePrefs(y, x) < prefs.malePrefs(y, matchedWoman)
             && prefs.femalePrefs(x, y) < prefs.femalePrefs(x, matchedMan));
    }

    static void checkPermLength(Permutation mensMatches, Prefs prefs) {
        if (mensMatches.size() != prefs.n())
            throw new RuntimeException("Wrong size for permutation");
    }

    static boolean isStableMatching(Permutation mensMatches, Prefs prefs) {
        for (int y = 0; y < prefs.n(); y++) 
            for (int x = 0; x < prefs.n(); x++)
                if (isUnstablePair(mensMatches, prefs, y, x))
                    return false;
        return true;
    }

    Permutation iterationPhase(Permutation mensMatches);

    // TODO move these to a subclass

    /** Do one `initiationPhase` and at most `c * n` `iterationPhase`s until a stable matching is reached.
     * Returns the output and whether it's a stable matching.
      */
    default Pair<Permutation, Boolean> runOne(Prefs prefs, int c, Random rng) {
        Permutation curr = initiationPhase(prefs, rng);
        for (int i = 0; i < c * prefs.n(); i++) {
            if (isStableMatching(curr, prefs))
                return new Pair<>(curr, true);
            curr = iterationPhase(curr);
        }
        return new Pair<>(curr, isStableMatching(curr, prefs));
    }

    /** Keep running `runOne` until a stable matching is found */
    default Permutation run(Prefs prefs, int c, Random rng) {
        Pair<Permutation, Boolean> res;
        do {
            res = runOne(prefs, c, rng);
        } while (!res.snd());
        return res.fst();
    }
}
