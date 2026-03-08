import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class LeapfrogOnDemandAllSeq {

    static int length = 100;
    static int testTry = 100;

    static long seed = 0;

    static boolean redundDo = true;

    static String fileName = "allSeq";

    static ArrayList<Double> compsTable = new ArrayList<>();
    static ArrayList<Double> varTable = new ArrayList<>();
    static ArrayList<Double> redundTable = new ArrayList<>();

    static int[] array;

    static int[] seq = {1, 0, 0, 0, 0};
    static int[] seqb = {1, 0, 0, 0, 0};

    static Random rng = new Random(seed);

    static long comps = 0;
    static double var;
    static int done = 0;

    static String gapName = "";

    static ArrayList<Integer> usedMins = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> usedPairs = new ArrayList<>();
    static double redund = 0;

    static BigInteger totals = new BigInteger("0");

    static long bestComps = Long.MAX_VALUE;

    protected static int randInt(int min, int max) {
        return rng.nextInt(max - min) + min;
    }

    protected static void swap(int[] h, int a, int b) {
        int temp = h[a];
        h[a] = h[b];
        h[b] = temp;
    }

    protected static boolean comp(int a, int b, long c) {
        if (!redundDo) return a > b;
        int[] pair = new int[] {Math.min(a, b), Math.max(a, b)};
        int minI = usedMins.indexOf(pair[0]);
        if (minI != -1) {
            if (usedPairs.get(minI).contains(pair[1])) redund++;
            else usedPairs.get(minI).add(pair[1]);
        } else {
            usedMins.add(pair[0]);
            ArrayList<Integer> add = new ArrayList<>();
            add.add(pair[1]);
            usedPairs.add(add);
        }
        return a > b;
    }

    protected static long points(int i, int j, int k) {
        int a, b, c;
        long comps = 0;
        a = array[i];
        b = array[j];
        if (comp(a, b, comps++)) {
            array[i] = b;
            c = b;
            array[j] = a;
            b = a;
            a = c;
        }
        c = array[k];
        if (comp(b, c, comps++)) {
            array[k] = b;
            if (comp(a, c, comps++)) array[j] = c;
            else {
                array[i] = c;
                array[j] = a;
            }
        }
        return comps;
    }

    protected static long leapfrogPass(int gap1, int gap2, boolean dir) {
        long c = 0;
        if (dir) for (int i = 0, j = gap2, k = gap1 + gap2; k < length; i++, j++, k++) c += points(i, j, k);
        else for (int i = length - gap1 - gap2 - 1, j = length - gap2 - 1, k = length - 1; i >= 0; i--, j--, k--) c += points(i, j, k);
        return c;
    }

    protected static long shellPass(int gap) {
        long c = 0;
        for (int h = gap, i = h; i < length; i++) {
            int v = array[i], j = i;
            for (; j >= h && j - h >= 0 && comp(array[j - h], v, c++); j -= h) swap(array, j, j - h);
        }
        return c;
    }

    protected static long leapfrog() {
        long c = 0;
        boolean d = true;
        for (int i = seq.length - 1; i >= 1; i--, d = !d) c += leapfrogPass(seq[i], seqb[i], d);
        c += shellPass(1);
        return c;
    }

    protected static void runLeapfrogOnDemand() {
        System.err.println("Trying to make " + length + " length array with " + testTry + " trials: " + genGapName());
        array = new int[length];
        for (int i = 0; i < length; i++) array[i] = i;
        while (done < testTry) {
            for (int j = 0; j < length; j++) swap(array, j, randInt(j, length));
            usedPairs.clear();
            usedMins.clear();
            redund = 0;
            long get = leapfrog();
            compsTable.add((double) get);
            redundTable.add(redund);
            comps += get;
            done++;
        }
        double sum = comps;
        sum /= testTry;
        for (int a = 0; a < compsTable.size(); a++) {
            double b = compsTable.get(a) - sum;
            b *= b;
            compsTable.set(a, b);
        }
        var = 0;
        redund = 0;
        for (int a = 0; a < testTry; a++) {
            var += compsTable.get(a);
            redund += redundTable.get(a);
        }
        redund /= testTry;
        var = Math.sqrt(var);
        varTable.add(var);
        if (comps <= bestComps) {
            bestComps = comps;
            boolean written = false;
            while (!written) {
                try {
                    Path path = FileSystems.getDefault().getPath("LOD-BEST-" + fileName + ".txt");
                    File output = new File("LOD-BEST-" + fileName + ".txt");
                    if (!output.getParentFile().exists()) output.getParentFile().mkdirs();
                    if (!output.createNewFile()) {
                        //output.delete();
                        //output.createNewFile();
                    }
                    String currentOutput = Files.readString(path);
                    PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                    getWrite.append(currentOutput + render());
                    getWrite.close();
                    written = true;
                } catch (IOException e) { e.printStackTrace(); try { Thread.sleep(1000); } catch (Exception e1) {e1.printStackTrace(); } }
            }
        }
        /*boolean written = false;
        while (!written) {
            try {
                Path path = FileSystems.getDefault().getPath("LOD-" + fileName + ".txt");
                File output = new File("LOD-" + fileName + ".txt");
                if (!output.getParentFile().exists()) output.getParentFile().mkdirs();
                if (!output.createNewFile()) {
                    //output.delete();
                    //output.createNewFile();
                }
                String currentOutput = Files.readString(path);
                PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                getWrite.append(currentOutput + render());
                getWrite.close();
                written = true;
            } catch (IOException e) { e.printStackTrace(); try { Thread.sleep(1000); } catch (Exception e1) {e1.printStackTrace(); } }
        }*/
    }

    protected static String writeSeqBackwards() {
        String out = "SEQ = [";
        for (int i = seq.length - 1; i >= 0; i--) out += (seq[i]) + (i == 0 ? "]" : ":" + seqb[i] + ", ");
        return out;
    }

    protected static String render() {
        return gapName + ": (LEN = " + length + ", TRY = " + testTry + ", VAR = " + (long) ((varTable.get(varTable.size() - 1)) / Math.sqrt(testTry)) + ", COMPS_AVG = " + (long) (1.0 * comps / testTry) + (redundDo ? ", REDUND_AVG = " + (long) redund : "") + ",\n" + writeSeqBackwards() + ",\n)\n\n";
    }

    protected static String printSeq(String[] a) {
        String out = "[";
        for (int i = 0; i < a.length; i++) out += a[i] + (i == a.length - 1 ? "]" : ", ");
        return out;
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
                System.err.println("DEFAULT WILL BE USED: 5000");
                length = 5000;
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
        a = argsContain(args, "--seed");
        if (a != -1) {
            try { seed = Long.parseLong(args[a + 1]); }
            catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING SEED: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: 0");
                seed = 0;
            }
        }
        a = argsContain(args, "--name");
        if (a != -1) gapName = args[a + 1];
        a = argsContain(args, "--noredund");
        if (a != -1) redundDo = false;
        a = argsContain(args, "--seq");
        if (a != -1) {
            try {
                seq = new int[args.length - a - 1];
                for (int i = a + 1, j = 0; i < args.length; i++, j++) seq[j] = Integer.valueOf(args[i]);
            } catch (NumberFormatException e) {
                String[] seqError = new String[args.length - a - 1];
                for (int i = a + 1, j = 0; i < args.length; i++, j++) seqError[j] = args[i];
                System.err.println("PROBLEM PARSING SEQUENCE: " + printSeq(seqError));
                System.err.println("DEFAULT WILL BE USED: 1, 4, 10, 23, 57, 132, 301, 701, 1636");
                seq = new int[] {1, 4, 10, 23, 57, 132, 301, 701, 1636};
            }
        }
        a = argsContain(args, "--file");
        if (a != -1) fileName = args[a + 1];
    }

    protected static int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    protected static String genGapName() {
        String name = "1";
        for (int i = 1; i < seq.length; i++) name += "-" + seq[i] + ":" + seqb[i];
        return name;
    }

    protected static void recursive(int index, int value) {
        seq[index] = value;
        for (int b = value - 1; b > value / 2; b--) {
            seqb[index] = b;
            if (index == seq.length - 1) {
                gapName = genGapName();
                fileName = "allSeq/" + genGapName();
                usedPairs.clear();
                usedMins.clear();
                compsTable.clear();
                varTable.clear();
                redundTable.clear();
                done = 0;
                comps = 0;
                //runLeapfrogOnDemand();
                totals = totals.add(BigInteger.ONE);
            } else for (int a = value + 1; a < length; a++) {
                if (index < seq.length - 4 && gcd(value, a) == 1) System.err.println(totals + " " + index + " " + a);
                recursive(index + 1, a);
            }
        }
    }

    public static void main(String[] args) {
        argSetup(args);
        redundDo = false;
        for (int a = 2; a < length; a++) recursive(1, a);
        System.err.println(totals.toString());
    }
}