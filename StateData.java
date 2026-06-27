import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class StateData {
    private static record Means(
        List<Float> arithmetic, List<Float> geometric, Optional<List<Float>> relativeArithmetic
    ) {
        @Override
        public String toString() {
            return "\tAM: " + arithmetic + "\n\tGM: " + geometric + "\n\tRAM: " + relativeArithmetic;
        }

        public String toCSVString() {
            String relativeArithmeticStr = "";
            if (relativeArithmetic.isPresent()) {
                relativeArithmeticStr = ",\"" + relativeArithmetic.get().toString() + "\"";
            }

            return "\"" + arithmetic + "\"" + "," + "\"" + geometric + "\"" + relativeArithmeticStr + ",";
        }
    }

    public int trialIndex;
    public boolean converges;

    private final Prefs prefs;

    private List<Index> matchingPairs;
    private Permutation matchingPerm;
    private List<Index> unstablePairs;
    private List<Index> nm1Pairs;
    private List<Index> nm2GenPairs;
    private List<Index> nm2Pairs;

    public Means matchingMeans;
    public Means unstableMeans;
    public Means nm1Means;
    public Means nm2GenMeans;
    public Means nm2Means;

    public int numEdges, numSingletons, numChains, numCycles;
    public float avgChainLength, avgCycleLength;

    public void printStateData() {
        System.out.printf("Trial: %d\n", trialIndex);
        System.out.printf("n    numUnstable    numNM1    numNM2\n");
        System.out.printf(
            "%d   %d             %d        %d\n",
            matchingPairs.size(), unstablePairs.size(), nm1Pairs.size(), nm2Pairs.size()
        );

        System.out.printf("\n--- MEANS ---\n");
        System.out.printf("Matching:\n%s\n", matchingMeans);
        System.out.printf("Unstable:\n%s\n", unstableMeans);
        System.out.printf("NM1:\n%s\n", nm1Means);
        System.out.printf("NM2Gen:\n%s\n", nm2GenMeans);
        System.out.printf("NM2:\n%s\n", nm2Means);

        System.out.printf("\nnumEdges    numSingletons    numChains    numCycles    avgChainLength    avgCycleLength\n");
        System.out.printf(
            "%d          %d               %d           %d           %.2f              %.2f\n",
            numEdges, numSingletons, numChains, numCycles, avgChainLength, avgCycleLength
        );

        System.out.printf("\nConverges?  %B\n", converges);
    }

    public String toCSVString(int programIndex) {
        StringBuilder builder = new StringBuilder();
        builder.append(programIndex).append(",");
        builder.append(trialIndex).append(",");

        builder.append(unstablePairs.size()).append(",");
        builder.append(nm1Pairs.size()).append(",");
        builder.append(nm2Pairs.size()).append(",");

        builder.append(matchingMeans.toCSVString());
        builder.append(unstableMeans.toCSVString());
        builder.append(nm1Means.toCSVString());
        builder.append(nm2GenMeans.toCSVString());
        builder.append(nm2Means.toCSVString());

        builder.append(numEdges).append(",");
        builder.append(numSingletons).append(",");
        builder.append(numChains).append(",");
        builder.append(numCycles).append(",");
        builder.append(avgChainLength).append(",");
        builder.append(avgCycleLength).append(",");
        
        builder.append(converges ? 1 : 0);

        return builder.toString();
    }

    public StateData(Prefs prefs) {
        this.prefs = prefs;
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

    public void addMatching(Collection<Index> pairs, Permutation perm) {
        matchingPairs = new ArrayList<>(pairs);
        matchingPerm = perm;
        matchingMeans = getMeans(matchingPairs, true);
    }

    public void addUnstable(Collection<Index> pairs) {
        unstablePairs = new ArrayList<>(pairs);
        unstableMeans = getMeans(unstablePairs, false);
    }

    public void addNM1(Collection<Index> pairs) {
        nm1Pairs = new ArrayList<>(pairs);
        nm1Means = getMeans(nm1Pairs, false);
    }

    public void addNM2_gen(Collection<Index> pairs) {
        nm2GenPairs = new ArrayList<>(pairs);
        nm2GenMeans = getMeans(nm2GenPairs, false);
    }

    public void addNM2(Collection<Index> pairs) {
        nm2Pairs = new ArrayList<>(pairs);
        nm2Means = getMeans(nm2Pairs, false);
    }

    private Means getMeans(List<Index> pairs, boolean isMatching) {
        assert pairs != null;

        List<Float> arithmetic = arithmeticMeans(pairs);
        List<Float> geometric = geometricMeans(pairs);
        Optional<List<Float>> relativeArithmetic;
        
        if (isMatching) {
            relativeArithmetic = Optional.empty();
        }
        else {
            relativeArithmetic = Optional.of(relativeArithmeticMeans(pairs));
        }

        return new Means(arithmetic, geometric, relativeArithmetic);
    }

    private List<Float> arithmeticMeans(List<Index> pairs) {
        List<Float> out = new ArrayList<>();
        for (Index pair : pairs) {
            int maleRating = prefs.malePrefs(pair.y(), pair.x());
            int femaleRating = prefs.femalePrefs(pair.x(), pair.y());
            out.add((float)(maleRating + femaleRating) / 2);
        }
        out.sort(null);
        return out;
    }

    private List<Float> relativeArithmeticMeans(List<Index> pairs) {
        List<Float> out = new ArrayList<>();
        for (Index pair : pairs) {
            int maleRating = prefs.malePrefs(pair.y(), pair.x());
            int femaleRating = prefs.femalePrefs(pair.x(), pair.y());

            int colManWith = matchingPerm.get(pair.y());
            int rowWomanWith = matchingPerm.getInverse(pair.x());

            int maleCurrentRating = prefs.malePrefs(pair.y(), colManWith);
            int femaleCurrentRating = prefs.femalePrefs(pair.x(), rowWomanWith);

            out.add((float)(maleRating-maleCurrentRating + femaleRating-femaleCurrentRating) / 2);
        }
        out.sort(null);
        return out;
    }

    private List<Float> geometricMeans(List<Index> pairs) {
        List<Float> out = new ArrayList<>();
        for (Index pair : pairs) {
            int maleRating = prefs.malePrefs(pair.y(), pair.x());
            int femaleRating = prefs.femalePrefs(pair.x(), pair.y());
            out.add((float)Math.sqrt((double)maleRating * femaleRating));
        }
        out.sort(null);
        return out;
    }

    @Override
    public String toString() {
        return matchingPairs.toString() + "\n" + unstablePairs.toString() + "\n" + nm1Pairs.toString() + "\n" + nm2GenPairs.toString() + "\n" + nm2Pairs.toString() + "\n";
    }
}
