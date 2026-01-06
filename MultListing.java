public class MultListing {

    static int[] seq = {1,5,12,23,62,145,367,815,1968,4711,11969,27901,84801,
        213331,543749,1355339,3501671,8810089,21521774,
        58548857,157840433,410151271,1131376761};
    static int seqLen = seq.length;
    static int length = 2147483647;

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

    protected static void runDivisions() {
        int[] a = new int[seqLen + 1];
        for (int i = 0; i < seqLen; i++) a[i] = seq[i];
        a[seqLen] = length;
        double[] b = new double[a.length - 1];
        for (int i = 0; i < b.length; i++) b[i] = a[i + 1] / (a[i] * 1.0);
        printSeq(a);
        printSeq(b);
    }

    public static void main(String[] args) {
        runDivisions();
    }
}