public class FastLDE {

    protected static int gcd(int a, int b) {
        if (b == 0) return a;
        else return gcd(b, a % b);
    }

    protected static boolean greater(int[] vLen, int[] vSum, int[] gcd, int[] gaps, int a, int b) {
        if (vLen[a] + vSum[a] < vLen[b] + vSum[b]) return true;
        if (vLen[a] + vSum[a] == vLen[b] + vSum[b]) {
            if (vLen[a] < vLen[b]) return true;
            if (vLen[a] == vLen[b]) {
                if (vSum[a] < vSum[b]) return true;
                if (vSum[a] == vSum[b]) {
                    if (gcd[a] > gcd[b]) return true;
                    if (gcd[a] == gcd[b] && gaps[a] > gaps[b]) return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[] cGaps = {694561309, 302581050, 131758049, 59520078, 26323235, 11557229, 5219369, 2345991, 1061990, 471263, 207462};
        //, 94001, 40988, 18283, 8055, 3592, 1581, 701, 301, 132, 57, 23, 10, 4, 1};
        int gapMin = (int) (2.2 * cGaps[0]);
        int gapMax = (int) (2.3 * cGaps[0]);
        int minT = 55;

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
        int[][] vectors = new int[gCnt][];
        int[] vLen = new int[gCnt];
        int[] vSum = new int[gCnt];
        int[] vCnt = new int[gCnt];

        for (int i = 0; i < gCnt; i++) {
            gapRange[i] = gapMin + i;
            gcds[i] = gcd(gapRange[i], cGaps[0]);
            vectors[i] = new int[] {0};
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

                    vectors[gIdx] = currVec;
                }
                if (currVLn == vLen[gIdx] && currSum < vSum[gIdx]) {
                    vSum[gIdx] = currSum;
                    vCnt[gIdx]++;

                    vectors[gIdx] = currVec;
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

        int lastR = vLen[idx[0]];
        int lastS = vSum[idx[0]];

        for (int k = 0, i = idx[k]; k < gCnt; k++) {
            if (vLen[i] < lastR || (vLen[i] == lastR && vSum[i] < lastS)) {
                lastR = vLen[i];
                lastS = vSum[i];
            }
            if (vLen[i] + vSum[i] >= minT) System.err.printf("%d - M%.3f T%d R%d S%-2d G%-2d (x%d)\n", gapRange[i], (double) gapRange[i] / cGaps[0], (vLen[i] + vSum[i]), vLen[i], vSum[i], gcds[i], vCnt[i]);
            if (k + 1 < gCnt) i = idx[k + 1];
        }
    }
}