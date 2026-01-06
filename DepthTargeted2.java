import java.util.ArrayList;
import java.util.Random;

public class DepthTargeted2 {

    static int[] array;
    static int length = 5000;
    static int testTry = 100;
    static double level = 2;

    static long seed = 0;

    static Random rng = new Random(seed);

    protected static int randInt(int min, int max) {
        return rng.nextInt(max - min) + min;
    }

    protected static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    protected static boolean comp(int a, int b) {
        return a > b;
    }

    protected static int[] shellPass(int gap) {
        int[] depths = new int[length - gap > 0 ? length - gap : 0];
        for (int h = gap, i = h; i < length; i++) {
            int v = array[i], j = i, d = 1;
            for (; j >= h && j - h >= 0 && comp(array[j - h], v); j -= h, d++) swap(array, j, j - h);
            depths[i - gap] = d;
        }
        return depths;
    }

    protected static int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
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
        a = argsContain(args, "--level");
        if (a != -1) {
            try {
                level = Double.parseDouble(args[a + 1]);
                if (level < 1) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING LEVEL: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: 2");
                level = 2;
            }
        }
    }

    public static void main(String[] args) {
        argSetup(args);
        rng = new Random(0);
        array = new int[length];
        for (int i = 0; i < length; i++) array[i] = i;
        int[] original = new int[length];
        System.arraycopy(array, 0, original, 0, length);
        ArrayList<Integer> gaps = new ArrayList<>();
        gaps.add(length);
        for (int gap = randInt(1, length); gap >= 1; gap = randInt(1, gaps.get(gaps.size() - 1))) {
            System.err.println("Testing gap: " + gap);
            if (gcd(gaps.get(gaps.size() - 1), gap) != 1) continue;
            rng = new Random(seed);
            double avDepth = 0;
            long depLen = 0;
            for (int a = 0; a < testTry; a++) {
                for (int i = 0; i < length; i++) {
                    int j = randInt(i, length);
                    swap(array, i, j);
                }
                for (int g : gaps) shellPass(g);
                int[] depths = shellPass(gap);
                depLen += depths.length;
                for (int d : depths) avDepth += d;
            }
            avDepth /= depLen;
            System.err.println("Gap: " + gap + " Depth: " + avDepth);
            if (avDepth >= level) {
                System.err.println("Depth too high, trying to lower it.");
                while (avDepth >= level && gap < length) {
                    gap++;
                    while (gcd(gaps.get(gaps.size() - 1), gap) != 1 && gap < length) gap++;
                    rng = new Random(seed);
                    System.arraycopy(original, 0, array, 0, length);
                    avDepth = 0;
                    depLen = 0;
                    for (int a = 0; a < testTry; a++) {
                        for (int i = 0; i < length; i++) {
                            int j = randInt(i, length);
                            swap(array, i, j);
                        }
                        for (int g : gaps) shellPass(g);
                        int[] depths = shellPass(gap);
                        depLen += depths.length;
                        for (int d : depths) avDepth += d;
                    }
                    avDepth /= depLen;
                    System.err.println("Gap: " + gap + " Depth: " + avDepth);
                }
                gaps.add(gap);
            } else {
                System.err.println("Depth too low, trying to raise it.");
                while (avDepth <= level && gap > 1) {
                    gap--;
                    while (gap >= length / 2) gap--;
                    while (gcd(gaps.get(gaps.size() - 1), gap) != 1 && gap > 1) gap--;
                    rng = new Random(seed);
                    System.arraycopy(original, 0, array, 0, length);
                    avDepth = 0;
                    depLen = 0;
                    for (int a = 0; a < testTry; a++) {
                        for (int i = 0; i < length; i++) {
                            int j = randInt(i, length);
                            swap(array, i, j);
                        }
                        for (int g : gaps) shellPass(g);
                        int[] depths = shellPass(gap);
                        depLen += depths.length;
                        for (int d : depths) avDepth += d;
                    }
                    avDepth /= depLen;
                    System.err.println("Gap: " + gap + " Depth: " + avDepth);
                }
                if (gap == 1 && avDepth <= level) break;
                gaps.add(gap);
            }
            System.arraycopy(original, 0, array, 0, length);
            if (gap == 1) break;
        }
        if (!gaps.contains(1)) gaps.add(1);
        System.err.println("Gaps used:");
        for (int g = gaps.size() - 1; g >= 0; g--) System.err.print(gaps.get(g) + " ");
        System.err.println();
        System.err.print("java ShellOnDemand.java --name RECURRENT2_LEN-" + length + "_TEST-" + testTry + "_SEED-" + seed + "_LEVEL-" + level + " --length " + length + " --noredund --seq ");
        for (int g = gaps.size() - 1; g >= 1; g--) System.err.print(gaps.get(g) + " ");
        System.err.println();
    }
}