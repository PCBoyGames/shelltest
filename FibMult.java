import java.util.ArrayList;

public class FibMult {
    public static void main(String[] args) {
        int max = 100000;
        ArrayList<Integer> fibs = new ArrayList<>();
        for (int i = 0; ; i++) {
            int f = fib(i);
            if (f > 0 && f * fibs.get(fibs.size() - 1) > max) break;
            fibs.add(f);
        }
        ArrayList<Integer> products = new ArrayList<>();
        for (int i = 1; i < fibs.size(); i++) {
            int product = fibs.get(i) * fibs.get(i - 1);
            products.add(product);
        }
        System.out.println(products);
    }

    public static int fib(int n) {
        if (n <= 1) return n;
        return fib(n - 1) + fib(n - 2);
    }
}
