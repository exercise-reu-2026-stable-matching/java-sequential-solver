import java.util.Random;

/** A permutation on [0, n) */
class Permutation {
    private final int[] perm;
    private final int[] inversePerm;
    
    Permutation(int[] perm) {
        this.perm = perm;

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

        this.inversePerm = new int[perm.length];
        for (int i = 0; i < perm.length; i++)
            inversePerm[perm[i]] = i;
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
}