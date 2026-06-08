import java.util.Random;

class Permutation {
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