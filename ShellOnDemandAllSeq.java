import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class ShellOnDemandAllSeq {

    static int length = 1000;
    static int testTry = 100;

    static long seed = 0;

    static boolean redundDo = true;

    static String fileName = "allSeq";

    static ArrayList<Double> compsTable = new ArrayList<>();
    static ArrayList<Double> varTable = new ArrayList<>();
    static ArrayList<Double> redundTable = new ArrayList<>();
    static ArrayList<Double> depthsTable = new ArrayList<>();
    static ArrayList<ArrayList<Double>> passTable = new ArrayList<>();
    static ArrayList<Integer> overallDepthsTable = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> depthsPerGapTable = new ArrayList<>();

    static int[] array;

    static int[] seq = {1, 0, 0, 0, 0, 0, 0};

    static Random rng = new Random(seed);

    static long comps = 0;
    static double var;
    static ArrayList<Double> pass = new ArrayList<>();
    static int done = 0;

    static String gapName = "";

    static ArrayList<Integer> usedMins = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> usedPairs = new ArrayList<>();
    static double redund = 0;

    static double depth = 0;
    static ArrayList<Double> depthsPast = new ArrayList<>();

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

    protected static long shellPass(int gap, int gapIndex) {
        long c = 0;
        int[] depths = new int[length - gap > 0 ? length - gap : 0];
        for (int h = gap, i = h; i < length; i++) {
            int v = array[i], j = i, d = 1;
            for (; j >= h && j - h >= 0 && comp(array[j - h], v, c++); j -= h, d++) swap(array, j, j - h);
            depths[i - gap] = d;
            while (overallDepthsTable.size() <= d) overallDepthsTable.add(0);
            overallDepthsTable.set(d, overallDepthsTable.get(d) + 1);
            while (depthsPerGapTable.size() <= gapIndex) depthsPerGapTable.add(new ArrayList<>());
            while (depthsPerGapTable.get(gapIndex).size() <= d) depthsPerGapTable.get(gapIndex).add(0);
            depthsPerGapTable.get(gapIndex).set(d, depthsPerGapTable.get(gapIndex).get(d) + 1);
        }
        int sum = 0;
        for (int i = 0; i < depths.length; i++) sum += depths[i];
        double avgDepth = sum == 0 ? 0 : 1.0 * sum / depths.length;
        depthsPast.add(avgDepth);
        return c;
    }

    protected static long shell() {
        long c = 0;
        for (int i = seq.length - 1; i >= 0; i--) c += shellPass(seq[i], i);
        return c;
    }

    protected static void runShellOnDemand() {
        System.err.println("Trying to make " + length + " length array with " + testTry + " trials: " + genGapName());
        array = new int[length];
        for (int i = 0; i < length; i++) array[i] = i;
        while (done < testTry) {
            for (int j = 0; j < length; j++) swap(array, j, randInt(j, length));
            usedPairs.clear();
            usedMins.clear();
            redund = 0;
            depthsPast.clear();
            long get = shell();
            compsTable.add((double) get);
            redundTable.add(redund);
            passTable.add(new ArrayList<>(depthsPast));
            double avgDepth = 0;
            int depthsUsed = 0;
            for (int i = 0; i < depthsPast.size(); i++) {
                if (depthsPast.get(i) > 0) {
                    avgDepth += depthsPast.get(i);
                    depthsUsed++;
                }
            }
            avgDepth /= depthsUsed;
            depthsTable.add(avgDepth);
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
        pass = new ArrayList<>();
        depth = 0;
        for (int i = 0; i < seq.length; i++) pass.add(0.0);
        for (int i = 0; i < pass.size(); i++) {
            double pSum = 0;
            for (int j = 0; j < passTable.size(); j++) if (i < passTable.get(j).size()) pSum += passTable.get(j).get(i);
            pSum /= passTable.size();
            pass.set(i, pSum);
        }
        for (int a = 0; a < testTry; a++) {
            var += compsTable.get(a);
            redund += redundTable.get(a);
            depth += depthsTable.get(a);
        }
        redund /= testTry;
        depth /= testTry;
        var = Math.sqrt(var);
        varTable.add(var);
        if (comps <= bestComps) {
            bestComps = comps;
            boolean written = false;
            while (!written) {
                try {
                    Path path = FileSystems.getDefault().getPath("SOD-BEST-" + fileName + ".txt");
                    File output = new File("SOD-BEST-" + fileName + ".txt");
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
        boolean written = false;
        while (!written) {
            try {
                Path path = FileSystems.getDefault().getPath("SOD-" + fileName + ".txt");
                File output = new File("SOD-" + fileName + ".txt");
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

    protected static String writeSeqBackwards() {
        String out = "SEQ = [";
        for (int i = seq.length - 1; i >= 0; i--) out += (seq[i]) + (i == 0 ? "]" : ", ");
        return out;
    }

    protected static String writePass() {
        String out = "PASS_AVG = [";
        for (int i = 0; i < pass.size(); i++) out += (pass.get(i)) + (i == pass.size() - 1 ? "]" : ", ");
        return out;
    }

    protected static String writeOverallDepths() {
        String out = "OVERALL_DEPTHS = [";
        for (int i = 0; i < overallDepthsTable.size(); i++) out += (overallDepthsTable.get(i)) + (i == overallDepthsTable.size() - 1 ? "]" : ", ");
        return out;
    }

    protected static String writeDepthsPerGap() {
        String out = "DEPTHS_PER_GAP = [\n";
        for (int i = 0; i < depthsPerGapTable.size(); i++) {
            out += "  GAP " + seq[i] + ": [";
            for (int j = 0; j < depthsPerGapTable.get(i).size(); j++) out += (depthsPerGapTable.get(i).get(j)) + (j == depthsPerGapTable.get(i).size() - 1 ? "]\n" : ", ");
        }
        out += "]";
        return out;
    }

    protected static String render() {
        return gapName + ": (LEN = " + length + ", TRY = " + testTry + ", VAR = " + (long) ((varTable.get(varTable.size() - 1)) / Math.sqrt(testTry)) + ", COMPS_AVG = " + (long) (1.0 * comps / testTry) + (redundDo ? ", REDUND_AVG = " + (long) redund : "") + ", DEPTH_AVG_OF_AVGS = " + depth + ",\n" + writeSeqBackwards() + ",\n" + writePass() + ",\n" + writeOverallDepths() + ",\n" + writeDepthsPerGap() + ")\n\n";
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
                length = (int) Integer.parseInt(args[a + 1]);
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

    protected static String genGapName() {
        String name = "1";
        for (int i = 1; i < seq.length; i++) name += "-" + seq[i];
        return name;
    }

    protected static void recursive(int index, int value) {
        seq[index] = value;
        if (index == seq.length - 1) {
            gapName = genGapName();
            fileName = "allSeq/" + genGapName();
            usedPairs.clear();
            usedMins.clear();
            compsTable.clear();
            varTable.clear();
            redundTable.clear();
            depthsTable.clear();
            passTable.clear();
            overallDepthsTable.clear();
            depthsPerGapTable.clear();
            done = 0;
            comps = 0;
            runShellOnDemand();
            totals = totals.add(BigInteger.ONE);
        } else for (int a = 2 * value + 1; a < Math.min(length, 3 * value); a++) recursive(index + 1, a);
    }

    public static void main(String[] args) {
        argSetup(args);
        redundDo = false;
        for (int a = 3; a < 7; a++) recursive(1, a);
        System.err.println(totals.toString());
    }
}