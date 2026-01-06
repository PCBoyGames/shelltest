import java.util.Arrays;
import java.util.Random;

public class ShellGapSeq {

    static boolean detailedComps = false;
    static boolean doInter = false;
    static boolean silent = false;

    static int length = 1000;
    static int testTry = 100;
    static int iterTry = 1000;
    static int seqLen = 8;

    static int[] seq;
    static int[] array;
    static boolean[] tested;

    static Random rng;
    static long seed = 0;
    static boolean useseed = true;
    static long distseed = 0;
    static boolean usedistseed = false;
    static long selseed = 0;
    static boolean useselseed = false;

    static int[] ciura = {1, 4, 10, 23, 57, 132, 301, 701};
    static int[] ciura1750 = {1, 4, 10, 23, 57, 132, 301, 701, 1750};

    static String dist = "ciura1750-2246";

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

    protected static boolean allTested() {
        for (int i = 0; i < seqLen; i++) if (!tested[i]) return false;
        return true;
    }

    protected static boolean contains(int v) {
        for (int i = 0; i < seqLen - 1; i++) if (seq[i] == v) return true;
        return false;
    }

    protected static int nextFit(int v) {
        for (int i = v + 1; i < length / 2; i++) {
            boolean trial = true;
            for (int j = (int) Math.round(i / 1.9); j < i * 1.9 && trial; j++) {
                if (contains(j)) {
                    trial = false;
                    i = (int) (j * 1.9) + 1;
                }
            }
            if (trial) return i;
        }
        return length / 2;
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
        for (int i = seqLen - 1; i >= 0; i--) c += shellPass(seq[i]);
        return c;
    }

    protected static void iterate() {
        if (useselseed) rng = new Random(selseed);
        else rng = new Random();
        boolean selectAny = false;
        int sel = 1;
        while (!selectAny && !allTested()) {
            sel = randInt(1, seqLen);
            int selVal = seq[sel];
            while (tested[sel] && seqLen > 2) {
                sel = randInt(1, seqLen);
                selVal = seq[sel];
            }
            rng = new Random(seed);
            long comps = 0;
            for (int b = 0; b < testTry; b++) {
                for (int i = 0; i < length; i++) swap(array, i, randInt(i, length));
                comps += shell();
            }
            long bestVal = comps;
            if (!silent) System.err.println("SELECTED " + selVal + ": " + comps + " COMPS");
            for (int i = sel; i < seqLen - 1; i++) seq[i] = seq[i + 1];
            int best = selVal;
            for (int a = nextFit(1); a < length / 2; a = nextFit(a)) {
                if (a != selVal) {
                    selectAny = true;
                    comps = 0;
                    seq[seqLen - 1] = a;
                    for (sel = seqLen - 1; seq[sel] < seq[sel - 1] && sel >= 1; sel--) swap(seq, sel - 1, sel);
                    rng = new Random(useseed ? seed : selVal);
                    for (int b = 0; b < testTry; b++) {
                        for (int i = 0; i < length; i++) swap(array, i, randInt(i, length));
                        comps += shell();
                    }
                    if (detailedComps && !silent) System.err.print("TESTED " + a + ": " + comps + " COMPS ");
                    if (comps < bestVal) {
                        best = a;
                        bestVal = comps;
                        if (!silent) System.err.print("NEW BEST" + (detailedComps ? "" : ": " + a + ": " + comps + " COMPS \n"));
                    }
                    if (detailedComps && !silent) System.err.println("");
                    for (int i = sel; i + 1 < seqLen; i++) swap(seq, i, i + 1);
                }
            }
            if (best != -1) {
                seq[seqLen - 1] = best;
                if (!silent) System.err.println("BEST IS " + best + ": " + bestVal + " COMPS");
            } else seq[seqLen - 1] = selVal;
            if (best != selVal && best != -1) {
                for (int i = 1; i < seqLen; i++) tested[i] = false;
                tested[0] = true;
                if (!silent) System.err.println("TESTED RESET");
            }
            for (sel = seqLen - 1; seq[sel] < seq[sel - 1] && sel >= 1; sel--) swap(seq, sel - 1, sel);
            tested[sel] = true;
        }
    }

    protected static void printSeq(int[] a) {
        System.err.print("[");
        for (int i = 0; i < a.length; i++) System.err.print(a[i] + (i == a.length - 1 ? "]" : ", "));
        System.err.println("");
    }

    protected static void printSeq(double[] a) {
        System.err.print("[");
        for (int i = 0; i < a.length; i++) System.err.print((Math.round(1000 * a[i]) / 1000.0) + (i == a.length - 1 ? "]" : ", "));
        System.err.println("");
    }

    protected static void printSeq(String[] a) {
        System.err.print("[");
        for (int i = 0; i < a.length; i++) System.err.print(a[i] + (i == a.length - 1 ? "]" : ", "));
        System.err.println("");
    }

    protected static void startTesting() {
        if (usedistseed) rng = new Random(distseed);
        else rng = new Random();
        if (dist.equals("random")) for (int i = 1; i < seqLen; i++) seq[i] = randInt(2, Math.max(length / 2, 3));
        if (dist.equals("forcehigh")) for (int i = 1; i < seqLen; i++) seq[i] = length;
        if (dist.equals("lenpower")) for (int i = 1; i < seqLen; i++) seq[i] = (int) Math.round(Math.pow(length, 1.0 * i / seqLen));
        if (dist.equals("randomlenpower")) for (int i = 1; i < seqLen; i++) seq[i] = (int) Math.round(Math.pow(length, 1.0 * randInt(1, length / 2) / (length / 2)));
        if (dist.equals("ciura")) {
            for (int i = 1; i < ciura.length && i < seqLen; i++) seq[i] = ciura[i];
            for (int i = ciura.length; i < seqLen; i++) seq[i] = (int) (2.25d * seq[i - 1]);
        }
        if (dist.equals("ciura-2246")) {
            for (int i = 1; i < ciura.length && i < seqLen; i++) seq[i] = ciura[i];
            for (int i = ciura.length; i < seqLen; i++) seq[i] = seq[i] = (int) (2.246d * seq[i - 1]);
        }
        if (dist.equals("ciura1750")) {
            for (int i = 1; i < ciura1750.length && i < seqLen; i++) seq[i] = ciura1750[i];
            for (int i = ciura1750.length; i < seqLen; i++) seq[i] = (int) (2.25d * seq[i - 1]);
        }
        if (dist.equals("ciura1750-2246")) {
            for (int i = 1; i < ciura1750.length && i < seqLen; i++) seq[i] = ciura1750[i];
            for (int i = ciura1750.length; i < seqLen; i++) seq[i] = (int) (2.246d * seq[i - 1]);
        }
        if (dist.equals("tokuda")) for (int i = 1; i < seqLen; i++) seq[i] = (int) Math.ceil((Math.pow(2.25, i + 1) - 1) / 1.25);
        if (dist.equals("tokudafloor")) for (int i = 1; i < seqLen; i++) seq[i] = (int) Math.floor((Math.pow(2.25, i + 1) - 1) / 1.25);
        if (dist.equals("aphimult")) {
            seq[0] = 1;
            for (int i = 1; i < seqLen; i++) seq[i] = seq[i - 1] >= 401 ? (int) Math.floor(2.26993 * (seq[i - 1] - 1) + 1) : (int) Math.ceil(2.39 * (seq[i - 1] + 1) - 1);
        }
        Arrays.sort(seq, 1, seqLen);
        seq[0] = 1;
        if (!silent) printSeq(seq);
        for (int i = 0; i < length; i++) array[i] = i;
        for (int i = 1; i < seqLen; i++) tested[i] = false;
        tested[0] = true;
        for (int i = 0; i < iterTry; i++) {
            if (!silent) System.err.println("ITERATION " + (i + 1));
            iterate();
            if (!silent) printSeq(seq);
            if (allTested()) {
                if (!silent) System.err.println("ALL CONVERGED");
                return;
            }
        }
    }

    protected static void runDivisions() {
        int[] a = new int[seqLen + 1];
        for (int i = 0; i < seqLen; i++) a[i] = seq[i];
        a[seqLen] = length;
        double[] b = new double[a.length - 1];
        for (int i = 0; i < b.length; i++) b[i] = a[i + 1] / (a[i] * 1.0);
        printSeq(a);
        printSeq(b);
    }

    protected static boolean validDistribution(String match) {
        String m = match.toLowerCase();
        return m.equals("random") || m.equals("forcehigh") || m.equals("lenpower") || m.equals("randomlenpower") || m.equals("ciura") || m.equals("ciura-2246") || m.equals("ciura1750") || m.equals("ciura1750-2246") || m.equals("tokuda") || m.equals("tokudafloor") || m.equals("aphimult");
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
        a = argsContain(args, "--itertry");
        if (a != -1) {
            try {
                iterTry = Integer.parseInt(args[a + 1]);
                if (iterTry <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING ITERATION TRIALS: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: MAX 1000");
                iterTry = 1000;
            }
        }
        a = argsContain(args, "--seqlen");
        if (a != -1) {
            try {
                seqLen = Integer.parseInt(args[a + 1]);
                if (seqLen <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING SEQUENCE LENGTH: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: 8");
                seqLen = 8;
            }
        }
        a = argsContain(args, "--dist");
        if (a != -1) {
            if (validDistribution(args[a + 1])) dist = args[a + 1];
            else {
                System.err.println("PROBLEM PARSING DISTIBUION: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: CIURA1750-2246");
                dist = "ciura1750-2246";
            }
        }
        a = argsContain(args, "--detail");
        if (a != -1) detailedComps = true;
        a = argsContain(args, "--silent");
        if (a != -1) silent = true;
        a = argsContain(args, "--seed");
        if (a != -1) {
            if (args[a + 1].toLowerCase().equals("vary")) useseed = false;
            else try {
                seed = Long.parseLong(args[a + 1]);
                useseed = true;
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING SEED: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: 0");
                seed = 0;
                useseed = true;
            }
        }
        a = argsContain(args, "--distseed");
        if (a != -1) {
            if (args[a + 1].toLowerCase().equals("vary")) usedistseed = false;
            else try {
                distseed = Long.parseLong(args[a + 1]);
                usedistseed = true;
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING DISTRIBUTION SEED: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: VARY");
                usedistseed = false;
            }
        }
        a = argsContain(args, "--selseed");
        if (a != -1) {
            if (args[a + 1].toLowerCase().equals("vary")) useselseed = false;
            else try {
                selseed = Long.parseLong(args[a + 1]);
                useselseed = true;
            } catch (NumberFormatException e) {
                System.err.println("PROBLEM PARSING SELECTION SEED: " + args[a + 1]);
                System.err.println("DEFAULT WILL BE USED: VARY");
                useselseed = false;
            }
        }
        a = argsContain(args, "--inter");
        if (a != -1) doInter = true;
    }

    public static void main(String[] args) {
        argSetup(args);
        array = new int[length];
        if (doInter) {
            int intermediation = seqLen;
            for (int i = 1; i <= intermediation; i++) {
                System.err.println("SEQLEN: " + i);
                seqLen = i;
                seq = new int[seqLen];
                tested = new boolean[seqLen];
                startTesting();
                runDivisions();
            }
        } else {
            seq = new int[seqLen];
            tested = new boolean[seqLen];
            startTesting();
            runDivisions();
        }
    }
}