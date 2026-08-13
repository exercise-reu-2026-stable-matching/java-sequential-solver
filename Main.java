import java.io.IOException;
import java.util.Random;

class Main {
    /** Run CPII on a bunch of random inputs and spit out how long each takes to converge */
    public static void main(String[] args) throws IOException {
        final int seed = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
        final Random rng = new Random(seed);
        final int nSize  = Integer.parseInt(args[0]);
        final int nSamples = Integer.parseInt(args[1]);
        final Permutation initial = Permutation.allUnmatched(nSize);

        for (int i = 0; i < nSamples; i++) {
            if (i % 100 == 0)
                System.err.println("Sample " + i);
            final Prefs prefs = Prefs.random(rng, nSize);

            CPII fast = new CPIIFast(nSize);
            int itersToConvergeFast = fast.countIters(prefs, initial);
            System.out.println(itersToConvergeFast);
        }
        // CPII.out.flush();
    }
}
