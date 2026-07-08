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
        for (int i = 0; i < nSamples; i++) {
            System.err.println("Sample " + i);
            final Prefs prefs = Prefs.random(rng, nSize);

            CPII fast = new CPIIFast(nSize);
            // int itersToConvergeFast = 
            fast.countIters(prefs, initial);
            // out.write((itersToConvergeFast + "\n").getBytes());
        }
        CPII.out.flush();
    }
}
