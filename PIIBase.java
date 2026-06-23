import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

abstract class PIIBase {
    abstract Permutation initiationPhase(int n);

    abstract Permutation iterationPhase(Prefs prefs, Permutation mensMatches);

    final Pair<Permutation, Boolean> runOne(int c, Prefs prefs) {
        Permutation initial = initiationPhase(prefs.n());
        return runOne(c, prefs, initial);
    }

    /** Do one `initiationPhase` and at most `c * n` `iterationPhase`s until a stable matching is reached.
     * Returns the output and whether it's a stable matching.
      */
    final Pair<Permutation, Boolean> runOne(int c, Prefs prefs, Permutation initial) {
        Permutation curr = initial;
        for (int i = 0; i < c * prefs.n(); i++) {
            if (prefs.isStableMatching(curr))
                return new Pair<>(curr, true);
            // System.out.println("curr: " + curr);
            curr = iterationPhase(prefs, curr);
        }
        return new Pair<>(curr, prefs.isStableMatching(curr));
    }

    public Pair<Optional<Permutation>, Integer> runOrCycle(Prefs prefs, Permutation initial) {
        Permutation curr = initial;
        Set<Permutation> visited = new HashSet<>();

        int iters = 0;
        while (!prefs.isStableMatching(curr)) {
            if (visited.contains(curr)) return new Pair<>(Optional.empty(), iters);
            visited.add(curr);
            curr = iterationPhase(prefs, curr);
            iters++;
        }
        return new Pair<>(Optional.of(curr), iters);
    }

    /** Keep running `runOne` until a stable matching is found */
    public final Permutation run(int c, Prefs prefs) {
        Pair<Permutation, Boolean> res;
        do {
            res = runOne(c, prefs);
        } while (!res.snd());
        return res.fst();
    }
}
