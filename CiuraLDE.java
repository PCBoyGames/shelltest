import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class CiuraLDE {

    static int min = 968727;
    static int max = 968727;

    static int testRange = 5;

    static boolean longestRange = false;
    static boolean rangeMode = true;

    static int[] ciuraR = {211909, 46355, 10140, 2218, 485, 106, 23};
    // 423067086, 92545925, 20244421, 4428467, 968727
    // {701, 301, 132, 57};
    static int[] testOrder = {0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5};

    static ArrayList<int[]> perms = new ArrayList<>();
    static ArrayList<Integer> permsWays = new ArrayList<>();
    static int permsfound;

    protected static String printSeq(int[] a) {
        String out = "[";
        for (int i = 0; i < a.length; i++) out += a[i] + (i == a.length - 1 ? "]" : ", ");
        if (a.length == 0) out += "]";
        return out;
    }

    protected static String printSeq(double[] a) {
        String out = "[";
        for (int i = 0; i < a.length; i++) out += ((Math.round(1000 * a[i]) / 1000.0) + (i == a.length - 1 ? "]" : ", "));
        if (a.length == 0) out += "]";
        return out;
    }

    protected static void printSeqOut(int[] a) {
        System.err.print("[");
        for (int i = 0; i < a.length; i++) System.err.print(a[i] + (i == a.length - 1 ? "]" : ", "));
        if (a.length == 0) System.err.print("]");
        System.err.println("");
    }

    protected static int[] runLDE(int in) {
        perms = new ArrayList<>();
        permsWays = new ArrayList<>();
        permsfound = 0;
        if (Math.abs(in) < 2) {
            perms.add(new int[] {});
            return new int[] {};
        }
        int start = 0;
        while (start < ciuraR.length && ciuraR[start] >= Math.abs(in)) start++;
        int[] seq = new int[ciuraR.length - start];
        for (int i = start; i < ciuraR.length; i++) seq[i - start] = ciuraR[i];
        int[] gives;
        int[] best = new int[] {-999};
        int bestMin = Integer.MAX_VALUE;
        int bestLen = Integer.MAX_VALUE;
        for (int len = 1; len <= seq.length; len++) {
            int ways = 0;
            int[] accountBest = new int[len];
            int accountBestMin = Integer.MAX_VALUE;
            gives = new int[len];
            for (int i = 0; i < len; i++) gives[i] = 0;
            while (true) {
                int sum = 0;
                for (int j = 0; j < len; j++) sum += seq[j] * testOrder[gives[j]];
                if (sum == in && (in == 0 || gives[len - 1] != 0)) {
                    permsfound++;
                    int[] tryfor = new int[len];
                    for (int j = 0; j < len; j++) tryfor[j] = testOrder[gives[j]];
                    int min = 0;
                    for (int j = 0; j < len; j++) min += Math.abs(tryfor[j]);
                    if ((!rangeMode && (min < bestMin || (longestRange && min == bestMin && tryfor.length > best.length && (tryfor[len - 1] != 0 || in == 0)))) || (rangeMode && min < bestMin && tryfor.length <= bestLen && (tryfor[len - 1] != 0 || in == 0))) {
                        System.err.print(in + " PERM FOUND IN S" + min + "R" + tryfor.length + ": ");
                        printSeqOut(tryfor);
                        best = tryfor;
                        bestMin = min;
                        bestLen = tryfor.length;
                    }
                    if (min == accountBestMin && (tryfor[len - 1] != 0 || in == 0)) ways++;
                    if (min < accountBestMin && (tryfor[len - 1] != 0 || in == 0)) {
                        accountBest = tryfor;
                        accountBestMin = min;
                        ways = 1;
                    }
                }
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
            if (accountBestMin != Integer.MAX_VALUE && accountBestMin != 0) {
                perms.add(accountBest);
                permsWays.add(ways);
            }
        }
        return best;
    }


    // Kudos, Baeldung!
    protected static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    protected static double[] averageNonZero(int[] in) {
        int sum = 0;
        int count = 0;
        for (int i = 0; i < in.length; i++) {
            if (in[i] != 0) {
                sum += Math.abs(in[i]);
                count++;
            }
        }
        if (count == 0) return new double[] {0, Double.MAX_VALUE};
        return new double[] {count, round(1.0 * sum / count, 3)};
    }

    protected static String render(int val) {
        String out = val + ": " + perms.size() + " PERMS FROM TOTAL OF " + permsfound + ":\n";
        if (permsfound != 0) {
            for (int i = 0; i < perms.size(); i++) {
                int sum = 0;
                for (int j = 0; j < perms.get(i).length; j++) sum += Math.abs(perms.get(i)[j]);
                //+ ": A" + printSeq(averageNonZero(perms.get(i)))
                out += "\tS" + sum + "R" + perms.get(i).length + " " + permsWays.get(i) +  " WAY: " + printSeq(perms.get(i)) + ": A" + printSeq(averageNonZero(perms.get(i))) + "\n";
                //out += "R" + perms.get(i).length + ", S" + sum + " FOR " + val + ": " + printSeq(perms.get(i)) + ": A" + printSeq(averageNonZero(perms.get(i))) + "\n";
            }
        }
        else out += "\tS0R0 1 WAY: [] A:[0.0, NaN]\n";
        return out;
    }

    protected static int argsContain(String[] args, String match) {
        for (int i = 0; i < args.length; i++) if (args[i].toLowerCase().equals(match)) return i;
        return -1;
    }

    protected static void argSetup(String[] args) {
        int a = argsContain(args, "--range");
        if (a != -1) {
            try {
                min = Integer.valueOf(args[a + 1]);
                max = Integer.valueOf(args[a + 2]);
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING RANGE: " + args[a + 1] + ", " + args[a + 2]);
                System.err.println("DEFAULT WILL BE USED: 0, 100");
                min = 0;
                max = 100;
            }
        }
        a = argsContain(args, "--longest");
        if (a != -1) longestRange = true;
        a = argsContain(args, "--rangemode");
        if (a != -1) rangeMode = true;
        a = argsContain(args, "--mult");
        if (a != -1) {
            try {
                testRange = Integer.valueOf(args[a + 1]);
                if (testRange <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING MULTIPLIER RANGE: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: 5");
                testRange = 5;
            }
        }
    }

    public static void main(String[] args) {
        try {
            argSetup(args);
            testOrder = new int[2 * testRange + 1];
            for (int i = 1, j = 1; j <= testRange; i += 2, j++) {
                testOrder[i] = j;
                testOrder[i + 1] = -1 * j;
            }
            Path path = FileSystems.getDefault().getPath("CLDE-output.txt");
            Path allPerms = FileSystems.getDefault().getPath("CLDE-perms.txt");
            File output = new File("CLDE-output.txt");
            if (!output.createNewFile()) {
                output.delete();
                output.createNewFile();
            }
            File permOutput = new File("CLDE-perms.txt");
            if (!permOutput.createNewFile()) {
                permOutput.delete();
                permOutput.createNewFile();
            }
            PrintWriter argWrite = new PrintWriter(output, "UTF-8");
            argWrite.write("ARGS:\n\tRANGE: " + min + ", " + max + "\n\tMULTS: ABS(" + testRange + ")\n\tSIDE: " + (rangeMode && longestRange ? "LONGEST RANGE" : rangeMode ? "DISREGARD SUM" : "SHORTEST RANGE") + "\n\n");
            argWrite.close();
            for (int a = min; a <= max; a++) {
                int[] lde = runLDE(a);
                String currentOutput = Files.readString(path);
                PrintWriter outputWrite = new PrintWriter(output, "UTF-8");
                outputWrite.append(currentOutput + a + ": RANGE " + lde.length + ": " + printSeq(lde) + "\n");
                outputWrite.close();
                currentOutput = Files.readString(allPerms);
                PrintWriter permWriter = new PrintWriter(permOutput, "UTF-8");
                String render = render(a);
                permWriter.append(currentOutput + render);
                permWriter.close();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}