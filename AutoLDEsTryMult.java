import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class AutoLDEsTryMult {

    static int[] intialGaps = {261871, 112021, 47910, 20567, 8817, 3815, 1636, 701, 301, 132, 57, 23, 10, 4, 1};

    static double minMult = 2.0;
    static double maxMult = 2.0;

    static boolean decreaseMults = true;
    static double decreasePercent = 10;
    static double decreaseTarget = 2.0;

    static int testVal = 5;
    static int[] testOrder;

    static boolean coprimeForce = true;

    static ArrayList<Integer> aSeq = new ArrayList<>();
    static ArrayList<Integer> bSeq = new ArrayList<>();
    static ArrayList<Integer> cSeq = new ArrayList<>();

    static Random rng = new Random(0);

    static int[][] testCases = {{1000000, 100}, {1800000, 100}, {3200000, 100}, {5600000, 100}, {10000000, 100}, {18000000, 32}, {32000000, 32}, {56000000, 32}, {100000000, 32}, {180000000, 32}, {320000000, 32}};

    protected static void printSeqOut(ArrayList<Integer> a) {
        System.err.print("[");
        for (int i = 0; i < a.size(); i++) System.err.print(a.get(i) + (i == a.size() - 1 ? "]" : ", "));
        if (a.size() == 0) System.err.print("]");
        System.err.println("");
    }

    protected static String printSeq(ArrayList<Integer> a) {
        String out = "[";
        for (int i = 0; i < a.size(); i++) out += a.get(i) + (i == a.size() - 1 ? "]" : ", ");
        if (a.size() == 0) out += "]";
        return out;
    }

    protected static int gcd(int a, int b) {
        if (b == 0) return a;
        else return gcd(b, a % b);
    }

    // repurposed from aphitorite's Fast LDE Finder, ported to Java by PCBoyGames
    protected static boolean greater(int[] vLen, int[] vSum, int gcdComp, int gapMin, int a, int b) {
        if (vLen[a] + vSum[a] < vLen[b] + vSum[b]) return true;
        if (vLen[a] + vSum[a] == vLen[b] + vSum[b]) {
            if (vLen[a] < vLen[b]) return true;
            if (vLen[a] == vLen[b]) {
                if (vSum[a] < vSum[b]) return true;
                if (vSum[a] == vSum[b]) {
                    if (gcd(gcdComp, gapMin + a) > gcd(gcdComp, gapMin + b)) return true;
                    if (gcd(gcdComp, gapMin + a) == gcd(gcdComp, gapMin + b) && a < b) return true;
                }
            }
        }
        return false;
    }

    // repurposed from aphitorite's Fast LDE Finder, ported to Java by PCBoyGames
    protected static int getBest() {
        int[] cGaps = new int[bSeq.size()];
        for (int i = 0; i < cGaps.length; i++) cGaps[i] = bSeq.get(i);
        int gapMin = (int) (minMult * cGaps[0]);
        int gapMax = (int) (maxMult * cGaps[0]);
        int cCnt = cGaps.length;
        int[] currVec = new int[cCnt];
        int mulMax = testVal;
        int mulMin = -1 * mulMax;
        int mulRange = mulMax - mulMin;
        int currGap = 0;
        int currVLn = cCnt;
        int currSum = 0;
        for (int i = 0; i < cCnt; i++) {
            currVec[i] = mulMin;
            currGap += mulMin * cGaps[i];
            currSum += Math.abs(mulMin);
        }
        int gCnt = gapMax - gapMin + 1;
        int[] vLen = new int[gCnt];
        int[] vSum = new int[gCnt];
        for (int i = 0; i < gCnt; i++) {
            vLen[i] = cCnt + 1;
            vSum[i] = mulMax * gCnt;
        }
        while (true) {
            if (currGap >= gapMin && currGap <= gapMax) {
                int gIdx = currGap - gapMin;
                if (currVLn < vLen[gIdx]) {
                    vSum[gIdx] = currSum;
                    vLen[gIdx] = currVLn;
                }
                if (currVLn == vLen[gIdx] && currSum < vSum[gIdx]) vSum[gIdx] = currSum;
            }
            int i;
            for (i = 0; i < cCnt && currVec[i] == mulMax; i++) {
                currVec[i] = mulMin;
                currGap -= mulRange * cGaps[i];
            }
            if (i == cCnt) break;
            currVec[i]++;
            currGap += cGaps[i];
            if (currVec[i] > 0) currSum++;
            else currSum--;
            if (currVec[i] == 0 && i == currVLn - 1) currVLn--;
            if (currVec[i] != 0 && i >= currVLn) currVLn = i + 1;
        }
        int[] idx = new int[gCnt];
        for (int i = 0; i < gCnt; i++) idx[i] = i;
        for (int i, j, k = 1308699934, m; k > 0; k = (int) (k / 2.3601d)) for (j = k; j < gCnt; j++) for (i = j; i >= k && greater(vLen, vSum, cGaps[0], gapMin, idx[i - k], idx[i]); i -= k) {
            m = idx[i];
            idx[i] = idx[i - k];
            idx[i - k] = m;
        }
        int bestLen = 0;
        for (int i = 0; i < vLen.length; i++) if (vLen[i] > bestLen) bestLen = vLen[i];
        while (bSeq.size() > bestLen) bSeq.remove(bSeq.size() - 1);
        for (int k = 0, i = idx[k]; k < gCnt; k++) {
            if (!coprimeForce || gcd(gapMin + i, cGaps[0]) == 1) {
                int ret = gapMin + i;
                cGaps = new int[0];
                currVec = new int[0];
                vLen = new int[0];
                vSum = new int[0];
                idx = new int[0];
                return ret;
            }
            if (k + 1 < gCnt) i = idx[k + 1];
        }
        return 0;
    }

    protected static void cureLongSeq() {
        System.err.println("CURING SEQUENCE");
        while (true) {
            if (testVal < 1) testVal = 1;
            testOrder = new int[2 * testVal + 1];
            for (int i = 1, j = 1; i < testOrder.length; i += 2, j++) {
                testOrder[i] = j;
                testOrder[i + 1] = -1 * j;
            }
            ArrayList<Integer> curSet = new ArrayList<>();
            for (int i = (int) (bSeq.get(0) * minMult); i <= (int) (bSeq.get(0) * maxMult); i++) curSet.add(i);
            int len = 1;
            for (; len < bSeq.size() && !curSet.isEmpty(); len++) {
                int[] gives = new int[len];
                for (int i = 0; i < len; i++) gives[i] = 0;
                while (true) {
                    int sum = 0;
                    for (int j = 0; j < len; j++) sum += bSeq.get(j) * testOrder[gives[j]];
                    if (curSet.contains(sum)) curSet.remove(curSet.indexOf(sum));
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
            if (curSet.isEmpty()) {
                while (len - 1 < bSeq.size()) bSeq.remove(bSeq.size() - 1);
                printSeqOut(aSeq);
                printSeqOut(bSeq);
                for (int i = 0; i < bSeq.size(); i++) cSeq.add(bSeq.get(i));
                return;
            } else testVal++;
        }
    }

    protected static String findExtend() {
        String subtract = "";
        String extend = "";
        int[] ciura = {1, 4, 10, 23, 57, 132, 301, 701};
        int bar = 0;
        int j = aSeq.size() - 1;
        for (; j >= 0 && bar < ciura.length && aSeq.get(j) == ciura[bar]; bar++, j--);
        for (; bar < ciura.length; bar++) {
            if (subtract.length() == 0) subtract = "NO";
            subtract += ciura[bar] + (bar + 1 == ciura.length ? "_" : "-");
        }
        for (; j >= 0; j--) {
            if (extend.length() == 0) extend = "+";
            extend += aSeq.get(j) + (j == 0 ? "_" : "-");
        }
        return subtract + extend;
    }

    protected static void appendBest() {
        int add = getBest();
        aSeq.add(0, add);
        bSeq.add(0, add);
        printSeqOut(aSeq);
        printSeqOut(bSeq);
    }

    protected static void decrease() {
        double diffMin = minMult - decreaseTarget;
        double diffMax = maxMult - decreaseTarget;
        minMult -= (0.01 * decreasePercent) * diffMin;
        maxMult -= (0.01 * decreasePercent) * diffMax;
        System.err.println("NEW MULTS: " + minMult + "-" + maxMult);
    }

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

    protected static long shellPass(int[] array,int gap, int length) {
        long c = 0;
        for (int h = gap, i = h; i < length; i++) {
            int v = array[i], j = i;
            for (; j >= h && j - h >= 0 && comp(array[j - h], v, c++); j -= h) swap(array, j, j - h);
        }
        return c;
    }

    protected static long shell(int[] seq, int length) {
        long c = 0;
        int[] array = new int[length];
        for (int i = 0; i < length; i++) array[i] = i;
        for (int j = 0; j < length; j++) swap(array, j, randInt(j, length));
        for (int i = 0; i < seq.length; i++) c += shellPass(array, seq[i], length);
        array = new int[0];
        return c;
    }

    protected static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    protected static int leaderboarding(double v) {
        int[] seqTest = new int[aSeq.size()];
        for (int i = 0; i < aSeq.size(); i++) seqTest[i] = aSeq.get(i);
        int[] seqBest = {1410613638, 631316523, 282544094, 126451886, 56593218, 25328150, 11335549, 5073196, 2270496, 1016155, 454778, 203535, 91092, 40768, 18246, 8166, 3655, 1636, 701, 301, 132, 57, 23, 10, 4, 1};
        int win = 0;
        String basePath = new File("").getAbsolutePath();
        File _ALDEDirectory = new File(basePath + "/ALDE");
        _ALDEDirectory.mkdirs();
        for (int i = 0; i < testCases.length; i++) {
            int length = testCases[i][0];
            int times = testCases[i][1];
            rng = new Random(0);
            long cTest = 0;
            for (int j = 0; j < times; j++) cTest += shell(seqTest, length);
            double cTAvg = round(1.0 * cTest / times, 2);
            rng = new Random(0);
            long cBest = 0;
            for (int j = 0; j < times; j++) cBest += shell(seqBest, length);
            double cBAvg = round(1.0 * cBest / times, 2);
            System.err.println("");
            System.err.println("LENGTH: " + length + ", TRIALS: " + times);
            if (cTAvg < cBAvg) {
                System.err.println("AUTOLDE: " + cTAvg);
                win++;
            }
            System.err.println("+1636_F2.2344: " + cBAvg);
            if (cTAvg >= cBAvg) System.err.println("AUTOLDE: " + cTAvg);
            try {
                Path path = FileSystems.getDefault().getPath(basePath + "/ALDE/ALDE-" + v + ".txt");
                File output = new File(basePath + "/ALDE/ALDE-" + v + ".txt");
                if (!output.createNewFile()) {
                    //output.delete();
                    //output.createNewFile();
                }
                String currentOutput = Files.readString(path);
                PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                getWrite.append(currentOutput + "LENGTH: " + length + ", TRIALS: " + times + "\n" + (cTAvg < cBAvg ? ("AUTOLDE: " + cTAvg + "\n+1636_F2.2344: " + cBAvg) : ("+1636_F2.2344: " + cBAvg + "\nAUTOLDE: " + cTAvg)) + "\n\n");
                getWrite.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return win;
    }

    public static void main(String[] args) {
        boolean cured = false;
        for (double v = 2.335; v <= 2.4; v = round(0.005 + v, 3)) {
            minMult = round(v - 0.01, 4);
            maxMult = round(v + 0.01, 4);
            decreaseTarget = v;
            aSeq.clear();
            bSeq.clear();
            for (int i = 0; i < intialGaps.length; i++) aSeq.add(intialGaps[i]);
            for (int i = 0; i < aSeq.size(); i++) bSeq.add(aSeq.get(i));
            String extendString = findExtend();
            if (!cured) cureLongSeq();
            else {
                bSeq.clear();
                for (int i = 0; i < cSeq.size(); i++) bSeq.add(cSeq.get(i));
                printSeqOut(aSeq);
                printSeqOut(bSeq);
            }
            cured = true;
            double initMin = minMult;
            double initMax = maxMult;
            //System.err.println("|SEQUENCE NAME: AUTOLDE_" + extendString + initMin + "-" + initMax + (decreaseMults ? ("_DEC-" + decreasePercent + "%-" + decreaseTarget) : "") + (testVal != 5 ? "_ALTV-" + testVal : "") + (coprimeForce ? "_CP|" : "|"));
            while ((long) (bSeq.get(0)) < (long) testCases[testCases.length - 1][0]) {
                appendBest();
                if (decreaseMults) decrease();
            }
            System.err.println("\nCOMPLETE:");
            String name = "|SEQUENCE NAME: AUTOLDE_" + extendString + initMin + "-" + initMax + (decreaseMults ? ("_DEC-" + decreasePercent + "%-" + decreaseTarget) : "") + (testVal != 5 ? "_ALTV-" + testVal : "") + (coprimeForce ? "_CP|" : "|");
            System.err.print("/");
            for (int i = 1; i < name.length() - 1; i++) System.err.print("-");
            System.err.print("\\\n" + name + "\n\\");
            for (int i = 1; i < name.length() - 1; i++) System.err.print("-");
            System.err.println("/");
            printSeqOut(aSeq);
            int wintimes = leaderboarding(v);
            try {
                Path path = FileSystems.getDefault().getPath("ALDE-trials.txt");
                File output = new File("ALDE-trials.txt");
                if (!output.createNewFile()) {
                    //output.delete();
                    //output.createNewFile();
                }
                String currentOutput = Files.readString(path);
                PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                getWrite.append(currentOutput + "" + v + ": " + printSeq(aSeq) + ": WINS " + wintimes + " TIMES OF " + testCases.length + "\n");
                getWrite.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}