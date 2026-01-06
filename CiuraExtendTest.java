import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class CiuraExtendTest {

    static int length = 1000000;
    static int testTry = 100;
    static int seqLen = 20;
    static int rangeMin = 1636;
    static int rangeMax = 1636;
    static double multMin = 2.3;
    static double multMax = 2.35;
    static int incPow = 4;
    static double inc;

    static int[] seq;
    static int[] array;

    static int[] ciura = {1, 4, 10, 23, 57, 132, 301, 701, 1636};

    static Random rng;
    static long seed = 0;

    protected static int randInt(int min, int max) {
        return rng.nextInt(max - min) + min;
    }

    protected static void swap(int[] h, int a, int b) {
        int temp = h[a];
        h[a] = h[b];
        h[b] = temp;
    }

    protected static boolean comp(int a, int b, long c) {
        if (a > b) return true;
        else return false;
    }

    protected static long shellPass(int gap) {
        long c = 0;
        for (int h = gap, i = h; i < length; i++) {
            int v = array[i], j = i;
            for (; j >= h && j - h >= 0 && comp(array[j - h], v, c++); j -= h) swap(array, j, j - h);
        }
        return c;
    }

    protected static long shell() {
        long c = 0;
        for (int i = seqLen - 1; i >= 0; i--) c += shellPass(seq[i]);
        return c;
    }

    protected static void printSeq(int[] a) {
        System.err.print("[");
        for (int i = 0; i < a.length; i++) System.err.print(a[i] + (i == a.length - 1 ? "]" : ", "));
        System.err.println("");
    }

    // Kudos, Baeldung!
    protected static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    protected static int argsContain(String[] args, String match) {
        for (int i = 0; i < args.length; i++) if (args[i].toLowerCase().equals(match)) return i;
        return -1;
    }

    protected static void argSetup(String[] args) {
        int a = argsContain(args, "--length");
        if (a != -1) {
            try {
                length = Integer.parseInt(args[a + 1]);
                if (length <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING LENGTH: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: 10000");
                length = 10000;
            }
        }
        a = argsContain(args, "--testtry");
        if (a != -1) {
            try {
                testTry = Integer.parseInt(args[a + 1]);
                if (testTry <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING TEST TRIALS: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: 100");
                testTry = 100;
            }
        }
        a = argsContain(args, "--seqlen");
        if (a != -1) {
            try {
                seqLen = Integer.parseInt(args[a + 1]);
                if (seqLen <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING SEQUENCE LENGTH: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: 10");
                seqLen = 10;
            }
        }
        a = argsContain(args, "--range");
        if (a != -1) {
            try {
                rangeMin = Integer.parseInt(args[a + 1]);
                rangeMax = Integer.parseInt(args[a + 2]);
                if (rangeMin > rangeMax) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING RANGE: " + args[a + 1] + ", " + args[a + 2]);
                System.err.println("DEFAULT WILL BE USED: 1542, 1752");
                rangeMin = 1542;
                rangeMax = 1752;
            }
        }
        a = argsContain(args, "--mults");
        if (a != -1) {
            try {
                multMin = Double.parseDouble(args[a + 1]);
                multMax = Double.parseDouble(args[a + 2]);
                if (multMin > multMax) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING MULTS: " + args[a + 1] + ", " + args[a + 2]);
                System.err.println("DEFAULT WILL BE USED: 2.2, 2.3");
                multMin = 2.2;
                multMax = 2.3;
            }
        }
        a = argsContain(args, "--decimal");
        if (a != -1) {
            try {
                incPow = Integer.parseInt(args[a + 1]);
                if (seqLen <= 1) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING DECIMAL PLACES: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: 2");
                incPow = 2;
            }
        }
    }

    public static void main(String[] args) {
        try {
            argSetup(args);
            inc = Math.pow(10, -1 * incPow);
            Path path = FileSystems.getDefault().getPath("CET-output.txt");
            File output = new File("CET-output.txt");
            if (!output.createNewFile()) {
                output.delete();
                output.createNewFile();
            }
            PrintWriter argWrite = new PrintWriter(output, "UTF-8");
            argWrite.write("ARGS:\n\tLENGTH: " + length + "\n\tTESTTRY: " + testTry + "\n\tSEQLEN: " + seqLen + "\n\tRANGE: " + rangeMin + ", " + rangeMax + "\n\tMULTS: " + multMin + ", " + multMax + "\n\tDECIMAL PLACES: " + incPow + "\n\n");
            argWrite.close();
            array = new int[length];
            for (int i = 0; i < length; i++) array[i] = i;
            seq = new int[seqLen];
            for (int i = 0; i < ciura.length; i++) seq[i] = ciura[i];
            for (int nextGap = rangeMin; nextGap <= rangeMax; nextGap++) {
                seq[ciura.length] = nextGap;
                System.err.println("TESTING FOR NEXT IS " + nextGap);
                double best = 2.1 - inc;
                long bestVal = Long.MAX_VALUE;
                long comps = 0;
                for (double nextMult = multMin; nextMult <= multMax; nextMult += inc, nextMult = round(nextMult, incPow)) {
                    for (int i = ciura.length + 1; i < seqLen; i++) seq[i] = (int) (nextMult * seq[i - 1]);
                    comps = 0;
                    rng = new Random(seed);
                    for (int b = 0; b < testTry; b++) {
                        for (int i = 0; i < length; i++) swap(array, i, randInt(i, length));
                        comps += shell();
                    }
                    System.err.print("TESTED " + nextMult + ": " + comps + " COMPS ");
                    if (comps < bestVal) {
                        best = nextMult;
                        bestVal = comps;
                        System.err.print("NEW BEST");
                    }
                    System.err.println("");
                }
                System.err.println("BEST IS " + best + ": " + bestVal);
                String currentOutput = Files.readString(path);
                PrintWriter outputWrite = new PrintWriter(output, "UTF-8");
                outputWrite.append(currentOutput + bestVal + ": " + nextGap + " BY " + best + "\n");
                outputWrite.close();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}