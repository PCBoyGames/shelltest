import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;

public class FibMult {
    public static void main(String[] args) {
        String num = "";
        for (int i = 0; i < 1000; i++) num += "9";
        BigInteger max = new BigInteger(num);
        ArrayList<BigInteger> fibs = new ArrayList<>();
        fibs.add(BigInteger.ZERO);
        fibs.add(BigInteger.ONE);
        while (true) {
            BigInteger f = fibs.get(fibs.size() - 1).add(fibs.get(fibs.size() - 2));
            if (f.compareTo(BigInteger.ZERO) > 0 && max.compareTo(f.multiply(fibs.get(fibs.size() - 1))) < 0) break;
            fibs.add(f);
        }
        ArrayList<BigInteger> products = new ArrayList<>();
        for (int i = 1; i < fibs.size(); i++) {
            BigInteger product = fibs.get(i).multiply(fibs.get(i - 1));
            products.add(product);
        }
        System.out.println(products);
        boolean written = false;
        while (!written) {
            try {
                File output = new File("FibMult.txt");
                if (!output.createNewFile()) {
                    //output.delete();
                    //output.createNewFile();
                }
                PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                getWrite.append(products.toString());
                getWrite.close();
                written = true;
            } catch (IOException e) { e.printStackTrace(); try { Thread.sleep(1000); } catch (Exception e1) {e1.printStackTrace(); } }
        }
    }
}