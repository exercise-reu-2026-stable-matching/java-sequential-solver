import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Random;

class Main {
    /** Run CPII on a bunch of random inputs and spit out how long each takes to converge */
    public static void main(String[] args) throws IOException {
        final Random rng = new Random(1);
        final int nSize  = Integer.parseInt(args[0]);
        final int nSamples = Integer.parseInt(args[1]);
        final Permutation initial = Permutation.allUnmatched(nSize);

        // Buffer to avoid overhead of System.out.println each time
        try (BufferedOutputStream out = new BufferedOutputStream(System.out)) {
            for (int i = 0; i < nSamples; i++) {
                Prefs prefs = Prefs.random(rng, nSize);
                FastCPII cpii = new FastCPII(prefs);
                var result = cpii.runOrCycle(prefs, initial);
                assert result.fst().isPresent();
                int itersToConverge = result.snd();
                out.write((itersToConverge + "\n").getBytes());
            }
        }
    }
}
