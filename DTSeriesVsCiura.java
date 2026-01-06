import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class DTSeriesVsCiura {

    public class DepthTargeted {

        static int[] array;
        static int length = 5000;
        static int testTry = 100;
        static int level = 2;

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
                    level = Integer.parseInt(args[a + 1]);
                    if (level < 1) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    System.err.println("PROBLEM PARSING LEVEL: " + args[a + 1]);
                    System.err.println("DEFAULT WILL BE USED: 2");
                    level = 2;
                }
            }
        }

        public static ArrayList<Integer> getGaps(String[] args) {
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
                int depth1 = 0, depth2 = 0;
                for (int a = 0; a < testTry; a++) {
                    for (int i = 0; i < length; i++) {
                        int j = randInt(i, length);
                        swap(array, i, j);
                    }
                    for (int g : gaps) shellPass(g);
                    int[] depths = shellPass(gap);
                    for (int d : depths) {
                        if (d < level) depth1++;
                        else if (d >= level) depth2++;
                    }
                }
                System.err.println("Gap: " + gap + " Depth 1: " + depth1 + " Depth 2: " + depth2);
                if (depth2 > depth1) {
                    System.err.println("Depth too high, trying to lower it.");
                    while (depth2 > depth1 && gap < length) {
                        gap++;
                        while (gcd(gaps.get(gaps.size() - 1), gap) != 1 && gap < length) gap++;
                        rng = new Random(seed);
                        System.arraycopy(original, 0, array, 0, length);
                        depth1 = 0;
                        depth2 = 0;
                        for (int a = 0; a < testTry; a++) {
                            for (int i = 0; i < length; i++) {
                                int j = randInt(i, length);
                                swap(array, i, j);
                            }
                            for (int g : gaps) shellPass(g);
                            int[] depths = shellPass(gap);
                            for (int d : depths) {
                                if (d < level) depth1++;
                                else if (d >= level) depth2++;
                            }
                        }
                        System.err.println("Gap: " + gap + " Depth 1: " + depth1 + " Depth 2: " + depth2);
                    }
                    gaps.add(gap);
                } else {
                    System.err.println("Depth too low, trying to raise it.");
                    while (depth2 <= depth1 && gap > 1) {
                        gap--;
                        while (gap >= length / 2) gap--;
                        while (gcd(gaps.get(gaps.size() - 1), gap) != 1 && gap > 1) gap--;
                        rng = new Random(seed);
                        System.arraycopy(original, 0, array, 0, length);
                        depth1 = 0;
                        depth2 = 0;
                        for (int a = 0; a < testTry; a++) {
                            for (int i = 0; i < length; i++) {
                                int j = randInt(i, length);
                                swap(array, i, j);
                            }
                            for (int g : gaps) shellPass(g);
                            int[] depths = shellPass(gap);
                            for (int d : depths) {
                                if (d < level) depth1++;
                                else if (d >= level) depth2++;
                            }
                        }
                        System.err.println("Gap: " + gap + " Depth 1: " + depth1 + " Depth 2: " + depth2);
                    }
                    if (gap == 1 && depth2 <= depth1) break;
                    gaps.add(gap);
                }
                System.arraycopy(original, 0, array, 0, length);
                if (gap == 1) break;
            }
            if (!gaps.contains(1)) gaps.add(1);
            System.err.println("Gaps used:");
            for (int g = gaps.size() - 1; g >= 0; g--) System.err.print(gaps.get(g) + " ");
            System.err.println();
            System.err.print("java ShellOnDemand.java --name RECURRENT_LEN-" + length + "_TEST-" + testTry + "_SEED-" + seed + "_LEVEL-" + level + " --length " + length + " --noredund --seq ");
            for (int g = gaps.size() - 1; g >= 1; g--) System.err.print(gaps.get(g) + " ");
            System.err.println();
            return gaps;
        }
    }

    public class DepthTargetedB {

        static int[] array;
        static int length = 5000;
        static int testTry = 100;
        static int level = 2;

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
                    level = Integer.parseInt(args[a + 1]);
                    if (level < 1) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    System.err.println("PROBLEM PARSING LEVEL: " + args[a + 1]);
                    System.err.println("DEFAULT WILL BE USED: 2");
                    level = 2;
                }
            }
        }

        public static ArrayList<Integer> getGaps(String[] args) {
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
                int depth1 = 0, depth2 = 0;
                for (int a = 0; a < testTry; a++) {
                    for (int i = 0; i < length; i++) {
                        int j = randInt(i, length);
                        swap(array, i, j);
                    }
                    for (int g : gaps) shellPass(g);
                    int[] depths = shellPass(gap);
                    for (int d : depths) {
                        if (d < level) depth1++;
                        else if (d >= level) depth2++;
                    }
                }
                System.err.println("Gap: " + gap + " Depth 1: " + depth1 + " Depth 2: " + depth2);

                // From here, try to binary search to the target depth. This sucks due to something
                // that Monte Carlo methods can't fix: aleatoric uncertainty, whereas we should just
                // have some sort of epistemic uncertainty control (trials are expensive). And we also
                // don't know whether Who is actually the first baseman (possibly not first instance).
                // This is all despite the fact that there is a relationship between gap and depth. So,
                // we do our best with what we have. Linear search is more accurate in this case, but
                // slower because we then negate epistemic uncertainty.
                System.err.println("Attempting to binary search to target depth: " + level);
                int left = depth2 > depth1 ? gap : 1;
                int right = depth2 > depth1 ? gaps.get(gaps.size() - 1) : gap;
                int mid = (left + right) / 2;
                while (left != right) {
                    rng = new Random(seed);
                    System.arraycopy(original, 0, array, 0, length);
                    depth1 = 0;
                    depth2 = 0;
                    for (int a = 0; a < testTry; a++) {
                        for (int i = 0; i < length; i++) {
                            int j = randInt(i, length);
                            swap(array, i, j);
                        }
                        for (int g : gaps) shellPass(g);
                        int[] depths = shellPass(mid);
                        for (int d : depths) {
                            if (d < level) depth1++;
                            else if (d >= level) depth2++;
                        }
                    }
                    System.err.println("Gap: " + mid + " Depth 1: " + depth1 + " Depth 2: " + depth2);
                    if (depth2 <= depth1) right = mid;
                    else left = mid + 1;
                    mid = (left + right) / 2;
                }
                gap = mid;

                if (depth2 > depth1) {
                    System.err.println("Depth too high, trying to lower it.");
                    while (depth2 > depth1 && gap < length) {
                        gap++;
                        while (gcd(gaps.get(gaps.size() - 1), gap) != 1 && gap < length) gap++;
                        rng = new Random(seed);
                        System.arraycopy(original, 0, array, 0, length);
                        depth1 = 0;
                        depth2 = 0;
                        for (int a = 0; a < testTry; a++) {
                            for (int i = 0; i < length; i++) {
                                int j = randInt(i, length);
                                swap(array, i, j);
                            }
                            for (int g : gaps) shellPass(g);
                            int[] depths = shellPass(gap);
                            for (int d : depths) {
                                if (d < level) depth1++;
                                else if (d >= level) depth2++;
                            }
                        }
                        System.err.println("Gap: " + gap + " Depth 1: " + depth1 + " Depth 2: " + depth2);
                    }
                    gaps.add(gap);
                } else {
                    System.err.println("Depth too low, trying to raise it.");
                    while (depth2 <= depth1 && gap > 1) {
                        gap--;
                        while (gap >= length / 2) gap--;
                        while (gcd(gaps.get(gaps.size() - 1), gap) != 1 && gap > 1) gap--;
                        rng = new Random(seed);
                        System.arraycopy(original, 0, array, 0, length);
                        depth1 = 0;
                        depth2 = 0;
                        for (int a = 0; a < testTry; a++) {
                            for (int i = 0; i < length; i++) {
                                int j = randInt(i, length);
                                swap(array, i, j);
                            }
                            for (int g : gaps) shellPass(g);
                            int[] depths = shellPass(gap);
                            for (int d : depths) {
                                if (d < level) depth1++;
                                else if (d >= level) depth2++;
                            }
                        }
                        System.err.println("Gap: " + gap + " Depth 1: " + depth1 + " Depth 2: " + depth2);
                    }
                    if (gap == 1 && depth2 <= depth1) break;
                    gaps.add(gap);
                }
                System.arraycopy(original, 0, array, 0, length);
                if (gap == 1) break;
            }
            if (!gaps.contains(1)) gaps.add(1);
            System.err.println("Gaps used:");
            for (int g = gaps.size() - 1; g >= 0; g--) System.err.print(gaps.get(g) + " ");
            System.err.println();
            System.err.print("java ShellOnDemand.java --name RECURRENTB_LEN-" + length + "_TEST-" + testTry + "_SEED-" + seed + "_LEVEL-" + level + " --length " + length + " --noredund --seq ");
            for (int g = gaps.size() - 1; g >= 1; g--) System.err.print(gaps.get(g) + " ");
            System.err.println();
            return gaps;
        }
    }

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

        public static ArrayList<Integer> getGaps(String[] args) {
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
            return gaps;
        }
    }

    public class DepthTargeted2B {

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

        public static ArrayList<Integer> getGaps(String[] args) {
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

                // From here, try to binary search to the target depth. This sucks due to something
                // that Monte Carlo methods can't fix: aleatoric uncertainty, whereas we should just
                // have some sort of epistemic uncertainty control (trials are expensive). And we also
                // don't know whether Who is actually the first baseman (possibly not first instance).
                // This is all despite the fact that there is a relationship between gap and depth. So,
                // we do our best with what we have. Linear search is more accurate in this case, but
                // slower because we then negate epistemic uncertainty.
                System.err.println("Attempting to binary search to target depth: " + level);
                int left = avDepth > level ? gap : 1;
                int right = avDepth > level ? gaps.get(gaps.size() - 1) : gap;
                int mid = (left + right) / 2;
                while (left != right) {
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
                        int[] depths = shellPass(mid);
                        depLen += depths.length;
                        for (int d : depths) avDepth += d;
                    }
                    avDepth /= depLen;
                    System.err.println("Gap: " + mid + " Depth: " + avDepth);
                    if (avDepth < level) right = mid;
                    else left = mid + 1;
                    mid = (left + right) / 2;
                }
                gap = mid;

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
            System.err.print("java ShellOnDemand.java --name RECURRENT2B_LEN-" + length + "_TEST-" + testTry + "_SEED-" + seed + "_LEVEL-" + level + " --length " + length + " --noredund --seq ");
            for (int g = gaps.size() - 1; g >= 1; g--) System.err.print(gaps.get(g) + " ");
            System.err.println();
            return gaps;
        }
    }

    public class DepthTargeted2C {

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
                try { seed = Long.parseLong(args[a + 1]);}
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

        public static ArrayList<Integer> getGaps(String[] args) {
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

                int gapL = gap, gapR = gap;

                if (gaps.get(gaps.size() - 1) >= 64) {
                    // From here, try to binary search to the target depth. This sucks due to something
                    // that Monte Carlo methods can't fix: aleatoric uncertainty, whereas we should just
                    // have some sort of epistemic uncertainty control (trials are expensive). And we also
                    // don't know whether Who is actually the first baseman (possibly not first instance).
                    // This is all despite the fact that there is a relationship between gap and depth. So,
                    // we do our best with what we have. Linear search is more accurate in this case, but
                    // slower because we then negate epistemic uncertainty.
                    System.err.println("Attempting to binary search to target depth: " + level);
                    int left = avDepth > level ? gap : 1;
                    int right = avDepth > level ? gaps.get(gaps.size() - 1) : gap;
                    int mid = (left + right) / 2;
                    while (left != right) {
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
                            int[] depths = shellPass(mid);
                            depLen += depths.length;
                            for (int d : depths) avDepth += d;
                        }
                        avDepth /= depLen;
                        System.err.println("Gap: " + mid + " Depth: " + avDepth);
                        if (avDepth < level + 0.025) right = mid;
                        else left = mid + 1;
                        mid = (left + right) / 2;
                    }
                    gapL = mid;
                    left = avDepth < level ? gap : 1;
                    right = avDepth < level ? gaps.get(gaps.size() - 1) : gap;
                    mid = (left + right) / 2;
                    while (left != right) {
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
                            int[] depths = shellPass(mid);
                            depLen += depths.length;
                            for (int d : depths) avDepth += d;
                        }
                        avDepth /= depLen;
                        System.err.println("Gap: " + mid + " Depth: " + avDepth);
                        if (avDepth > level - 0.025) left = mid + 1;
                        else right = mid;
                        mid = (left + right) / 2;
                    }
                    gapR = mid;
                }

                if (avDepth >= level) {
                    System.err.println("Depth too high, trying to lower it.");
                    gap = gapL;
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
                    gap = gapR;
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
            System.err.print("java ShellOnDemand.java --name RECURRENT2C_LEN-" + length + "_TEST-" + testTry + "_SEED-" + seed + "_LEVEL-" + level + " --length " + length + " --noredund --seq ");
            for (int g = gaps.size() - 1; g >= 1; g--) System.err.print(gaps.get(g) + " ");
            System.err.println();
            return gaps;
        }
    }

    public class CiuraGaps {
        public static ArrayList<Integer> getGaps(int max) {
            int[] ciura = {1, 4, 10, 23, 57, 132, 301, 701, 1636, 3657, 8172, 18235, 40764, 91064, 203519, 454741, 1016156, 2270499, 5073398, 11335582, 25328324, 56518561, 126451290, 282544198, 631315018};
            ArrayList<Integer> gaps = new ArrayList<>();
            for (int i = ciura.length - 1; i >= 0; i--) if (ciura[i] <= max) gaps.add(ciura[i]);
            return gaps;
        }
    }

    public class ShellOnDemand {

        static int length = 5000;
        static int testTry = 100;

        static long seed = 0;

        static boolean redundDo = true;

        static String fileName = "output";

        static ArrayList<Double> compsTable = new ArrayList<>();
        static ArrayList<Double> varTable = new ArrayList<>();
        static ArrayList<Double> redundTable = new ArrayList<>();
        static ArrayList<Double> depthsTable = new ArrayList<>();
        static ArrayList<ArrayList<Double>> passTable = new ArrayList<>();
        static ArrayList<Integer> overallDepthsTable = new ArrayList<>();
        static ArrayList<ArrayList<Integer>> depthsPerGapTable = new ArrayList<>();

        static int[] array;

        static int[] seq = {1, 4, 10, 23, 57, 132, 301, 701, 1636};

        static Random rng = new Random(seed);

        static long comps = 0;
        static double var;
        static ArrayList<Double> pass = new ArrayList<>();
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
            System.err.println("Trying to make " + length + " length array with " + testTry + " trials");
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
            boolean written = false;
            while (!written) {
                try {
                    Path path = FileSystems.getDefault().getPath("SOD-" + fileName + ".txt");
                    File output = new File("SOD-" + fileName + ".txt");
                    if (path.toFile().exists() == false) output.getParentFile().mkdirs();
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
        }
    }

    public static String[] argsFromList(ArrayList<Integer> gaps, int length, String name) {
        ArrayList<String> argsList = new ArrayList<>();
        argsList.add("--name");
        argsList.add(name + (name.equals("CIURA") ? "_1636_F2.2344_LDE" : "_LEN-" + length + "_TEST-100_SEED-0_LEVEL-2"));
        argsList.add("--length");
        argsList.add("" + length);
        argsList.add("--noredund");
        argsList.add("--file");
        argsList.add("DT_AND_CIURA/SOD-" + name + (name.equals("CIURA") ? "_1636_F2.2344_LDE" : ""));
        argsList.add("--seq");
        for (int i = gaps.size() - 1; i >= (name.equals("CIURA") ? 0 : 1); i--) argsList.add("" + gaps.get(i));
        String[] args = new String[argsList.size()];
        for (int i = 0; i < argsList.size(); i++) args[i] = argsList.get(i);
        return args;
    }

    public static void main(String[] unused) {
        for (int i = 32; i <= 1048576; i *= 2) {
            System.out.println("Length: " + i);
            String[] args = new String[] {"--length", "" + i, "--testtry", "100", "--seed", "0", "--level", "2"};
            ArrayList<Integer> dt1 = DepthTargeted.getGaps(args);
            ArrayList<Integer> dt1b = DepthTargetedB.getGaps(args);
            ArrayList<Integer> dt2 = DepthTargeted2.getGaps(args);
            ArrayList<Integer> dt2b = DepthTargeted2B.getGaps(args);
            ArrayList<Integer> dt2c = DepthTargeted2C.getGaps(args);
            ArrayList<Integer> ciura = CiuraGaps.getGaps(i);
            String[] args1 = argsFromList(dt1, i, "RECURRENT");
            String[] args1b = argsFromList(dt1b, i, "RECURRENTB");
            String[] args2 = argsFromList(dt2, i, "RECURRENT2");
            String[] args2b = argsFromList(dt2b, i, "RECURRENT2B");
            String[] args2c = argsFromList(dt2c, i, "RECURRENT2C");
            String[] argsCiura = argsFromList(ciura, i, "CIURA");
            ShellOnDemand.main(args1);
            ShellOnDemand.main(args1b);
            ShellOnDemand.main(args2);
            ShellOnDemand.main(args2b);
            ShellOnDemand.main(args2c);
            ShellOnDemand.main(argsCiura);
        }
    }
}