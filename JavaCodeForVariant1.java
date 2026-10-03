import java.util.Arrays;
import java.util.Random;

/**
* variant 1 - Mapping the Complexity panorama
*
* Empirical examine of 3 one of a kind complexity classes:
*
* 1. Binary seek -> O(log n)
* 2. Linear Traversal -> O(n)
* 3. Array Sorting -> O(n log n)
*
* Execution times are measured the usage of system.nanoTime().
*
* To reduce measurement noise:
* - datasets are generated outside the timed section;
* - JVM warm-up runs are achieved first;
* - each experiment is repeated numerous times;
* - the median execution time is mentioned;
* - very rapid operations are finished in batches.
*/

public class Variant1Benchmark {

    private static final int WARMUP_RUNS = 10;
    private static final int MEASURED_RUNS = 21;

    
    private static volatile long blackhole = 0;

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println(" Variant 1 - Mapping the Complexity Landscape");
        System.out.println("==============================================");

        System.out.println("Java version: "
                + System.getProperty("java.version"));

        System.out.println("Operating system: "
                + System.getProperty("os.name")
                + " "
                + System.getProperty("os.arch"));

        System.out.println("Warm-up runs: " + WARMUP_RUNS);
        System.out.println("Measured runs: " + MEASURED_RUNS);
        System.out.println();

        benchmarkBinarySearch();
        benchmarkLinearTraversal();
        benchmarkArraySorting();
    }

    // ============================================================
    // 1. BINARY SEARCH - O(log n)
    // ============================================================

    private static void benchmarkBinarySearch() {

        int[] sizes = {
                1_000,
                10_000,
                100_000,
                1_000_000,
                2_000_000
        };

        System.out.println("==============================================");
        System.out.println(" Binary Search - Expected O(log n)");
        System.out.println("==============================================");

        System.out.printf(
                "%12s %20s %20s%n",
                "n",
                "Time/search (ns)",
                "T/log2(n)"
        );

        for (int n : sizes) {

            /*
             * Generate an ordered synthetic dataset.
             * Dataset preparation is outside the timed section.
             */
            int[] data = new int[n];

            for (int i = 0; i < n; i++) {
                data[i] = i * 2;
            }

            /*
             * Use several targets so that the JVM cannot reduce
             * the benchmark to exactly the same search every time.
             */
            int[] targets = {
                    -1,
                    n / 3,
                    n,
                    (3 * n) / 2,
                    2 * n + 1
            };

            /*
             * A single Binary Search is extremely fast.
             * We therefore execute many searches in one timed block.
             */
            int batchSize = chooseBinaryBatchSize(n);

            // JVM warm-up
            for (int w = 0; w < WARMUP_RUNS; w++) {

                blackhole ^= runBinarySearchBatch(
                        data,
                        targets,
                        Math.max(10_000, batchSize / 10)
                );
            }

            double[] samples = new double[MEASURED_RUNS];

            for (int run = 0; run < MEASURED_RUNS; run++) {

                long start = System.nanoTime();

                long result = runBinarySearchBatch(
                        data,
                        targets,
                        batchSize
                );

                long end = System.nanoTime();

                blackhole ^= result;

                /*
                 * Convert total batch time to average time
                 * per individual search.
                 */
                samples[run] =
                        (end - start) / (double) batchSize;
            }

            double timePerSearch = median(samples);

            double normalized =
                    timePerSearch / log2(n);

            System.out.printf(
                    "%,12d %20.3f %20.3f%n",
                    n,
                    timePerSearch,
                    normalized
            );
        }

        System.out.println();
    }

    private static long runBinarySearchBatch(
            int[] data,
            int[] targets,
            int repetitions) {

        long result = 0;

        for (int i = 0; i < repetitions; i++) {

            int target =
                    targets[i % targets.length];

            result += Arrays.binarySearch(
                    data,
                    target
            );
        }

        return result;
    }

    private static int chooseBinaryBatchSize(int n) {

        if (n <= 10_000) {
            return 2_000_000;
        }

        if (n <= 100_000) {
            return 1_500_000;
        }

        return 1_000_000;
    }

    // ============================================================
    // 2. LINEAR TRAVERSAL - O(n)
    // ============================================================

    private static void benchmarkLinearTraversal() {

        int[] sizes = {
                1_000,
                10_000,
                100_000,
                1_000_000,
                2_000_000
        };

        System.out.println("==============================================");
        System.out.println(" Linear Traversal - Expected O(n)");
        System.out.println("==============================================");

        System.out.printf(
                "%12s %20s %24s%n",
                "n",
                "Time (ns)",
                "T/n (ns/element)"
        );

        for (int n : sizes) {

            /*
             * Create the synthetic dataset before timing.
             */
            int[] data =
                    createRandomArray(n, 1_000L + n);

            /*
             * Small traversals are also very fast, so multiple
             * traversals are grouped into the same timed block.
             */
            int batchSize =
                    chooseTraversalBatchSize(n);

            // JVM warm-up
            for (int w = 0; w < WARMUP_RUNS; w++) {

                blackhole ^= runTraversalBatch(
                        data,
                        Math.max(1, batchSize / 10)
                );
            }

            double[] samples =
                    new double[MEASURED_RUNS];

            for (int run = 0;
                 run < MEASURED_RUNS;
                 run++) {

                long start =
                        System.nanoTime();

                long result =
                        runTraversalBatch(
                                data,
                                batchSize
                        );

                long end =
                        System.nanoTime();

                blackhole ^= result;

                /*
                 * Divide by the number of traversals to obtain
                 * the estimated time of one complete traversal.
                 */
                samples[run] =
                        (end - start)
                                / (double) batchSize;
            }

            double time =
                    median(samples);

            double normalized =
                    time / n;

            System.out.printf(
                    "%,12d %20.3f %24.5f%n",
                    n,
                    time,
                    normalized
            );
        }

        System.out.println();
    }

    private static long runTraversalBatch(
            int[] data,
            int repetitions) {

        long checksum = 0;

        for (int r = 0; r < repetitions; r++) {

            long sum = 0;

            for (int value : data) {
                sum += value;
            }

            checksum ^= sum;
        }

        return checksum;
    }

    private static int chooseTraversalBatchSize(int n) {

        if (n <= 1_000) {
            return 20_000;
        }

        if (n <= 10_000) {
            return 5_000;
        }

        if (n <= 100_000) {
            return 500;
        }

        if (n <= 1_000_000) {
            return 50;
        }

        return 25;
    }

    // ============================================================
    // 3. ARRAY SORTING - O(n log n)
    // ============================================================

    private static void benchmarkArraySorting() {

        int[] sizes = {
                1_000,
                10_000,
                100_000,
                500_000,
                1_000_000
        };

        System.out.println("==============================================");
        System.out.println(" Array Sorting - Expected O(n log n)");
        System.out.println("==============================================");

        System.out.printf(
                "%12s %20s %24s%n",
                "n",
                "Time (ns)",
                "T/(n log2(n))"
        );

        for (int n : sizes) {

            /*
             * The original unsorted dataset is created before
             * measurements begin.
             */
            int[] source =
                    createRandomArray(
                            n,
                            2_000L + n
                    );

            // JVM warm-up
            for (int w = 0;
                 w < WARMUP_RUNS;
                 w++) {

                /*
                 * Each sort needs a fresh unsorted array.
                 * Cloning is outside the timed section.
                 */
                int[] warmData =
                        source.clone();

                Arrays.sort(warmData);

                blackhole ^=
                        warmData[n / 2];
            }

            double[] samples =
                    new double[MEASURED_RUNS];

            for (int run = 0;
                 run < MEASURED_RUNS;
                 run++) {

                /*
                 * Prepare the input before starting the timer.
                 */
                int[] data =
                        source.clone();

                long start =
                        System.nanoTime();

                Arrays.sort(data);

                long end =
                        System.nanoTime();

                blackhole ^=
                        data[n / 2];

                samples[run] =
                        end - start;
            }

            double time =
                    median(samples);

            double normalized =
                    time
                            / (n * log2(n));

            System.out.printf(
                    "%,12d %20.3f %24.3f%n",
                    n,
                    time,
                    normalized
            );
        }

        System.out.println();
    }

    // ============================================================
    // SYNTHETIC DATA GENERATION
    // ============================================================

    private static int[] createRandomArray(
            int n,
            long seed) {

        Random random =
                new Random(seed);

        int[] data =
                new int[n];

        for (int i = 0; i < n; i++) {
            data[i] =
                    random.nextInt();
        }

        return data;
    }

    // ============================================================
    // STATISTICAL UTILITIES
    // ============================================================

    private static double median(
            double[] values) {

        double[] copy =
                values.clone();

        Arrays.sort(copy);

        int middle =
                copy.length / 2;

        if (copy.length % 2 == 1) {
            return copy[middle];
        }

        return (
                copy[middle - 1]
                        + copy[middle]
        ) / 2.0;
    }

    // ============================================================
    // MATHEMATICAL UTILITIES
    // ============================================================

    private static double log2(int n) {

        return Math.log(n)
                / Math.log(2.0);
    }
}
