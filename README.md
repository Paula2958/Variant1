# Big-O Complexity — Variant 1

Empirical study of different algorithmic complexity classes using Java.

This project corresponds to **Variant 1 — Mapping the Complexity Landscape** from the Big Data course.

## Objective

The ultimate goal of this variant is to thoroughly compare theoretical Big-O complexity with real execution-time measurements whilst using a couple of synthetic datasets caracterised by their increasing size.

The three different complexity classes that are studied are the following:

| Algorithm | Expected Complexity | Normalization |
|---|---|---|
| Binary Search | O(log n) | T(n) / log₂(n) |
| Linear Traversal | O(n) | T(n) / n |
| Array Sorting | O(n log n) | T(n) / (n log₂(n)) |

If the theoretical complexity is a good description of the measured growth, the corresponding normalized values should remain approximately constant as the input size increases.

## Implementation

The experiments are implemented in Java in:

`Variant1Benchmark.java`

Execution times are measured using `System.nanoTime()`.

To make the measurements more reliable, the program uses:

- JVM warm-up runs
- Multiple measurements for each input size
- The median execution time
- Batching for very fast operations
- Input preparation outside the timed section
- Synthetic datasets with increasing input sizes

## Running the experiment

Compile the program:

```bash
javac Variant1Benchmark.java
```

Run it:

```bash
java Variant1Benchmark
```
The program prints the measured execution time and normalized value for each input size.

Example output structure:

```text
Binary Search - Expected O(log n)

           n     Time/search (ns)            T/log2(n)
       1,000                  ...                  ...
      10,000                  ...                  ...
     100,000                  ...                  ...
```

The exact execution times can vary depending on the computer, JVM and system load. The important result is the growth trend as `n` increases.

## Report

The accompanying report contains the experimental results, plots, normalized measurements and a comparison between the empirical behaviour and the theoretical Big-O complexity of each algorithm.

## Author

**Paula Hernández Varela**  
Data Science and Engineering  
Universidad de Las Palmas de Gran Canaria  
Academic Year 2026–2027
