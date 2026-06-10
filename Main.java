import java.util.Random;

class Main {
    /** Find a (randomly-generated) `Prefs` of size `n` that causes
     * `runOne` with `c` to not yield a stable matching. */
    static Prefs findNonTerminating(Random rng, int n, int c) {
        while (true) {
            Prefs prefs = Prefs.random(rng, n);
            PII pii = new PII(prefs);
            var result = pii.runOne(c, rng);
            if (!result.snd())
                return prefs;
        }
    }

    public static void main(String[] args) {
        Random rng = new Random(1);
        Prefs nonTerminating = findNonTerminating(rng, 3, 50);
        System.out.println(nonTerminating);
    }
}
