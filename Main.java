import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Random;

class Main {
    private static void printIterationCounts(int nSamples, int nSize, Random rng) throws IOException {
        final CPII cpii = new CPII();
        final Permutation empty = Permutation.allUnmatched(nSize);
        try (BufferedOutputStream out = new BufferedOutputStream(System.out)) { // don't flush every time
            for (int i = 0; i < nSamples; i++) {
                Prefs prefs = Prefs.random(rng, nSize);
                var result = cpii.runOrCycle(prefs, empty);
                assert result.fst().isPresent(); // CPII should always converge
                int nCycles = result.snd();
                assert nCycles <= nSize * nSize;
                out.write((nCycles + "\n").getBytes());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final int nSamples = Integer.parseInt(args[0]);
        final int nSize = Integer.parseInt(args[1]);
        final Random rng = new Random(1);
        
        printIterationCounts(nSamples, nSize, rng);
    }
}
