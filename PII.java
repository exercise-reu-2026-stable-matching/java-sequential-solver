import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

class PII {
    /** 
     * `malePrefs[y][x]` is male `y`'s ranking of woman `x`, a.k.a. the left value at index yx in the 
     * ranking matrix in the original PII paper.
     */
    final int[][] malePrefs;
    /** 
     * `femalePrefs[x][y]` is female `x`'s ranking of man `y`, a.k.a. the right value at index yx in the 
     * *transpose* of the ranking matrix in the original PII paper.
     */
    final int[][] femalePrefs;
    final int n;

    PII(int[][] malePrefs, int[][] femalePrefs) {
        this.malePrefs = malePrefs;
        this.femalePrefs = femalePrefs;
        this.n = malePrefs.length;
    }

    static class Permutation {
        private int[] perm;
        
        Permutation(int[] fn) {
            this.perm = fn;
        }
        
        int size() {
            return perm.length;
        }

        int get(int index) {
            return perm[index];
        }

        Permutation invert() {
            int[] out = new int[size()];
            for (int i = 0; i < out.length; i++)
                out[perm[i]] = i;
            return new Permutation(out);
        }

        static Permutation random(Random rng, int n) {
            int[] out = new int[n];
            // Fisher-Yates shuffle
            for (int i = 0; i < n; i++)
                out[i] = i;
            for (int i = n - 1; i >= 1; i--) {
                int j = rng.nextInt(i + 1);
                int tmp = out[i];
                out[i] = out[j];
                out[j] = tmp;
            }
            return new Permutation(out);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("{");
            for (int i = 0; i < size(); i++) {
                sb.append(i + ": " + perm[i]);
                if (i != size() - 1)
                    sb.append(", ");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    Permutation initiationPhase(Random rng) {
        return Permutation.random(rng, n);
    }

    /** 2D index into an `n` x `n` array */
    static record Index(int y, int x) {
        @Override 
        public String toString() {
            return "(" + y + ", " + x + ")";
        }
    }

    boolean isUnstable(Permutation mensMatches, Permutation womensMatches, int y, int x) {
        int matchedWoman = womensMatches.get(y);
        int matchedMan = mensMatches.get(x);
        // Do y and x prefer to cheat with each other
        return (malePrefs[y][x] < malePrefs[y][matchedWoman] && femalePrefs[x][y] < femalePrefs[x][matchedMan]);
    }

    List<Index> nm1GeneratingPairs(Permutation mensMatches) {
        Permutation womensMatches = mensMatches.invert();
        List<Index> out = new ArrayList<>();
        for (int y = 0; y < n; y++) { // men
            final int[] row = malePrefs[y];
            int idxOfMinLeftValue = -1;
            for (int x = 0; x < n; x++) { // women
                if (isUnstable(mensMatches, womensMatches, y, x) && (idxOfMinLeftValue == -1 || row[x] < row[idxOfMinLeftValue]))
                    idxOfMinLeftValue = x;
            }
            if (idxOfMinLeftValue != -1)
                out.add(new Index(y, idxOfMinLeftValue));
        }
        return out;
    }

    List<Index> nm1Pairs(Permutation mensMatches) {
        List<Index> nm1GeneratingPairs = nm1GeneratingPairs(mensMatches);
        // this is pretty inefficient but it's easy to understand
        Map<Integer, Integer> generatingRowsOfCols = new HashMap<>();
        for (Index i : nm1GeneratingPairs) {
            int y2 = generatingRowsOfCols.getOrDefault(i.x(), -1);
            if (y2 == -1 || femalePrefs[i.x()][i.y()] < femalePrefs[i.x()][y2])
                generatingRowsOfCols.put(i.x(), i.y());
        }
        List<Index> out = new ArrayList<>();
        for (var e : generatingRowsOfCols.entrySet())
            out.add(new Index(e.getValue(), e.getKey()));
        return out;
    }

    /** Return a map from an nm1-pair to its corresponding nm2-generating pair. I think this map is injective (TODO?) */
    Map<Index, Index> nm2GeneratingPairs(Permutation mensMatches) {
        List<Index> nm1Pairs = nm1Pairs(mensMatches);
        Map<Index, Index> out = new HashMap<>();
        Permutation womensMatches = mensMatches.invert();
        for (Index pair : nm1Pairs) {
            int i = pair.y();
            int j = pair.x();
            int k = mensMatches.get(i);
            int l = womensMatches.get(j);
            out.put(pair, new Index(l, k));
        }
        return out;
    }

    static <K, V> Map<V, K> invertMap(Map<K, V> map) {
        Map<V, K> out = new HashMap<>();
        for (var e : map.entrySet()) {
            if (out.containsKey(e.getValue()))
                throw new RuntimeException("Duplicate in `invertMap`");
            out.put(e.getValue(), e.getKey());
        }
        return out;
    }

    /** Return a length-`n` array where the value at index `i` represents all the 
     * nm2-generating pairs `a_{l, k}` associated with matching pair `a_{i, k}` and `a_{l, j}`.
     * There are at most two of these values per set (TODO confirm this). 
     */
    Set<Index>[] nm2GeneratingPairsAssociatedWithMatchingPair(Permutation mensMatches) {
        Set<Index>[] out = new Set[n];
        for (int i = 0; i < n; i++)
            out[i] = new HashSet<>();
        
        Map<Index, Index> nm2GeneratingPairs = nm2GeneratingPairs(mensMatches);
        for (var e : nm2GeneratingPairs.entrySet()) {
            Index nm1Pair = e.getKey(); // a_{i, j}
            Index nm2GeneratingPair = e.getValue(); // a_{l, k}
            int i = nm1Pair.y();
            int j = nm1Pair.x();
            int l = nm2GeneratingPair.y();
            int k = nm2GeneratingPair.x();

            assert mensMatches.get(i) == k;
            assert mensMatches.get(l) == j;

            out[i].add(nm2GeneratingPair);
            out[l].add(nm2GeneratingPair);
        }

        return out; // TODO maybe this can be a list, idk about duplication though
    }

    /** Return an adjacency list for each nm2-generating pair in the nm2-generating graph G_M.
     * The degree of each vertex is at most two.
     */
    // Use sets so that two couples cheating on each other with each other don't have duplicate edges
    Map<Index, Set<Index>> nm2GeneratingGraph(Permutation mensMatches) {
        Map<Index, Set<Index>> out = new HashMap<>();
        Set<Index>[] nm2GeneratingPairsAssociatedWithMatchingPairs = nm2GeneratingPairsAssociatedWithMatchingPair(mensMatches);
        for (int y = 0; y < n; y++) { // men
            Index[] nm2GeneratingPairs = nm2GeneratingPairsAssociatedWithMatchingPairs[y].toArray(new Index[0]);
            assert nm2GeneratingPairs.length <= 2;
            for (Index u : nm2GeneratingPairs) // Make sure even singleton chains have a key in `out`
                out.putIfAbsent(u, new HashSet<>());
            if (nm2GeneratingPairs.length == 2) {
                // u and v have a common matching pair `(y, mensMatches.get(y))`
                Index u = nm2GeneratingPairs[0];
                Index v = nm2GeneratingPairs[1];
                out.get(u).add(v);
                out.get(v).add(u);
            }
        }
        return out;
    }

    Permutation iterationPhase(Permutation mensMatches) {
        throw new RuntimeException("Not implemented yet"); // TODO
    }

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
        slidesExample = new PII(malePrefs, femalePrefs);
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
        jeffreyExample = new PII(malePrefs, femalePrefs);
    }

    public static void main(String[] args) {
        PII pii = jeffreyExample;

        // Random rng = new Random(5);
        // Permutation mensMatches = pii.initiationPhase(rng);

        // Permutation mensMatches = new Permutation(new int[]{ 0, 3, 2, 1 });
        Permutation mensMatches = new Permutation(new int[]{ 0, 1, 2, 3, 4, 5 });
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

        Set<Index>[] nm2GeneratingPairsAssociatedWithMatchingPairs = 
            pii.nm2GeneratingPairsAssociatedWithMatchingPair(mensMatches);
        System.out.print("matching pairs to associated nm2 generating pairs: ");
        for (int i = 0; i < pii.n; i++)
            System.out.print(i + ": " + nm2GeneratingPairsAssociatedWithMatchingPairs[i] + ", ");
        System.out.println();

        Map<Index, Set<Index>> nm2GeneratingGraph = pii.nm2GeneratingGraph(mensMatches);
        System.out.print("nm2-generating graph: ");
        for (var e : nm2GeneratingGraph.entrySet())
            System.out.print(e.getKey() + ": " + e.getValue() + " ");
        System.out.println();
    }

}
