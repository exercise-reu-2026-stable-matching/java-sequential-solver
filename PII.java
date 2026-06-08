import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

class PII extends PIIBase {
    PII(int[][] malePrefs, int[][] femalePrefs) {
        super(malePrefs, femalePrefs);
    }

    List<Index> nm1GeneratingPairs(Permutation mensMatches) {
        checkPermLength(mensMatches);

        List<Index> out = new ArrayList<>();
        for (int y = 0; y < n; y++) { // men
            final int[] row = malePrefs[y];
            int idxOfMinLeftValue = -1;
            for (int x = 0; x < n; x++) { // women
                if (isUnstable(mensMatches, y, x) && (idxOfMinLeftValue == -1 || row[x] < row[idxOfMinLeftValue]))
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
        for (Index pair : nm1Pairs) {
            int i = pair.y();
            int j = pair.x();
            int k = mensMatches.get(i);
            int l = mensMatches.getInverse(j); // woman -> man
            out.put(pair, new Index(l, k));
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

    Set<Index> nmPairs(Permutation mensMatches) {
        List<Index> nm1Pairs = nm1Pairs(mensMatches);
        Set<Index> nm2Pairs = nm2Pairs(mensMatches);
        
        boolean[] inRow = new boolean[n];
        boolean[] inCol = new boolean[n];
        for (Index i : nm1Pairs) { 
            // should be disjoint
            assert !nm2Pairs.contains(i);
            
            // each row/column contains at most one pair
            assert !inRow[i.y()];
            assert !inCol[i.x()];
            inRow[i.y()] = true;
            inCol[i.x()] = true;
        }
        
        nm2Pairs.addAll(nm1Pairs);
        return nm2Pairs;
    }

    @Override
    Permutation iterationPhase(Permutation mensMatches) {
        checkPermLength(mensMatches);

        int[] perm = new int[n];
        Arrays.fill(perm, -1);
        for (Index nmPair : nmPairs(mensMatches))
            perm[nmPair.y()] = nmPair.x();
        for (int y = 0; y < n; y++)
            if (perm[y] == -1)
                perm[y] = mensMatches.get(y); // the original

        // check still injective
        for (int y1 = 0; y1 < n; y1++) for (int y2 = 0; y2 < n; y2++)
            assert !(y1 != y2 && perm[y1] == perm[y2]);

        return new Permutation(perm);
    }
}
