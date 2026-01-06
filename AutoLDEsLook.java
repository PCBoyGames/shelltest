import java.util.ArrayList;

public class AutoLDEsLook {

    static int[] intialGaps = {1636, 701, 301, 132, 57, 23, 10, 4, 1};

    static double minMult = 2.2;
    static double maxMult = 2.25;

    static ArrayList<Integer> aSeq = new ArrayList<>();
    static ArrayList<Integer> bSeq = new ArrayList<>();

    static int[] testOrder = {0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5};

    protected static void printSeqOut(ArrayList<Integer> a) {
        System.err.print("[");
        for (int i = 0; i < a.size(); i++) System.err.print(a.get(i) + (i == a.size() - 1 ? "]" : ", "));
        if (a.size() == 0) System.err.print("]");
        System.err.println("");
    }

    protected static int gcd(int a, int b) {
        if (b == 0) return a;
        else return gcd(b, a % b);
    }

    // repurposed from aphitorite's Fast LDE Finder, ported to Java by PCBoyGames
    protected static boolean greater(int[] vLen, int[] vSum, int[] gcd, int[] gaps, int a, int b) {
        if (vLen[a] + vSum[a] < vLen[b] + vSum[b]) return true;
        if (vLen[a] + vSum[a] == vLen[b] + vSum[b]) {
            if (vLen[a] < vLen[b]) return true;
            if (vLen[a] == vLen[b]) {
                if (vSum[a] < vSum[b]) return true;
                if (vSum[a] == vSum[b]) {
                    if (gcd[a] > gcd[b]) return true;
                    if (gcd[a] == gcd[b] && gaps[a] < gaps[b]) return true;
                }
            }
        }
        return false;
    }

    protected static boolean greater(int[] vNTt, int[] vLen, int[] vSum, int[] gcd, int[] gaps, int a, int b) {
        if (vNTt[a] > vNTt[b]) return true;
        if (vNTt[a] == vNTt[b]) {
            if (vLen[a] + vSum[a] < vLen[b] + vSum[b]) return true;
                if (vLen[a] + vSum[a] == vLen[b] + vSum[b]) {
                if (vLen[a] < vLen[b]) return true;
                if (vLen[a] == vLen[b]) {
                    if (vSum[a] < vSum[b]) return true;
                    if (vSum[a] == vSum[b]) {
                        if (gcd[a] > gcd[b]) return true;
                        if (gcd[a] == gcd[b] && gaps[a] < gaps[b]) return true;
                    }
                }
            }
        }
        return false;
    }

    // repurposed from aphitorite's Fast LDE Finder, ported to Java by PCBoyGames
    protected static int getBest(int l) {
        int[] cGaps = new int[bSeq.size() + 1];
        for (int i = 1; i < cGaps.length; i++) cGaps[i] = bSeq.get(i - 1);
        cGaps[0] = l;
        int gapMin = (int) (minMult * cGaps[0]);
        int gapMax = (int) (maxMult * cGaps[0]);
        int cCnt = cGaps.length;
        int[] currVec = new int[cCnt];
        int mulMax = 5;
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
        int[] gapRange = new int[gapMax - gapMin + 1];
        int gCnt = gapRange.length;
        int[] gcds = new int[gCnt];
        int[] vLen = new int[gCnt];
        int[] vSum = new int[gCnt];
        int[] vCnt = new int[gCnt];
        for (int i = 0; i < gCnt; i++) {
            gapRange[i] = gapMin + i;
            gcds[i] = gcd(gapRange[i], cGaps[0]);
            vLen[i] = cCnt + 1;
            vSum[i] = mulMax * gCnt;
        }
        while (true) {
            if (currGap >= gapMin && currGap <= gapMax) {
                int gIdx = currGap - gapMin;
                if (currVLn < vLen[gIdx]) {
                    vSum[gIdx] = currSum;
                    vLen[gIdx] = currVLn;
                    vCnt[gIdx] = 1;
                }
                if (currVLn == vLen[gIdx] && currSum < vSum[gIdx]) {
                    vSum[gIdx] = currSum;
                    vCnt[gIdx]++;
                }
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
        for (int i, j, k = 1308699934, m; k > 0; k = (int) (k / 2.3601d)) for (j = k; j < gCnt; j++) for (i = j; i >= k && greater(vLen, vSum, gcds, gapRange, idx[i - k], idx[i]); i -= k) {
            m = idx[i];
            idx[i] = idx[i - k];
            idx[i - k] = m;
        }
        for (int k = 0, i = idx[k]; k < gCnt; k++) {
            if (gcds[i] == 1) return vLen[i] + vSum[i];
            if (k + 1 < gCnt) i = idx[k + 1];
        }
        return 0;
    }

    protected static int considerNext() {
        int[] cGaps = new int[bSeq.size()];
        for (int i = 0; i < cGaps.length; i++) cGaps[i] = bSeq.get(i);
        int gapMin = (int) (minMult * cGaps[0]);
        int gapMax = (int) (maxMult * cGaps[0]);
        int cCnt = cGaps.length;
        int[] currVec = new int[cCnt];
        int mulMax = 5;
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
        int[] gapRange = new int[gapMax - gapMin + 1];
        int gCnt = gapRange.length;
        int[] gcds = new int[gCnt];
        int[] vLen = new int[gCnt];
        int[] vSum = new int[gCnt];
        int[] vCnt = new int[gCnt];
        int[] vNTt = new int[gCnt];
        for (int i = 0; i < gCnt; i++) {
            gapRange[i] = gapMin + i;
            gcds[i] = gcd(gapRange[i], cGaps[0]);
            vLen[i] = cCnt + 1;
            vSum[i] = mulMax * gCnt;
            vNTt[i] = 0;
        }
        while (true) {
            if (currGap >= gapMin && currGap <= gapMax) {
                int gIdx = currGap - gapMin;
                if (vNTt[gIdx] == 0 && gcds[gIdx] == 1) {
                    vNTt[gIdx] = getBest(currGap);
                    System.err.print(".");
                }
                if (currVLn < vLen[gIdx]) {
                    vSum[gIdx] = currSum;
                    vLen[gIdx] = currVLn;
                    vCnt[gIdx] = 1;
                }
                if (currVLn == vLen[gIdx] && currSum < vSum[gIdx]) {
                    vSum[gIdx] = currSum;
                    vCnt[gIdx]++;
                }
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
        for (int i, j, k = 1308699934, m; k > 0; k = (int) (k / 2.3601d)) for (j = k; j < gCnt; j++) for (i = j; i >= k && greater(vNTt, vLen, vSum, gcds, gapRange, idx[i - k], idx[i]); i -= k) {
            m = idx[i];
            idx[i] = idx[i - k];
            idx[i - k] = m;
        }
        int bestLen = 0;
        for (int i = 0; i < vLen.length; i++) if (vLen[i] > bestLen) bestLen = vLen[i];
        while (bSeq.size() > bestLen) bSeq.remove(bSeq.size() - 1);
        System.err.print("\n");
        for (int k = 0, i = idx[k]; k < gCnt; k++) {
            if (gcds[i] == 1) return gapRange[i];
            if (k + 1 < gCnt) i = idx[k + 1];
        }
        return 0;
    }

    protected static void cureLongSeq() {
        System.err.println("CURING SEQUENCE");
        int[] testOrder = {0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5};
        ArrayList<Integer> gcd1 = new ArrayList<>();
        for (int i = (int) (bSeq.get(0) * minMult); i <= (int) (bSeq.get(0) * maxMult); i++) if (gcd(i, bSeq.get(0)) == 1) gcd1.add(i);
        int len = 1;
        for (; len < bSeq.size() && !gcd1.isEmpty(); len++) {
            int[] gives = new int[len];
            for (int i = 0; i < len; i++) gives[i] = 0;
            while (true) {
                int sum = 0;
                for (int j = 0; j < len; j++) sum += bSeq.get(j) * testOrder[gives[j]];
                if (gcd1.contains(sum)) gcd1.remove(gcd1.indexOf(sum));
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
        while (len - 1 < bSeq.size()) bSeq.remove(bSeq.size() - 1);
        printSeqOut(aSeq);
        printSeqOut(bSeq);
    }

    protected static void appendBest() {
        int add = considerNext();
        //getBest();
        aSeq.add(0, add);
        bSeq.add(0, add);
        printSeqOut(aSeq);
        printSeqOut(bSeq);
    }

    public static void main(String[] args) {
        for (int i = 0; i < intialGaps.length; i++) aSeq.add(intialGaps[i]);
        for (int i = 0; i < aSeq.size(); i++) bSeq.add(aSeq.get(i));
        cureLongSeq();
        while ((long) (bSeq.get(0) * maxMult) < (long) Integer.MAX_VALUE) appendBest();
    }
}