import java.util.List;
import java.util.Map;
import java.util.Set;

class Main {
    // Initial matching in the example from the slides
    static final PII slidesExample;
    static {
        int[][] malePrefs = {
            { 4, 2, 3, 1 },
            { 3, 1, 2, 4 },
            { 2, 4, 1, 3 },
            { 1, 4, 3, 2 }
        };
        int[][] femalePrefs = {
            { 1, 4, 2, 3 },
            { 1, 2, 3, 4 },
            { 4, 2, 3, 1 },
            { 3, 1, 4, 2 }
        };
        slidesExample = new PII(new Prefs(malePrefs, femalePrefs));
    }

    // Jeffrey's example that we worked out together
    static final PII jeffreyExample;
    static {
        int[][] malePrefs = {
            { 3, 4, 5, 1, 6, 2 },
            { 1, 5, 2, 6, 4, 3 },
            { 6, 1, 4, 5, 2, 3 },
            { 5, 6, 1, 3, 2, 4 },
            { 2, 4, 5, 1, 6, 3 },
            { 1, 3, 2, 5, 4, 6 }
        };
        int[][] femalePrefs = {
            { 2, 4, 5, 1, 6, 3 },
            { 2, 5, 3, 6, 1, 4 },
            { 1, 2, 6, 5, 4, 3 },
            { 2, 4, 1, 6, 5, 3 },
            { 6, 5, 4, 2, 3, 1 },
            { 3, 5, 1, 6, 2, 4 }
        };
        jeffreyExample = new PII(new Prefs(malePrefs, femalePrefs));
    }

    // Matthew's example
    static final PII matthewExample;
    static {
        int[][] malePrefs = {
            {  2,  4, 10,  9,  5,  3,  8,  6,  1,  7 },
            {  2, 10,  5,  8,  7,  4,  9,  1,  6,  3 },
            {  1,  2,  9,  6,  8,  5, 10,  7,  3,  4 },
            {  2,  4,  5,  8,  9, 10,  3,  6,  7,  1 },
            {  6, 10,  7,  4,  3,  1,  9,  8,  2,  5 },
            {  9,  2, 10,  6,  4,  1,  7,  5,  8,  3 },
            {  6,  5,  8,  3,  9,  7,  1, 10,  2,  4 },
            {  4,  7,  8,  9,  3, 10,  5,  2,  6,  1 },
            {  9,  3,  4,  1,  6,  7, 10,  8,  5,  2 },
            {  9, 10,  1,  5,  6,  3,  2,  8,  4,  7 }
        };
        int[][] femalePrefs = {
            {  5, 10,  2,  6,  3,  1,  8,  4,  9,  7 },
            {  1, 10,  9,  2,  7,  4,  3,  5,  8,  6 },
            {  5,  8,  7,  3,  4,  9,  2,  6, 10,  1 },
            {  6,  1,  8,  4, 10,  3,  7,  9,  2,  5 },
            {  4,  3,  2,  1,  5,  6,  7,  9, 10,  8 },
            {  8,  2,  9,  5,  1,  4,  6,  3,  7, 10 },
            {  4,  2,  7,  1,  8,  5, 10,  6,  3,  9 },
            {  9,  6,  3, 10,  8,  2,  4,  5,  7,  1 },
            {  3,  2,  6,  5,  4,  1,  8,  7,  9, 10 },
            {  8,  1,  4,  3,  9,  2,  7, 10,  6,  5 }
        };
        matthewExample = new PII(new Prefs(malePrefs, femalePrefs));
    }

    public static void main(String[] args) {
        PII pii = matthewExample;

        // Random rng = new Random(5);
        // Permutation mensMatches = pii.initiationPhase(rng);

        // Permutation mensMatches = new Permutation(new int[]{ 0, 3, 2, 1 });
        // Permutation mensMatches = Permutation.identity(6);
        Permutation mensMatches = new Permutation(new int[]{ 1, 7, 3, 0, 5, 6, 9, 8, 4, 2 });
        System.out.println("initiationPhase mensMatches: " + mensMatches);
        
        List<Index> nm1GeneratingPairs = pii.nm1GeneratingPairs(mensMatches);
        System.out.print("nm1 generating pairs: ");
        for (Index index : nm1GeneratingPairs)
            System.out.print(index + " ");
        System.out.println();

        List<Index> nm1Pairs = pii.nm1Pairs(mensMatches);
        System.out.print("nm1 pairs: ");
        for (Index index : nm1Pairs)
            System.out.print(index + " ");
        System.out.println();

        Map<Index, Index> nm2GeneratingPairs = pii.nm2GeneratingPairs(mensMatches);
        System.out.print("nm2 generating pairs: ");
        for (var e : nm2GeneratingPairs.entrySet())
            System.out.print(e.getKey() + ": " + e.getValue() + " ");
        System.out.println();

        PII.IndexForMatchingPair[] nm2GeneratingPairsAssociatedWithMatchingPairs = 
            pii.nm2GeneratingPairsAssociatedWithMatchingPair(mensMatches);
        System.out.print("matching pairs to associated nm2 generating pairs: ");
        for (int i = 0; i < pii.prefs.n(); i++)
            System.out.print(i + ": " + nm2GeneratingPairsAssociatedWithMatchingPairs[i] + ", ");
        System.out.println();

        Map<Index, PII.Edges> nm2GeneratingGraph = pii.nm2GeneratingGraph(mensMatches);
        System.out.print("nm2-generating graph: ");
        for (var e : nm2GeneratingGraph.entrySet())
            System.out.print(e.getKey() + ": " + e.getValue() + " ");
        System.out.println();

        Set<Index> nm2Pairs = pii.nm2Pairs(mensMatches);
        System.out.print("nm2-pairs: ");
        for (Index nm2Pair : nm2Pairs)
            System.out.print(nm2Pair + " ");
        System.out.println();

        Permutation nextMensMatches = pii.iterationPhase(mensMatches);
        System.out.println("iterationPhase mensMatches: " + nextMensMatches);
    }
}
