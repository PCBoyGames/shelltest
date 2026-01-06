import java.util.ArrayList;

public class ForcePar {
    public static void main(String[] args) {

        // Init variables
        int[] arr = new int[32], arr2 = new int[arr.length], maxParArr = new int[arr.length];
        int maxPar = 0, indexM = arr.length - 1;
        ArrayList<Integer> gapList = new ArrayList<>();

        // Init gapList
        gapList.add(23);
        gapList.add(10);
        gapList.add(4);

        // Init arr2; arr will be set in the loop
        for (int i = 0; i < arr.length; i++) arr2[i] = 0;

        // Main loop
        // Runs until arr2 is all 1s
        while (notAll(arr2)) {

            // Init loop variables
            for (int i = 0; i < arr.length; i++) arr[i] = arr2[i];
            int par = 0, s = 0, index = arr.length - 1;

            // Attempt to run gaps, fail out if swaps are made
            // Killers must stay in the same order
            for (int gap : gapList) {
                boolean swaps = true;
                while (s == 0 && swaps) {
                    swaps = false;
                    for (int i = 0; i + gap < arr.length && s == 0; i++) {
                        if (arr[i] > arr[i + gap]) {
                            int temp = arr[i];
                            arr[i] = arr[i + gap];
                            arr[i + gap] = temp;
                            swaps = true;
                            s++;
                        }
                    }
                }
            }

            // If no swaps were made, check par
            if (s == 0) {
                boolean found = false;

                // The outside loop is the par value, starting at the worst overall case
                // We go down from there until we find a 1 > 0 pair or reach 0
                // The inside loop checks if there is 1 > 0 pair with that gap
                // If found, we set par and break out of both loops
                for (int i = arr.length - 1; i > 0 && !found; i--) {
                    for (int j = 0; j + i < arr.length && !found; j++) {
                        if (arr[j] > arr[j + i]) {
                            par = i;
                            found = true;
                        }
                    }
                }

                // If new worst par, set maxPar and maxParArr
                // Also print them
                if (par > maxPar) {
                    maxPar = par;
                    System.out.println("New Max Par: " + maxPar);
                    System.out.print("Max Par Array: ");
                    for (int i = 0; i < arr.length; i++) {
                        maxParArr[i] = arr[i];
                        System.out.print(maxParArr[i] + " ");
                    }
                    System.out.println();
                }
            }

            // Increment arr2
            boolean incremented = false;

            // Start at the end, move backwards
            while (!incremented && index >= 0) {

                // If we find a 1, change it to a 0 and move left
                if (arr2[index] == 1) {
                    arr2[index] = 0;
                    index--;
                }

                // But when we find a 0, we change it to a 1 and stop
                else {
                    arr2[index]++;
                    incremented = true;

                    // If we reach a new lowest index changed to a 1, set indexM and print it
                    // This is just to track progress on the exponential-adjacent search
                    if (index < indexM) {
                        indexM = index;
                        System.out.println("New IndexM: " + indexM);
                    }
                }

            // If we reach the front with all 1s, we stop as well
            // This is already covered by this loop's condition index >= 0
            }

        // End of main loop
        }

        // Print final results
        System.out.println("Max Par: " + maxPar);
        System.out.print("Max Par Array: ");
        for (int i = 0; i < arr.length; i++) System.out.print(maxParArr[i] + " ");
    }

    // Returns true if arr2 is not all 1s
    // Returns false if arr2 is all 1s
    // Used to control main loop
    static boolean notAll(int[] arr2) {
        for (int i = 0; i < arr2.length; i++) if (arr2[i] != 1) return true;
        return false;
    }
}