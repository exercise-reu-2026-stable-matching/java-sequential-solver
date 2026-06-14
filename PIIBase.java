import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

abstract class PIIBase {
    final Prefs prefs;

    PIIBase(Prefs prefs) {
        this.prefs = prefs;
    }

    Permutation initiationPhase(Random rng) {
        return Permutation.random(rng, prefs.n());
    }

    /** Given the identity permutation (men's matches) as input, compute the next permutation after one PII iteration.
      * This function should respect composition, so that TODO */
    abstract Permutation iterationPhase();

    /** Given the men's matches as input, compute the next permutation after one PII iteration */
    // abstract Permutation iterationPhase(Permutation mensMatches);

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

    // Apply `p` to the rows and columns of `prefs`
    private static Prefs permutePrefs(Prefs prefs, Permutation p) {
        int n = p.size(); // TODO check this is correct against prefs
        
        Permutation[] malePrefs = prefs.malePrefs();
        Permutation[] femalePrefs = prefs.femalePrefs();
        Permutation[] malePrefs2 = new Permutation[n];
        Permutation[] femalePrefs2 = new Permutation[n];

        // undo `p` so that the matches are along the diagonal
        for (int i = 0; i < n; i++) {
            // apply p to the order of malePrefs
            malePrefs2[p.get(i)] = malePrefs[i];
            // same for femalePrefs
            femalePrefs2[i] = femalePrefs[i].compose(p);
        }
        return new Prefs(malePrefs2, femalePrefs2);
    }

    /** Do one `initiationPhase` and at most `c * n` `iterationPhase`s until a stable matching is reached.
     * Returns the output and whether it's a stable matching.
      */
    final Pair<Permutation, Boolean> runOne(int c, Permutation initial) {
        Permutation curr = initial;
        for (int i = 0; i < c * prefs.n(); i++) {
            if (isStableMatching(curr))
                return new Pair<>(curr, true);
            // System.out.println("curr: " + curr);
            
            // prefs but rearranged so that querying about its identity matching
            // is equivalent to querying about the original's matching under curr
            Prefs prefs0 = permutePrefs(this.prefs, curr.invert());
            curr = new PII(prefs0).iterationPhase();
        }
        return new Pair<>(curr, isStableMatching(curr));
    }

    public final Optional<Permutation> runOrCycle(Permutation initial) {
        Permutation curr = initial;
        Set<Permutation> visited = new HashSet<>();

        System.out.println("entry");
        while (!isStableMatching(curr)) {
            System.out.println("here: " + curr);
            if (visited.contains(curr)) return Optional.empty();
            visited.add(curr);

            Prefs prefs0 = permutePrefs(this.prefs, curr.invert());
            curr = new PII(prefs0).iterationPhase().compose(curr);
        }
        System.out.println("here: " + curr);
        return Optional.of(curr);
    }

    /** Keep running `runOne` until a stable matching is found */
    public final Permutation run(int c, Random rng) {
        Pair<Permutation, Boolean> res;
        do {
            res = runOne(c, rng);
        } while (!res.snd());
        return res.fst();
    }
}
