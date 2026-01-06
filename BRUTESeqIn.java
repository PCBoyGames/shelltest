import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class BRUTESeqIn {

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
                    for (int i = a + 1, j = 0; i < args.length; i++, j++)  seqError[j] = args[i];
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

    static int avg = 539582;
    static int var = 2000;

    static double minMult = 2.2;
    static double maxMult = 2.4;
    static int maxTimes = 2000;

    static int[] seq = {1, 4, 10, 23, 57, 132, 301, 701, 0, 0, 0, 0};
    static int[] array;

    static int[][] in = {
        {1636,3616},{1636,3620},{1636,3621},{1636,3625},{1636,3626},{1636,3627},{1636,3628},{1636,3651},{1636,3653},{1636,3654},{1636,3656},{1636,3657},{1636,3658},{1636,3659},{1636,3664},{1636,3683},{1636,3686},{1636,3687},{1636,3688},{1636,3689},{1636,3691},{1636,3692},{1636,3693},{1636,3694},{1636,3695},{1636,3696},{1636,3718},{1636,3719},{1636,3720},{1636,3722},{1636,3723},{1636,3724},{1636,3726},{1636,3727},{1636,3728},{1636,3731},{1636,3750},{1636,3751},{1636,3752},{1636,3753},{1636,3754},{1636,3755},{1636,3756},{1636,3757},{1636,3758},{1636,3760},{1636,3761},{1636,3762},{1636,3763},{1636,3783},{1636,3785},{1636,3786},{1636,3787},{1636,3788},{1636,3789},{1636,3790},{1636,3791},{1636,3792},{1636,3793},{1636,3794},{1636,3795},{1636,3796},{1636,3815},{1636,3817},{1636,3818},{1636,3819},{1636,3820},{1636,3821},{1636,3822},{1636,3823},{1636,3824},{1636,3825},{1636,3826},{1636,3827},{1636,3828},{1636,3831},{1636,3849},{1636,3850},{1636,3851},{1636,3852},{1636,3853},{1636,3854},{1636,3855},{1636,3856},{1636,3857},{1636,3858},{1636,3859},{1636,3860},{1636,3861},{1636,3862},{1636,3863},{1636,3882},{1636,3885},{1636,3886},{1636,3887},{1636,3888},{1636,3889},{1636,3890},{1636,3891},{1636,3892},{1636,3893},{1636,3894},{1636,3895},{1636,3898},{1636,3916},{1636,3917},{1636,3918},{1636,3919},{1636,3920},{1636,3921},{1636,3922},{1636,3923},{1636,3924},{1636,3925},{1636,3926},
        //{1654,3640},{1654,3641},{1654,3652},{1654,3653},{1654,3661},{1654,3672},{1654,3673},{1654,3677},{1654,3681},{1654,3682},{1654,3686},{1654,3694},{1654,3702},{1654,3706},{1654,3710},{1654,3714},{1654,3719},{1654,3723},{1654,3726},{1654,3727},{1654,3731},{1654,3735},{1654,3743},{1654,3747},{1654,3751},{1654,3755},{1654,3756},{1654,3759},{1654,3763},{1654,3764},{1654,3768},{1654,3772},{1654,3777},{1654,3780},{1654,3784},{1654,3785},{1654,3789},{1654,3793},{1654,3796},{1654,3797},{1654,3800},{1654,3801},{1654,3805},{1654,3809},{1654,3810},{1654,3813},{1654,3814},{1654,3817},{1654,3818},{1654,3821},{1654,3822},{1654,3826},{1654,3830},{1654,3833},{1654,3834},{1654,3838},{1654,3842},{1654,3846},{1654,3847},{1654,3850},{1654,3851},{1654,3854},{1654,3855},{1654,3858},{1654,3859},{1654,3863},{1654,3867},{1654,3868},{1654,3871},{1654,3875},{1654,3879},{1654,3883},{1654,3887},{1654,3888},{1654,3891},{1654,3892},{1654,3895},{1654,3896},{1654,3900},{1654,3904},{1654,3905},{1654,3908},{1654,3909},{1654,3912},{1654,3916},{1654,3917},{1654,3921},{1654,3924},{1654,3925},{1654,3928},{1654,3929},{1654,3932},{1654,3933},{1654,3937},{1654,3941},{1654,3942},{1654,3945},{1654,3946},{1654,3949},{1654,3950},{1654,3953},{1654,3954},{1654,3958},{1654,3962},{1654,3965},{1654,3966},
        //{1673,3692},{1673,3695},{1673,3699},{1673,3700},{1673,3701},{1673,3704},{1673,3708},{1673,3725},{1673,3726},{1673,3729},{1673,3736},{1673,3737},{1673,3741},{1673,3755},{1673,3762},{1673,3763},{1673,3764},{1673,3768},{1673,3770},{1673,3777},{1673,3794},{1673,3795},{1673,3798},{1673,3799},{1673,3800},{1673,3807},{1673,3810},{1673,3821},{1673,3824},{1673,3827},{1673,3828},{1673,3831},{1673,3833},{1673,3836},{1673,3837},{1673,3854},{1673,3857},{1673,3858},{1673,3861},{1673,3866},{1673,3867},{1673,3868},{1673,3869},{1673,3870},{1673,3873},{1673,3887},{1673,3891},{1673,3894},{1673,3895},{1673,3896},{1673,3897},{1673,3898},{1673,3900},{1673,3902},{1673,3905},{1673,3906},{1673,3909},{1673,3923},{1673,3926},{1673,3927},{1673,3928},{1673,3930},{1673,3931},{1673,3932},{1673,3935},{1673,3936},{1673,3939},{1673,3942},{1673,3953},{1673,3956},{1673,3960},{1673,3963},{1673,3964},{1673,3965},{1673,3966},{1673,3968},{1673,3969},{1673,3972},{1673,3979},{1673,3986},{1673,3990},{1673,3993},{1673,3996},{1673,3999},{1673,4000},{1673,4001},{1673,4002},{1673,4005},{1673,4009},
        //{1676,3689},{1676,3714},{1676,3716},{1676,3718},{1676,3720},{1676,3722},{1676,3724},{1676,3726},{1676,3734},{1676,3736},{1676,3747},{1676,3749},{1676,3751},{1676,3753},{1676,3759},{1676,3761},{1676,3767},{1676,3773},{1676,3774},{1676,3776},{1676,3778},{1676,3780},{1676,3782},{1676,3784},{1676,3786},{1676,3788},{1676,3790},{1676,3792},{1676,3796},{1676,3798},{1676,3809},{1676,3811},{1676,3813},{1676,3815},{1676,3817},{1676,3819},{1676,3821},{1676,3823},{1676,3825},{1676,3829},{1676,3831},{1676,3833},{1676,3836},{1676,3846},{1676,3848},{1676,3850},{1676,3852},{1676,3854},{1676,3856},{1676,3858},{1676,3860},{1676,3866},{1676,3868},{1676,3870},{1676,3873},{1676,3879},{1676,3881},{1676,3883},{1676,3885},{1676,3887},{1676,3889},{1676,3891},{1676,3893},{1676,3895},{1676,3897},{1676,3899},{1676,3903},{1676,3905},{1676,3906},{1676,3908},{1676,3910},{1676,3912},{1676,3916},{1676,3918},{1676,3920},{1676,3922},{1676,3924},{1676,3926},{1676,3928},{1676,3930},{1676,3932},{1676,3942},{1676,3943},{1676,3945},{1676,3947},{1676,3949},{1676,3951},{1676,3953},{1676,3955},{1676,3957},{1676,3959},{1676,3961},{1676,3963},{1676,3965},{1676,3969},{1676,3978},{1676,3980},{1676,3982},{1676,3984},{1676,3986},{1676,3988},{1676,3990},{1676,3992},{1676,3996},{1676,3998},{1676,4000},{1676,4002},{1676,4005},{1676,4011},{1676,4015},{1676,4017},{1676,4019},{1676,4021}
    };

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

    protected static String printSeq(int[] a) {
        String out = "[";
        for (int i = 0; i < a.length; i++) out += a[i] + (i == a.length - 1 ? "]" : ", ");
        if (a.length == 0) out += "]";
        return out;
    }

    protected static void printSeqOut(int[] a) {
        System.err.print("[");
        for (int i = 0; i < a.length; i++) System.err.print(a[i] + (i == a.length - 1 ? "]" : ", "));
        if (a.length == 0) System.err.print("]");
        System.err.println("");
    }

    protected static String render(long c) {
        return printSeq(seq) + "...: " + c;
    }

    protected static String acceptOrDeny(long c, int t) {
        if (c <= (-1 * var) + (t * avg)) return "ACCEPTED";
        else if (c >= var + (t * avg)) return "DENIED";
        return "NEUTRAL";
    }

    protected static boolean brute(int x, int y) {
        seq[seq.length - 2] = x;
        seq[seq.length - 1] = y;
        System.err.print("TEST BY " + printSeq(seq) + " ");
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
                    Path path = FileSystems.getDefault().getPath(basePath + "/BF-" + seq[seq.length - 4] + "-" + seq[seq.length - 3] + "/BF-output-" + x + ".txt");
                    File output = new File(basePath + "/BF-" + seq[seq.length - 4] + "-" + seq[seq.length - 3] + "/BF-output-" + x + ".txt");
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
                Path path = FileSystems.getDefault().getPath(basePath + "/BF-" + seq[seq.length - 4] + "-" + seq[seq.length - 3] + "/BF-output-NEUTRAL.txt");
                File output = new File(basePath + "/BF-" + seq[seq.length - 4] + "-" + seq[seq.length - 3] + "/BF-output-NEUTRAL.txt");
                if (!output.createNewFile()) {
                    //output.delete();
                    //output.createNewFile();
                }
                String currentOutput = Files.readString(path);
                PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                getWrite.append(currentOutput + render(comps) + ": POTENTIAL NEUTRAL\n");
                getWrite.close();
            } catch (IOException e) { e.printStackTrace();}
        }
        System.err.println("NEUTRAL");
        return false;
    }

    public static void main(String[] args) {
        //double[] get = SOD.getBrutesBounds(length);
        //var = (int) (10 * get[0]);
        //avg = (int) (get[1] * 1.1);
        array = new int[length];
        for (int i = 0; i < length; i++) array[i] = i;
        for (int j = 0; j < in.length; j++) {
            seq[seq.length - 4] = in[j][0];
            seq[seq.length - 3] = in[j][1];
            String basePath = new File("").getAbsolutePath();
            File _BFDirectory = new File(basePath + "/BF-" + seq[seq.length - 4] + "-" + seq[seq.length - 3]);
            _BFDirectory.mkdirs();
            int testCount = 0;
            int timesAcc = 0;
            int[] seqBase = new int[seq.length - 2];
            for (int i = 0; i < seq.length - 2; i++) seqBase[i] = seq[i];
            for (int x = (int) (minMult * seq[seq.length - 3]); x <= maxMult * seq[seq.length - 3] && x < length; x++) {
                int[] testX = new int[seq.length - 3];
                for (int i = 0; i < seq.length - 3; i++) testX[i] = seq[i];
                if (LDE.good(x, 4, testX)) {
                    System.err.println("X = " + x + " LDE IDEAL");
                    for (int y = (int) (minMult * x); y <= maxMult * x && y < length; y++) {
                        int[] testY = new int[seq.length - 2];
                        for (int i = 0; i < seq.length - 3; i++) testY[i] = seq[i];
                        testY[seq.length - 3] = x;
                        if (LDE.good(y, 5, testY)) {
                            //System.err.println("Y = " + y + " LDE IDEAL");
                            //for (int z = (int) (minMult * y); z <= maxMult * y && z < length; z++) {
                                testCount++;
                                if (brute(x, y)) timesAcc++;
                            //}
                        } //else System.err.println("Y = " + y + " LDE NOT IDEAL, SKIPPING");
                    }
                } else System.err.println("X = " + x + " LDE NOT IDEAL, SKIPPING");
            }
            if (timesAcc > 0) {
                try {
                    Path path = FileSystems.getDefault().getPath("BF-timesX.txt");
                    File output = new File("BF-timesX.txt");
                    if (!output.createNewFile()) {
                        //output.delete();
                        //output.createNewFile();
                    }
                    String currentOutput = Files.readString(path);
                    PrintWriter getWrite = new PrintWriter(output, "UTF-8");
                    getWrite.append(currentOutput + printSeq(seqBase) + ": " + timesAcc + " TIMES OF " + testCount + " (" + 100.0 * timesAcc / testCount + "%)\n");
                    getWrite.close();
                } catch (IOException e) { e.printStackTrace();}
            }
        }
    }
}