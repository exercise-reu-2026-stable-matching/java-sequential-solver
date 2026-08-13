/** The recurrence b_1 := 1, b_2 := 1/e, b_(i+1) := b_i^(1/(1 - b_i)) that we believe is
 * an upper bound for E[|B_i|]/n
 */

public class Recurrence {
    public static void main(String[] args) {
        final int iters = Integer.parseInt(args[0]);

        double sum = 0.0;
        double b;
        
        if (iters <= 0) return;

        b = 1.0;
        sum += b;
        // System.out.println(sum);
        if (iters == 1) return;

        b = Math.exp(-1.0); // 1 / e
        sum += b;
        // System.out.println(sum);
        if (iters == 2) return;

        for (int i = 3; i <= iters; i++) {
            b = Math.pow(b, 1.0 / (1 - b));
            assert b > 1.0 / (2.0 * i * Math.log(i));
            sum += b;
            // System.out.println(sum);
        }
        System.out.println(b);
        double bound = 1.0 / (2.0 * iters * Math.log(iters));
        System.out.println(bound);
        System.out.println(sum);
    }
}