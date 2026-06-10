import java.util.Optional;
import java.util.Random;

class Main {
    /** Find a (randomly-generated) `Prefs` of size `n` that causes
     * `runOrCycle` to cycle. */
    static Prefs findCycle(Random rng, int n) {
        while (true) {
            Prefs prefs = Prefs.random(rng, n);
            PII pii = new PII(prefs);
            Permutation initial = Permutation.identity(n);
            Optional<Permutation> result = pii.runOrCycle(initial);
            if (!result.isPresent())
                return prefs;
        }
    }

    public static void main(String[] args) {
        Random rng = new Random(1);
        Prefs cyclePrefs = findCycle(rng, 3);
        System.out.println(cyclePrefs);
    }
}
