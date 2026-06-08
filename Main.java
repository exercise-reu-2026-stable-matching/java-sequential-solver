import java.util.List;
import java.util.Map;
import java.util.Set;

class Main {
    public static void main(String[] args) {
        Pair<Prefs, Permutation> example = Examples.matthewExample;
        PII pii = new PII(example.fst());
        Permutation mensMatches = example.snd();

        // Random rng = new Random(5);
        // Permutation mensMatches = pii.initiationPhase(rng);
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
