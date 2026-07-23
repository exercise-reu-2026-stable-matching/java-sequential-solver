import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Main {
    /** Run CPII male-first and female-first on a bunch of random inputs and record state data
     * and how long each takes to converge. */
    public static void main(String[] args) throws IOException {
        final int nSize  = Integer.parseInt(args[0]);
        final int nTrials = Integer.parseInt(args[1]);
        final String writeFile = args[2];
        final int programIndex = args.length >= 4 ? Integer.parseInt(args[3]) : 0;
        
        final Random rng = new Random(programIndex);
        final Permutation initial = Permutation.allUnmatched(nSize);
        final int maxStateBuffer = 100000;

        List<TrialStateData> trialStateDataList = new ArrayList<>();
        List<IterationStateData> mfIterationStateDataList = new ArrayList<>();
        List<IterationStateData> fmIterationStateDataList = new ArrayList<>();

        CPIIDataCollection cpiiMF = new CPIIDataCollection(nSize, mfIterationStateDataList, false);
        CPIIDataCollection cpiiFM = new CPIIDataCollection(nSize, fmIterationStateDataList, true);

        try (
            PrintWriter trialWriter = new PrintWriter(writeFile + "_trial.csv");
            PrintWriter iterMFWriter = new PrintWriter(writeFile + "_iterMF.csv");
            PrintWriter iterFMWriter = new PrintWriter(writeFile + "_iterFM.csv");
        ) {
            TrialStateData.writeCSVHeaders(trialWriter, nSize);
            IterationStateData.writeCSVHeaders(iterMFWriter);
            IterationStateData.writeCSVHeaders(iterFMWriter);

            for (int trial = 0; trial < nTrials; trial++) {
                final Prefs prefs = Prefs.random(rng, nSize);
                final Prefs swappedPrefs = prefs.maleFemaleSwap();

                // System.out.println(prefs);
                // System.out.println("\n");

                int mfIterations = cpiiMF.countIters(prefs, initial, trial);
                int fmIterations = cpiiFM.countIters(swappedPrefs, initial, trial);

                TrialStateData trialStateData = new TrialStateData(trial, prefs, mfIterations, fmIterations);
                trialStateDataList.add(trialStateData);

                TrialStateData.bufferWriteCSV(trialStateDataList, trialWriter, programIndex, maxStateBuffer);
                IterationStateData.bufferWriteCSV(mfIterationStateDataList, iterMFWriter, programIndex, maxStateBuffer);
                IterationStateData.bufferWriteCSV(fmIterationStateDataList, iterFMWriter, programIndex, maxStateBuffer);
            }
            TrialStateData.bufferWriteCSV(trialStateDataList, trialWriter, programIndex, 1);
            IterationStateData.bufferWriteCSV(mfIterationStateDataList, iterMFWriter, programIndex, 1);
            IterationStateData.bufferWriteCSV(fmIterationStateDataList, iterFMWriter, programIndex, 1);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
            // throw new RuntimeException("File " + writeFile + "_{trial|iterMF|iterFM}.csv" + " not found!");
        }
    }
}
