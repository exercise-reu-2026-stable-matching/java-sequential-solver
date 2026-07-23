import java.io.PrintWriter;
import java.util.List;

class TrialStateData {
    private final int trialIndex;
    private final Prefs prefs;
    private final int mfIterations;
    private final int fmIterations;

    public TrialStateData(int trialIndex, Prefs prefs, int mfIterations, int fmIterations) {
        this.trialIndex = trialIndex;
        this.prefs = prefs;
        this.mfIterations = mfIterations;
        this.fmIterations = fmIterations;
    }

    public static void writeCSVHeaders(PrintWriter writer, int nSize) {
        StringBuilder builder = new StringBuilder();
        builder.append("programIndex,trialIndex,");
        builder.append("mfIterations,fmIterations,");

        for (int i = 0; i < nSize * nSize; i++) {
            builder.append("l").append(i).append(",");
            builder.append("r").append(i).append(",");
        }

        builder.deleteCharAt(builder.length() - 1);

        writer.println(builder.toString());
    }

    public static void bufferWriteCSV(List<TrialStateData> dataList, PrintWriter writer, int programIndex, int maxBuffer) {
        if (dataList.size() >= maxBuffer) {
            // Write iteration CSV line for each state
            for (TrialStateData stateData : dataList) {
                writer.println(stateData.toCSVString(programIndex));
            }

            dataList.clear();
        }
    }

    public String toCSVString(int programIndex) {
        StringBuilder builder = new StringBuilder();

        builder.append(programIndex).append(",");
        builder.append(trialIndex).append(",");

        builder.append(mfIterations).append(",");
        builder.append(fmIterations).append(",");

        for (int y = 0; y < prefs.n(); y++) {
            for (int x = 0; x < prefs.n(); x++) {
                int left_val = prefs.malePrefs(y, x);
                int right_val = prefs.femalePrefs(x, y);
                builder.append(left_val).append(",");
                builder.append(right_val).append(",");
            }
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }
}
