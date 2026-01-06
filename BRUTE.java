import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class BRUTE {

    public class SOD {
        static int length = 1000;
        static int testTry = 100;

        static long seed = 0;

        static ArrayList<Double> compsTable = new ArrayList<>();
        static ArrayList<Double> varTable = new ArrayList<>();
        static ArrayList<String> strTable = new ArrayList<>();

        static int[] array;

        static int[] seq = {1, 4, 10, 23, 57, 132, 301, 701};

        static Random rng = new Random(seed);

        static long comps = 0;
        static double var;
        static int done = 0;

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

        protected static void runShellOnDemand() {
            array = new int[length];
            int d = done;
            for (int i = 0; i < length; i++) array[i] = i;
            while (done < testTry) {
                for (int j = 0; j < length; j++) swap(array, j, randInt(j, length));
                long get = shell();
                compsTable.add((double) get);
                comps += get;
                done++;
            }
            double sum = comps;
            sum /= testTry;
            for (int a = d; a < compsTable.size(); a++) {
                double b = compsTable.get(a) - sum;
                b *= b;
                compsTable.set(a, b);
            }
            var = 0;
            for (int a = 0; a < testTry; a++) var += compsTable.get(a);
            var = Math.sqrt(var);
            varTable.add(var);
            strTable.add("" + var);
            System.err.println("(" + testTry + ", " + Double.parseDouble(strTable.get(strTable.size() - 1)) / Math.sqrt(testTry) + ", " + 1.0 * comps / testTry + ")");
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
                    System.err.println("DEFAULT WILL BE USED: 1000");
                    length = 1000;
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
            rng = new Random(seed);
            a = argsContain(args, "--seq");
            if (a != -1) {
                try {
                    seq = new int[args.length - a - 1];
                    for (int i = a + 1, j = 0; i < args.length; i++, j++) seq[j] = Integer.valueOf(args[i]);
                } catch (NumberFormatException e) {
                    String[] seqError = new String[args.length - a - 1];
                    for (int i = a + 1, j = 0; i < args.length; i++, j++) seqError[j] = args[i];
                    System.err.println("PROBLEM PARSING SEQUENCE: " + printSeq(seqError));
                    System.err.println("DEFAULT WILL BE USED: 1, 4, 10, 23, 57, 132, 301, 701");
                    seq = new int[] {1, 4, 10, 23, 57, 132, 301, 701};
                }
            }
        }

        public static double[] getBrutesBounds(int length) {
            String[] args = {"--length", Integer.toString(length), "--testtry", "100000", "--seed", "0", "--seq", "1", "4", "10", "23", "57", "132", "301", "701", "1636", "3657", "8172", "18235", "40764", "91064"};
            argSetup(args);
            int temp = 100000;
            for (testTry = 1000; testTry <= temp; testTry += 1000) runShellOnDemand();
            return new double[] {Double.parseDouble(strTable.get(strTable.size() - 1)) / Math.sqrt(testTry), 1.0 * comps / testTry};
        }
    }

    public class LDE {

        static int[] ciuraR = {701, 301, 132, 57, 23, 10, 4, 1};
        static int[] testOrder = {0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5};

        protected static int[] runLDE(int in) {
            if (Math.abs(in) < 2) return new int[] {};
            int start = 0;
            while (start < ciuraR.length && ciuraR[start] >= Math.abs(in)) start++;
            int[] seq = new int[ciuraR.length - start];
            for (int i = start; i < ciuraR.length; i++) seq[i - start] = ciuraR[i];
            int[] gives;
            int[] best = new int[] {-999};
            for (int len = 1; len <= seq.length; len++) {
                gives = new int[len];
                for (int i = 0; i < len; i++) gives[i] = 0;
                while (true) {
                    int sum = 0;
                    for (int j = 0; j < len; j++) sum += seq[j] * testOrder[gives[j]];
                    if (sum == in && (in == 0 || gives[len - 1] != 0)) return gives;
                    int i = len - 1;
                    while (i >= 0) {
                        gives[i]++;
                        if (gives[i] == testOrder.length) {
                            gives[i] = 0;
                            i--;
                            if (i < 0) break;
                        } else break;
                    }
                    if (i < 0) break;
                }
            }
            return best;
        }

        public static boolean good(int v, int l, int[] newseq) {
            for (int i = 0, j = newseq.length - 1; i < j; i++, j--) {
                int tmp = newseq[i];
                newseq[i] = newseq[j];
                newseq[j] = tmp;
            }
            ciuraR = newseq;
            int[] lde = runLDE(v);
            return lde.length > l;
        }
    }

    static int length = 25000;

    static int avg = 0;
    static int var = 0;

    static double minMult = 2.2;
    static double maxMult = 2.4;
    static int maxTimes = 2000;

    static int[] seq = {1, 4, 10, 23, 57, 132, 301, 701, 1636, 0, 0, 0};
    static int[] array;

    static Random rng;

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
        String out = "..." + seq[seq.length - 4] + ", " + seq[seq.length - 3] + ", " + seq[seq.length - 2] + ", " + seq[seq.length - 1] + "...: " + c;
        return out;
    }

    protected static String acceptOrDeny(long c, int t) {
        if (c <= (-1 * var) + (t * avg)) return "ACCEPTED";
        else if (c >= var + (t * avg)) return "DENIED";
        return "NEUTRAL";
    }

    protected static boolean brute(int x, int y, int z) {
        System.err.print("TEST BY (..." + seq[seq.length - 4] + ", " + x + ", " + y + ", " + z + "...) ");
        seq[seq.length - 3] = x;
        seq[seq.length - 2] = y;
        seq[seq.length - 1] = z;
        rng = new Random(0);
        long comps = 0;
        int times = 0;
        while (times < 2000) {
            for (int j = 0; j < length; j++) swap(array, j, randInt(j, length));
            long get = shell();
            comps += get;
            times++;
            if (acceptOrDeny(comps, times) == "ACCEPTED") {
                try {
                    String basePath = new File("").getAbsolutePath();
                    Path path = FileSystems.getDefault().getPath(basePath + "/BF/BF-output-" + x + ".txt");
                    File output = new File(basePath + "/BF/BF-output-" + x + ".txt");
                    if (!output.createNewFile()) {
                        //output.delete();
                        //output.createNewFile();
                    }
                    String currentOutput = Files.readString(path);
                    PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                    getWrite.append(currentOutput + render(comps) + ": ACCEPTED IN " + times + " TRIES, (AVG " + (1.0 * comps / times) + ")\n");
                    getWrite.close();
                } catch (IOException e) { e.printStackTrace(); }
                System.err.println("ACCEPTED IN " + times + " TRIES");
                return true;
            } else if (acceptOrDeny(comps, times) == "DENIED") {
                System.err.println("DENIED IN " + times + " TRIES");
                return false;
            }
        }
        if (times == maxTimes) {
            try {
                String basePath = new File("").getAbsolutePath();
                Path path = FileSystems.getDefault().getPath(basePath + "/BF/BF-output-NEUTRAL.txt");
                File output = new File(basePath + "/BF/BF-output-NEUTRAL.txt");
                if (!output.createNewFile()) {
                    //output.delete();
                    //output.createNewFile();
                }
                String currentOutput = Files.readString(path);
                PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                getWrite.append(currentOutput + render(comps) + ": POTENTIAL NEUTRAL\n");
                getWrite.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
        System.err.println("NEUTRAL");
        return false;
    }

    public static void main(String[] args) {
        double[] get = SOD.getBrutesBounds(length);
        var = (int) (10 * get[0]);
        avg = (int) (get[1] + length / 4.75);
        String basePath = new File("").getAbsolutePath();
        File _BFDirectory = new File(basePath + "/BF");
        _BFDirectory.mkdirs();
        array = new int[length];
        for (int i = 0; i < length; i++) array[i] = i;
        for (int x = (int) (minMult * seq[seq.length - 4]); x <= maxMult * seq[seq.length - 4] && x < length; x++) {
            int[] testX = new int[seq.length - 3];
            for (int i = 0; i < seq.length - 3; i++) testX[i] = seq[i];

            if (LDE.good(x, 3, testX)) {
                int testCount = 0;
                System.err.println("X = " + x + " LDE IDEAL");
                int timesAcc = 0;
                for (int y = (int) (minMult * x); y <= maxMult * x && y < length; y++) {
                    int[] testY = new int[seq.length - 2];
                    for (int i = 0; i < seq.length - 3; i++) testY[i] = seq[i];
                    testY[seq.length - 3] = x;
                    if (LDE.good(y, 4, testY)) {
                        System.err.println("Y = " + y + " LDE IDEAL");
                        for (int z = (int) (minMult * y); z <= maxMult * y && z < length; z++) {
                            testCount++;
                            if (brute(x, y, z)) timesAcc++;
                        }
                    } else System.err.println("Y = " + y + " LDE NOT IDEAL, SKIPPING");
                }
                if (timesAcc > 0) {
                    try {
                        Path path = FileSystems.getDefault().getPath("BF-timesX-new.txt");
                        File output = new File("BF-timesX-new.txt");
                        if (!output.createNewFile()) {
                            //output.delete();
                            //output.createNewFile();
                        }
                        String currentOutput = Files.readString(path);
                        PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                        getWrite.append(currentOutput + x + ": " + timesAcc + " TIMES OF " + testCount + " (" + String.format("%.3f%", 100.0 * timesAcc / testCount) + "%)\n");
                        getWrite.close();
                    } catch (IOException e) { e.printStackTrace(); }
                }
            } else System.err.println("X = " + x + " LDE NOT IDEAL, SKIPPING");
        }
    }
}