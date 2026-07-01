import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

/** A permutation on [0, n) */
class Permutation {
    final int[] perm;
    final int[] inversePerm;

    // TODO encapsulate, worry about invariant, etc.
    /** Record constructor, no checks */
    Permutation(int[] perm, int[] inversePerm) {
        this.perm = perm;
        this.inversePerm = inversePerm;
    }

    static Permutation allUnmatched(int n) {
        return new Permutation(CPIISlow.allUnmatched(n), CPIISlow.allUnmatched(n));
    }
    
    Permutation(int[] perm) {
        this.perm = perm;

        checkPermInv(perm);

        this.inversePerm = new int[perm.length];
        for (int i = 0; i < perm.length; i++)
            inversePerm[perm[i]] = i;
    }

    private static void checkPermInv(int[] perm) {
        // make sure it's a bijection
        boolean[] present = new boolean[perm.length];
        for (int y : perm) {
            if (y < 0 || y >= perm.length)
                throw new RuntimeException("Unexpected element " + y);
            if (present[y]) 
                throw new RuntimeException("Duplicate " + y);
            present[y] = true;
        }
        for (int y = 0; y < perm.length; y++)
            if (!present[y])
                throw new RuntimeException("Missing " + y);
    }
    
    /** `n` */
    int size() {
        return perm.length;
    }

    int get(int index) {
        return perm[index];
    }

    int getInverse(int index) {
        return inversePerm[index];
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

    @Override
    public int hashCode() {
        return Arrays.hashCode(perm);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Permutation other && Arrays.equals(perm, other.perm);
    }

    /** The identity permutation on `n` elements */
    static Permutation identity(int n) {
        int[] out = new int[n];
        for (int i = 0; i < n; i++)
            out[i] = i; // 0-indexed
        return new Permutation(out);
    }

    /** Compose `this(other)`. Throws if the sizes don't match. */
    Permutation compose(Permutation other) {
        int n = size();
        if (other.size() != n)
            throw new RuntimeException("Sizes don't match in `Permutation.compose`");
        
        int[] out = new int[n];
        for (int i = 0; i < n; i++)
            out[i] = perm[other.perm[i]];
        return new Permutation(out);
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    /** The next permutation in the left-to-right lexicographic ordering, returning `null` once the end is reached.
     */
    private Permutation next() {
        int n = size();
        // smallest `i` such that everything after that index is decreasing. -1 if the permutation is decreasing everywhere
        int i = n - 2;
        for (; i >= 0; i--)
            if (perm[i] < perm[i + 1])
                break;
        if (i < 0) return null;
        
        int[] nextPerm = Arrays.copyOf(perm, n);
        
        // find largest index > swapIdx with a larger value
        int j = n - 1;
        for (; j > i; j--)
            if (perm[j] > perm[i])
                break;
        assert j > i;

        swap(nextPerm, i, j);

        // reverse everything after i
        for (int k = i + 1, l = n - 1; k < l; k++, l--)
            swap(nextPerm, k, l);

        return new Permutation(nextPerm);
    }

    /** Return a finite stream of length `n!` of all permutations */
    static Stream<Permutation> all(int n) {
        return Stream.iterate(identity(n), Permutation::next).takeWhile(Objects::nonNull);
    }
}