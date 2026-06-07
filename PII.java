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

    void checkPermLength(Permutation mensMatches) {
        if (mensMatches.size() != n)
            throw new RuntimeException("Wrong size for permutation");
    }

    List<Index> nm1GeneratingPairs(Permutation mensMatches) {
        checkPermLength(mensMatches);

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
        checkPermLength(mensMatches);

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
        checkPermLength(mensMatches);

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

    // For some matching pair (not in this class), two points that each share the row and column of the matching pair, respectively
    static class IndexForMatchingPair {
        Index inRow, inCol;

        IndexForMatchingPair() {
            inRow = inCol = null;
        }

        @Override
        public String toString() {
            return "(" + inRow + ", " + inCol + ")";
        }
    }

    /** Return a length-`n` array. For every matching pair `a_{y, x}`, puts any nm2-generating pairs `a_{y, w}` and/or `a_{z, x}`
     * at index `y`. Therefore, there are at most two of these values per set. */
    // Equivalently, for every nm2-generating pair `a_{l, k}`, there are two matching pairs `a_{i, k}` and `a_{l, j}`,
    // where `i` is the man matched with woman `k` and `j` is the woman matched with man `l`.
    // We put `a_{l, k}` at both indices `i` and `l` (the men).
    IndexForMatchingPair[] nm2GeneratingPairsAssociatedWithMatchingPair(Permutation mensMatches) {
        checkPermLength(mensMatches);

        IndexForMatchingPair[] out = new IndexForMatchingPair[n];
        for (int i = 0; i < n; i++)
            out[i] = new IndexForMatchingPair();
        
        Map<Index, Index> nm2GeneratingPairs = nm2GeneratingPairs(mensMatches);
        for (var e : nm2GeneratingPairs.entrySet()) {
            Index nm1Pair = e.getKey(); // a_{i, j}
            Index nm2GeneratingPair = e.getValue(); // a_{l, k}
            int i = nm1Pair.y();
            int j = nm1Pair.x();
            int l = nm2GeneratingPair.y();
            int k = nm2GeneratingPair.x();

            assert mensMatches.get(i) == k; // a_{i, k} is a matching pair
            assert mensMatches.get(l) == j; // a_{l, j} is a matching pair

            out[i].inCol = nm2GeneratingPair;
            out[l].inRow = nm2GeneratingPair;
        }

        return out;
    }

    /** Both fields can be `null` */
    static class Edges {
        Index rPtr, cPtr;

        @Override
        public String toString() {
            return "(" + rPtr + ", " + cPtr + ")";
        }
    }

    /** Return the row-pointer and col-pointer of nm2-generating pairs of a given pair in the nm2-generating graph G_M.
     * The degree of each vertex is at most two.
     * The row-pointer is the nm2-generating pair found by traveling *horizontally* from the current nm2-generating pair 
     * to the matching pair associated with both vertices, then traveling vertically.
     * The col-pointer is defined similarly but with the order switched.
     * Singleton nodes are still present in the keys of the returned map. Values are never null (but the fields may be)
     */
    Map<Index, Edges> nm2GeneratingGraph(Permutation mensMatches) {
        checkPermLength(mensMatches);

        Map<Index, Edges> out = new HashMap<>();
        IndexForMatchingPair[] nm2GeneratingPairsAssociatedWithMatchingPairs = nm2GeneratingPairsAssociatedWithMatchingPair(mensMatches);
        for (int y = 0; y < n; y++) { // men
            IndexForMatchingPair nm2GeneratingPairs = nm2GeneratingPairsAssociatedWithMatchingPairs[y];
            Index inCol = nm2GeneratingPairs.inCol;
            Index inRow = nm2GeneratingPairs.inRow;
            if (inRow != null) out.putIfAbsent(inRow, new Edges());
            if (inCol != null) out.putIfAbsent(inCol, new Edges());
            if (inRow != null && inCol != null) {
                // node in common
                out.get(inRow).rPtr = inCol;
                out.get(inCol).cPtr = inRow;
            }
        }
        return out;
    }

    /** Returns the nm2-pair from the row end and column end of a chain; otherwise `null` for cycles */
    Index nm2PairFromChain(Permutation mensMatches, Index start) {
        Map<Index, Edges> edges = nm2GeneratingGraph(mensMatches);

        Index rEnd = null;
        for (Index curr = start; ; ) {
            Index next = edges.get(curr).rPtr;
            if (next == null) {
                // we have found the r-end
                rEnd = curr;
                break;
            } else if (next.equals(start))
                // cycle
                return null;
            else
                // keep going
                curr = next;
        }
        Index cEnd = null;
        for (Index curr = start; ; ) {
            Index next = edges.get(curr).cPtr;
            if (next == null) {
                // reached col end
                cEnd = curr;
                break;
            } else if (next.equals(start)) {
                // cycle (shouldn't ever happen but whatever)
                assert false; // unreachable
                return null;
            } else { 
                // continue
                curr = next;
            }
        }
        assert rEnd != null;
        assert cEnd != null;
        return new Index(rEnd.y(), cEnd.x());
    }

    Set<Index> nm2Pairs(Permutation mensMatches) {
        Map<Index, Edges> nm2GeneratingGraph = nm2GeneratingGraph(mensMatches);
        Set<Index> out = new HashSet<>();

        // This does a ton of extra work TODO
        for (Index nm2GeneratingPair : nm2GeneratingGraph.keySet()) {
            Index nm2Pair = nm2PairFromChain(mensMatches, nm2GeneratingPair);
            if (nm2Pair != null)
                out.add(nm2Pair);
        }
        return out;
    }

    Permutation iterationPhase(Permutation mensMatches) {
        checkPermLength(mensMatches);

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

        IndexForMatchingPair[] nm2GeneratingPairsAssociatedWithMatchingPairs = 
            pii.nm2GeneratingPairsAssociatedWithMatchingPair(mensMatches);
        System.out.print("matching pairs to associated nm2 generating pairs: ");
        for (int i = 0; i < pii.n; i++)
            System.out.print(i + ": " + nm2GeneratingPairsAssociatedWithMatchingPairs[i] + ", ");
        System.out.println();

        Map<Index, Edges> nm2GeneratingGraph = pii.nm2GeneratingGraph(mensMatches);
        System.out.print("nm2-generating graph: ");
        for (var e : nm2GeneratingGraph.entrySet())
            System.out.print(e.getKey() + ": " + e.getValue() + " ");
        System.out.println();

        Set<Index> nm2Pairs = pii.nm2Pairs(mensMatches);
        System.out.print("nm2-pairs: ");
        for (Index nm2Pair : nm2Pairs)
            System.out.print(nm2Pair + " ");
        System.out.println();
    }

}
