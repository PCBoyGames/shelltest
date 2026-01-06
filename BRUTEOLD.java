import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class BRUTEOLD {

    //OPTIONAL SHELLONDEMAND
    public class SOD {
        static int length = 1000;
        static int testTry = 100;

        static long seed = 0;

        static ArrayList<Double> compsTable = new ArrayList<>();
        static ArrayList<Double> varTable = new ArrayList<>();
        static ArrayList<String> strTable = new ArrayList<>();
        static ArrayList<Double> redundTable = new ArrayList<>();
        static ArrayList<Double> depthsTable = new ArrayList<>();

        static int[] array;

        static int[] seq = {1, 4, 10, 23, 57, 132, 301, 701};

        static Random rng = new Random(seed);

        static long comps = 0;
        static double var;
        static int done = 0;

        static String gapName = "CIURA_1636";

        static ArrayList<Integer> usedMins = new ArrayList<>();
        static ArrayList<ArrayList<Integer>> usedPairs = new ArrayList<>();
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
            if (a > b) return true;
            else return false;
        }

        protected static long shellPass(int gap) {
            long c = 0;
            if (gap > length) return 0;
            int[] depths = new int[length - gap];
            for (int h = gap, i = h; i < length; i++) {
                int v = array[i], j = i, d = 1;
                for (; j >= h && j - h >= 0 && comp(array[j - h], v, c++); j -= h, d++) swap(array, j, j - h);
                depths[i - gap] = d;
            }
            int sum = 0;
            for (int i = 0; i < depths.length; i++) sum += depths[i];
            double avgDepth = 1.0 * sum / depths.length;
            depthsPast.add(avgDepth);
            //System.err.println("Pass of " + gap + " at " + redund + " redundant pairs and average insertion depth " + avgDepth);
            return c;
        }

        protected static long shell() {
            long c = 0;
            for (int i = seq.length - 1; i >= 0; i--) c += shellPass(seq[i]);
            return c;
        }

        protected static void runShellOnDemand() {
            System.err.println("Trying to make " + length + " length array with " + testTry + " trials");
            array = new int[length];
            for (int i = 0; i < length; i++) array[i] = i;
            compsTable.clear();
            varTable.clear();
            strTable.clear();
            redundTable.clear();
            depthsTable.clear();
            comps = 0;
            done = 0;
            while (done < testTry) {
                for (int j = 0; j < length; j++) swap(array, j, randInt(j, length));
                usedPairs.clear();
                usedMins.clear();
                redund = 0;
                depthsPast.clear();
                long get = shell();
                compsTable.add((double) get);
                redundTable.add(redund);
                double avgDepth = 0;
                for (int i = 0; i < depthsPast.size(); i++) avgDepth += depthsPast.get(i);
                avgDepth /= depthsPast.size();
                depthsTable.add(avgDepth);
                comps += get;
                done++;
                if (done % 10 == 0) System.err.println("Done " + done + " of " + testTry);
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
                depth += depthsTable.get(a);
            }
            redund /= testTry;
            depth /= testTry;
            var = Math.sqrt(var);
            varTable.add(var);
            strTable.add("" + var);
            System.err.println("(" + testTry + ", " + Double.parseDouble(strTable.get(strTable.size() - 1)) / Math.sqrt(testTry) + ", " + 1.0 * comps / testTry + ")");
        }

        protected static String render() {
            return gapName + ": (" + (int) redund + ", " + depth + ")";
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
        }

        public static double[] getBrutesBounds(int length) {
            String[] args = {"--length", Integer.toString(length), "--testtry", "100000", "--seed", "0", "--seq", "1", "4", "10", "23", "57", "132", "301", "701", "1636", "3657"};
            argSetup(args);
            runShellOnDemand();
            return new double[] {Double.parseDouble(strTable.get(strTable.size() - 1)) / Math.sqrt(testTry), 1.0 * comps / testTry};
        }

        public static String affirmSOD(String[] args) {
            argSetup(args);
            runShellOnDemand();
            return render();
        }
    }

    static int length = 10000;

    static int avg = 190570;
    static int var = -2145;

    //TWO ZEROS AT END REQUIRED: USED FOR NEW GAPS
    static int[] seq = {1, 4, 10, 23, 57, 132, 301, 701, 0, 0};
    static int[] array;

    static Random rng;

    static ArrayList<Double> compsTable = new ArrayList<>();

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
        for (int i = seq.length - 1; i >= 0; i--) c += shellPass(seq[i]);
        return c;
    }

    protected static String render(long c) {
        String out = "..." + seq[seq.length - 3] + ", " + seq[seq.length - 2] + ", " + seq[seq.length - 1] + "...: " + c;
        return out;
    }

    protected static String acceptOrDeny(long c, int t) {
        if (c <= var + (t * avg)) return "ACCEPTED";
        else if (c >= (-1 * var) + (t * avg)) return "DENIED";
        return "NEUTRAL";
    }

    protected static boolean brute(int x, int y) {
        compsTable.clear();
        System.err.println("TEST BY (..." + seq[seq.length - 3] + ", " + x + ", " + y + "...)");
        seq[seq.length - 2] = x;
        seq[seq.length - 1] = y;
        rng = new Random(0);
        long comps = 0;
        int times = 0;
        while (times < 2000) {
            for (int j = 0; j < length; j++) swap(array, j, randInt(j, length));
            long get = shell();
            compsTable.add((double) get);
            comps += get;
            times++;
            if (acceptOrDeny(comps, times) == "ACCEPTED") {
                try {
                    String basePath = new File("").getAbsolutePath();
                    Path path = FileSystems.getDefault().getPath(basePath + "/BF/BF-output-" + x + ".txt");
                    File output = new File(basePath + "/BF/BF-output-" + x + ".txt");
                    output.createNewFile();
                    String currentOutput = Files.readString(path);
                    PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                    String[] p = new String[] {"--name", "SOD_SAYS", "--length", Integer.toString(length), "--testtry", "100", "--seed", "0", "--seq"};
                    String[] params = new String[p.length + seq.length];
                    for (int i = 0; i < p.length; i++) params[i] = p[i];
                    for (int i = 0; i < seq.length; i++) params[i + p.length] = Integer.toString(seq[i]);
                    String printTest = render(comps) + ": ACCEPTED IN " + times + " TRIES, (AVG " + (1.0 * comps / times) + ") " + SOD.affirmSOD(params);
                    System.err.println(printTest);
                    getWrite.append(currentOutput + printTest + "\n");
                    getWrite.close();
                } catch (IOException e) { e.printStackTrace(); }
                System.err.println("ACCEPTED IN " + times + " TRIES");
                return true;
            } else if (acceptOrDeny(comps, times) == "DENIED") {
                System.err.println("DENIED IN " + times + " TRIES");
                return false;
            }
        }
        if (times == 2000) {
            try {
                String basePath = new File("").getAbsolutePath();
                Path path = FileSystems.getDefault().getPath(basePath + "/BF/BF-output-NEUTRAL.txt");
                File output = new File(basePath + "/BF/BF-output-NEUTRAL.txt");
                output.createNewFile();
                String currentOutput = Files.readString(path);
                PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                String[] p = new String[] {"--name", "SOD_SAYS", "--length", Integer.toString(length), "--testtry", "100", "--seed", "0", "--seq"};
                String[] params = new String[p.length + seq.length];
                for (int i = 0; i < p.length; i++) params[i] = p[i];
                for (int i = 0; i < seq.length; i++) params[i + p.length] = Integer.toString(seq[i]);
                getWrite.append(currentOutput + render(comps) + ": POTENTIAL NEUTRAL" + SOD.affirmSOD(params) + "\n");
                getWrite.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
        return false;
    }

    public static void main(String[] args) {
        //SHELLONDEMAND USES BEST KNOWN SEQUENCE
        //double[] get = SOD.getBrutesBounds(length);
        //avg = (int) get[1];
        //System.err.println(get[1]);
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        String basePath = new File("").getAbsolutePath();
        File _BFDirectory = new File(basePath + "/BF");
        _BFDirectory.mkdirs();
        array = new int[length];
        for (int i = 0; i < length; i++) array[i] = i;
        for (int x = 1636; x <= 1636 && x < length; x++) {
            int timesAcc = 0;
            int testCount = 0;
            for (int y = 2 * x; y <= 3 * x && y < length; y++) {
                testCount++;
                if (brute(x, y)) timesAcc++;
            }
            //UNCOMMENT TO OMIT 0
            //if (timesAcc > 0) {
                try {
                    Path path = FileSystems.getDefault().getPath("BF-timesX.txt");
                    File output = new File("BF-timesX.txt");
                    if (!output.createNewFile()) {
                        //output.delete();
                        //output.createNewFile();
                    }
                    String currentOutput = Files.readString(path);
                    PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                    getWrite.append(currentOutput + x + ": " + timesAcc + " TIMES OF " + testCount + " (" + 100.0 * timesAcc / testCount + "%)\n");
                    getWrite.close();
                } catch (IOException e) { e.printStackTrace(); }
            //UNCOMMENT TO OMIT 0
            //}
        }
    }
}