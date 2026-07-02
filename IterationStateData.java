import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class IterationStateData {
    public int trialIndex; // TODO: Make sure this is set in run iteration
    private final int iterationIndex;

    private List<Index> matchingPairs;
    private List<Index> unstablePairs;
    private List<Index> nm1Pairs;
    private List<Index> nm2GenPairs;
    private List<Index> nm2Pairs;

    public int numEdges, numSingletons, numChains, numCycles;
    public float avgChainLength, avgCycleLength;

    public IterationStateData(int trialIndex, int iterationIndex) {
        this.trialIndex = trialIndex;
        this.iterationIndex = iterationIndex;
    }

    public String toCSVString(int programIndex) {
        StringBuilder builder = new StringBuilder();

        builder.append(programIndex).append(",");
        builder.append(trialIndex).append(",");
        builder.append(iterationIndex).append(",");
        
        builder.append(unstablePairs.size()).append(",");
        builder.append(nm1Pairs.size()).append(",");
        builder.append(nm2Pairs.size()).append(",");

        builder.append(numEdges).append(",");
        builder.append(numSingletons).append(",");
        builder.append(numChains).append(",");
        builder.append(numCycles).append(",");
        builder.append(avgChainLength).append(",");
        builder.append(avgCycleLength).append(",");

        builder.append(pairsToCSVString(matchingPairs)).append(",");
        builder.append(pairsToCSVString(unstablePairs)).append(",");
        builder.append(pairsToCSVString(nm1Pairs)).append(",");
        builder.append(pairsToCSVString(nm2GenPairs)).append(",");
        builder.append(pairsToCSVString(nm2Pairs));

        return builder.toString();
    }

    public String pairsToCSVString(List<Index> pairList) {
        int n = matchingPairs.size();

        int[] flattenedIndices = new int[pairList.size()];

        for (int i = 0; i < pairList.size(); i++) {
            Index pairIndex = pairList.get(i);
            flattenedIndices[i] = pairIndex.y() * n + pairIndex.x();
        }

        return "\"" + Arrays.toString(flattenedIndices) + "\"";
    }

    public void addNM2Graph(Map<Index, PII.Edges> nm2Graph) {
        
        // Loop through all nodes and determine if they're row-end (list) or internal (set)
        List<Index> rowEnds = new ArrayList<>();
        Set<Index> internalNodes = new HashSet<>();

        for (Index node : nm2Graph.keySet()) {
            PII.Edges edges = nm2Graph.get(node);
            if (edges.rPtr == null) {
                rowEnds.add(node);
            }
            // rPtr isn't null, check if cPtr isn't null either
            else if (edges.cPtr != null) {
                internalNodes.add(node);
            } 
        }

        // Follow each row end to column end (marking singleton or chain), remove from internal set
        //   - Track avg chain length
        int chainLengthSum = 0;
        numSingletons = 0;
        numChains = 0;

        for (Index rowEnd : rowEnds) {
            Index start = rowEnd;
            Index next = nm2Graph.get(start).cPtr;

            int chainLength = 0;

            while (next != null) {
                internalNodes.remove(next);
                chainLength++;
                next = nm2Graph.get(next).cPtr;
            }

            if (chainLength == 0) {
                numSingletons++;
            }
            else {
                numChains++;
            }

            chainLengthSum += chainLength;
        }

        // Follow each internal node until we reach itself, removing all nodes along the way from internal set
        int cycleLengthSum = 0;
        numCycles = 0;

        while (!internalNodes.isEmpty()) {
            Index start = internalNodes.iterator().next();
            internalNodes.remove(start);

            Index next = nm2Graph.get(start).cPtr;

            int cycleLength = 1;
            do { 
                internalNodes.remove(next);
                next = nm2Graph.get(next).cPtr;
                cycleLength++;
            } while (next != start);

            numCycles++;
            cycleLengthSum += cycleLength;
        }

        avgChainLength = numChains != 0 ? (float)chainLengthSum / numChains : 0;
        avgCycleLength = numCycles != 0 ? (float)cycleLengthSum / numCycles : 0;

        numEdges = chainLengthSum + cycleLengthSum;
    }

    public void addMatching(Collection<Index> pairs) {
        matchingPairs = new ArrayList<>(pairs);
    }

    public void addUnstable(Collection<Index> pairs) {
        unstablePairs = new ArrayList<>(pairs);
    }

    public void addNM1(Collection<Index> pairs) {
        nm1Pairs = new ArrayList<>(pairs);
    }

    public void addNM2_gen(Collection<Index> pairs) {
        nm2GenPairs = new ArrayList<>(pairs);
    }

    public void addNM2(Collection<Index> pairs) {
        nm2Pairs = new ArrayList<>(pairs);
    }

    @Override
    public String toString() {
        return matchingPairs.toString() + "\n" + unstablePairs.toString() + "\n" + nm1Pairs.toString() + "\n" + nm2GenPairs.toString() + "\n" + nm2Pairs.toString() + "\n";
    }
}
