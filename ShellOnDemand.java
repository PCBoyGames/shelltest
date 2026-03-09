import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class ShellOnDemand {

    static int length = 5000;
    static int testTry = 100;

    static long seed = 0;

    static boolean redundDo = true;

    static String fileName = "output";

    static ArrayList<Double> compsTable = new ArrayList<>();
    static ArrayList<Double> swapsTable = new ArrayList<>();
    static ArrayList<Double> redundTable = new ArrayList<>();
    static ArrayList<Double> depthsTable = new ArrayList<>();
    static ArrayList<ArrayList<Double>> passTable = new ArrayList<>();
    static ArrayList<Long> overallDepthsTable = new ArrayList<>();
    static ArrayList<ArrayList<Long>> depthsPerGapTable = new ArrayList<>();
    static ArrayList<Long> swapsPerGapTable = new ArrayList<>();

    static int[] array;

    static int[] seq = {1, 4, 10, 23, 57, 132, 301, 701, 1636};

    static Random rng;

    static long comps = 0;
    static long swaps = 0;
    static double var;
    static ArrayList<Double> pass = new ArrayList<>();
    static int done = 0;

    static String gapName = "CIURA_1636";

    static HashMap<Integer, HashSet<Integer>> usedPairsMap = new HashMap<>();
    static double redund = 0;

    static double depth = 0;
    static ArrayList<Double> depthsPast = new ArrayList<>();

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
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        HashSet<Integer> partners = usedPairsMap.get(min);
        if (partners == null) {
            partners = new HashSet<>();
            partners.add(max);
            usedPairsMap.put(min, partners);
        } else if (!partners.add(max)) redund++;
        return a > b;
    }

    protected static long shellPass(int gap, int gapIndex) {
        long c = 0;
        int[] depths = new int[length - gap > 0 ? length - gap : 0];
        for (int h = gap, i = h; i < length; i++) {
            int v = array[i], j = i, d = 1;
            for (; j >= h && j - h >= 0 && comp(array[j - h], v, c++); j -= h, d++) {
                swap(array, j, j - h);
                while (swapsPerGapTable.size() <= gapIndex) swapsPerGapTable.add(0L);
                swapsPerGapTable.set(gapIndex, swapsPerGapTable.get(gapIndex) + 1);
                swaps++;
            }
            depths[i - gap] = d;
            while (overallDepthsTable.size() <= d) overallDepthsTable.add(0L);
            overallDepthsTable.set(d, overallDepthsTable.get(d) + 1);
            while (depthsPerGapTable.size() <= gapIndex) depthsPerGapTable.add(new ArrayList<>());
            while (depthsPerGapTable.get(gapIndex).size() <= d) depthsPerGapTable.get(gapIndex).add(0L);
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
        System.err.println("Trying to make " + length + " length array with " + testTry + " trials");
        array = new int[length];
        for (int i = 0; i < length; i++) array[i] = i;
        while (done < testTry) {
            for (int j = 0; j < length; j++) swap(array, j, randInt(j, length));
            swaps = 0;
            usedPairsMap.clear();
            redund = 0;
            depthsPast.clear();
            long get = shell();
            compsTable.add((double) get);
            swapsTable.add((double) swaps);
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
        swaps = 0;
        for (int a = 0; a < testTry; a++) {
            var += compsTable.get(a);
            redund += redundTable.get(a);
            depth += depthsTable.get(a);
            swaps += swapsTable.get(a);
        }
        redund /= testTry;
        depth /= testTry;
        swaps /= testTry;
        var = Math.sqrt(var);
        boolean written = false;
        int retries = 0;
        while (!written && retries < 10) {
            try {
                Path path = FileSystems.getDefault().getPath("SOD-" + fileName + ".txt");
                File output = new File("SOD-" + fileName + ".txt");
                if (!output.createNewFile()) {
                    //output.delete();
                    //output.createNewFile();
                }
                String currentOutput = Files.readString(path);
                PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                getWrite.append(currentOutput + render());
                getWrite.close();
                written = true;
            } catch (IOException e) {
                e.printStackTrace();
                retries++;
                try { Thread.sleep(1000); } catch (Exception e1) {e1.printStackTrace(); }
            }
        }
        if (!written) {
            System.err.println("Failed to write output after 10 retries. Printing to err instead.");
            try { Thread.sleep(1000); } catch (Exception e) {e.printStackTrace(); }
            System.err.println("\n\n" + render());
        }
    }

    protected static String writeSeqBackwards() {
        StringBuilder sb = new StringBuilder("SEQ = [");
        for (int i = seq.length - 1; i >= 0; i--) {
            sb.append(seq[i]);
            sb.append(i == 0 ? "]" : ", ");
        }
        return sb.toString();
    }

    protected static String writePass() {
        StringBuilder sb = new StringBuilder("PASS_AVG = [");
        for (int i = 0; i < pass.size(); i++) {
            sb.append(pass.get(i));
            sb.append(i == pass.size() - 1 ? "]" : ", ");
        }
        return sb.toString();
    }

    protected static String writeOverallDepths() {
        StringBuilder sb = new StringBuilder("OVERALL_DEPTHS = [");
        for (int i = 0; i < overallDepthsTable.size(); i++) {
            sb.append(overallDepthsTable.get(i));
            sb.append(i == overallDepthsTable.size() - 1 ? "]" : ", ");
        }
        return sb.toString();
    }

    protected static String writePerGap() {
        StringBuilder sb = new StringBuilder("PER_GAP = [\n");
        for (int i = 0; i < depthsPerGapTable.size(); i++) {
            sb.append("  GAP ").append(seq[i]).append(": (DEPTHS = [");
            for (int j = 0; j < depthsPerGapTable.get(i).size(); j++) {
                sb.append(depthsPerGapTable.get(i).get(j));
                if (j == depthsPerGapTable.get(i).size() - 1) {
                    sb.append("], SWAPS = ").append(swapsPerGapTable.get(i));
                    sb.append(i == seq.length - 1 ? ")\n" : "),\n");
                } else sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    protected static String render() {
        long variance = (long) (var / Math.sqrt(testTry));
        long avgComps = (long) (1.0 * comps / testTry);
        StringBuilder sb = new StringBuilder();
        sb.append(gapName).append(": (");
        sb.append("LEN = ").append(length).append(", ");
        sb.append("TRY = ").append(testTry).append(", ");
        sb.append("VAR = ").append(variance).append(", ");
        sb.append("COMPS_AVG = ").append(avgComps).append(", ");
        sb.append("SWAPS_AVG = ").append(swaps);
        if (redundDo) sb.append(", REDUND_AVG = ").append((long) redund);
        sb.append(", DEPTH_AVG_OF_AVGS = ").append(depth).append(",\n");
        sb.append(writeSeqBackwards()).append(",\n");
        sb.append(writePass()).append(",\n");
        sb.append(writeOverallDepths()).append(",\n");
        sb.append(writePerGap()).append(")\n\n");
        return sb.toString();
    }

    protected static String printSeq(String[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            sb.append(a[i]);
            sb.append(i == a.length - 1 ? "]" : ", ");
        }
        return sb.toString();
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

    public static void main(String[] args) {
        argSetup(args);
        rng = new Random(seed);
        runShellOnDemand();
    }
}