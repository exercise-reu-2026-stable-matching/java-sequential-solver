import java.util.Random;

abstract class PIIBase {
    static Permutation initiationPhase(Prefs prefs, Random rng) {
        return Permutation.random(rng, prefs.n());
    }

    abstract Permutation iterationPhase(Permutation mensMatches);

    static boolean isUnstablePair(Permutation mensMatches, Prefs prefs, int y, int x) {
        int matchedWoman = mensMatches.get(y);
        int matchedMan = mensMatches.getInverse(x);
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

    final Pair<Permutation, Boolean> runOne(Prefs prefs, int c, Random rng) {
        Permutation initial = initiationPhase(prefs, rng);
        return runOne(prefs, c, initial);
    }

    /** Do one `initiationPhase` and at most `c * n` `iterationPhase`s until a stable matching is reached.
     * Returns the output and whether it's a stable matching.
      */
    final Pair<Permutation, Boolean> runOne(Prefs prefs, int c, Permutation initial) {
        Permutation curr = initial;
        for (int i = 0; i < c * prefs.n(); i++) {
            System.out.println("curr: " + curr);
            if (isStableMatching(curr, prefs))
                return new Pair<>(curr, true);
            curr = iterationPhase(curr);
        }
        return new Pair<>(curr, isStableMatching(curr, prefs));
    }

    // TODO we shouldn't have to pass `prefs` in here again

    /** Keep running `runOne` until a stable matching is found */
    final Permutation run(Prefs prefs, int c, Random rng) {
        Pair<Permutation, Boolean> res;
        do {
            res = runOne(prefs, c, rng);
        } while (!res.snd());
        return res.fst();
    }
}
