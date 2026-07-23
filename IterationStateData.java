import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class IterationStateData {
    private final int nSize;
    private final int trialIndex;
    private final int iterationIndex;
    private final boolean isFemaleMale;

    private List<Index> matchingPairs;
    private List<Index> unstablePairs;
    private List<Index> maleDominantPairs;
    private List<Index> femaleDominantPairs;
    private List<Index> maleFemaleDominantPairs;
    private List<Index> femaleMaleDominantPairs;

    public IterationStateData(int nSize, int trialIndex, int iterationIndex, boolean isFemaleMale) {
        this.nSize = nSize;
        this.trialIndex = trialIndex;
        this.iterationIndex = iterationIndex;
        this.isFemaleMale = isFemaleMale;
    }

    public static void writeCSVHeaders(PrintWriter writer) {
        StringBuilder builder = new StringBuilder();

        builder.append("programIndex,trialIndex,iterationIndex,");
        builder.append("matchIndices,unstableIndices,");
        builder.append("maleDominantIndices,femaleDominantIndices,");
        builder.append("maleFemaleDominantIndices,femaleMaleDominantIndices");

        writer.println(builder.toString());
    }

    public static void bufferWriteCSV(List<IterationStateData> dataList, PrintWriter writer, int programIndex, int maxBuffer) {
        if (dataList.size() >= maxBuffer) {
            // Write iteration CSV line for each state
            for (IterationStateData stateData : dataList) {
                writer.println(stateData.toCSVString(programIndex));
            }

            dataList.clear();
        }
    }

    public String toCSVString(int programIndex) {
        StringBuilder builder = new StringBuilder();

        builder.append(programIndex).append(",");
        builder.append(trialIndex).append(",");
        builder.append(iterationIndex).append(",");

        builder.append(pairsToCSVString(matchingPairs)).append(",");
        builder.append(pairsToCSVString(unstablePairs)).append(",");
        builder.append(pairsToCSVString(maleDominantPairs)).append(",");
        builder.append(pairsToCSVString(femaleDominantPairs)).append(",");
        builder.append(pairsToCSVString(maleFemaleDominantPairs)).append(",");
        builder.append(pairsToCSVString(femaleMaleDominantPairs));

        return builder.toString();
    }

    private String pairsToCSVString(List<Index> pairList) {
        int[] flattenedIndices = new int[pairList.size()];

        for (int i = 0; i < pairList.size(); i++) {
            Index pairIndex = pairList.get(i);
            flattenedIndices[i] = pairIndex.y() * nSize + pairIndex.x();
        }

        String commaSplitList = Arrays.stream(flattenedIndices)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining(","));
        return "\"[" + commaSplitList + "]\"";
    }

    public void addMatching(Collection<Index> pairs) {
        if (isFemaleMale) {
            matchingPairs = transposeIndices(pairs);
        }
        else {
            matchingPairs = new ArrayList<>(pairs);
        }
    }

    public void addUnstable(Collection<Index> pairs) {
        if (isFemaleMale) {
            unstablePairs = transposeIndices(pairs);
        }
        else {
            unstablePairs = new ArrayList<>(pairs);
        }
    }

    public void addMaleDominant(int[] permutation) {
        maleDominantPairs = permArrayToIndices(permutation, false);
    }

    public void addFemaleDominant(int[] permutation) {
        femaleDominantPairs = permArrayToIndices(permutation, true);
    }

    public void addMaleFemaleDominant(int[] permutation) {
        maleFemaleDominantPairs = permArrayToIndices(permutation, true);
    }

    public void addFemaleMaleDominant(int[] permutation) {
        femaleMaleDominantPairs = permArrayToIndices(permutation, false);
    }

    private List<Index> permArrayToIndices(int[] permutation, boolean inverse) {
        List<Index> indices = new ArrayList<>();

        for (int in = 0; in < permutation.length; in++) {
            int out = permutation[in];
            if (out != -1) {
                if (inverse) {
                    indices.add(new Index(out, in));
                }
                else {
                    indices.add(new Index(in, out));
                }
            }
        }

        return indices;
    }

    private static List<Index> transposeIndices(Collection<Index> pairs) {
        Index[] pairArray = new Index[pairs.size()];
        pairs.toArray(pairArray);
        for (int i = 0; i < pairArray.length; i++) {
            pairArray[i] = pairArray[i].transpose();
        }

        return Arrays.asList(pairArray);
    }

    @Override
    public String toString() {
        return matchingPairs.toString() + "\n" + unstablePairs.toString() + "\n" + maleDominantPairs.toString() + "\n" + femaleDominantPairs.toString() + "\n" + maleFemaleDominantPairs.toString() + "\n" + femaleMaleDominantPairs.toString() + "\n";
    }
}
