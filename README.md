# Big-O Complexity — Variant 1

Empirical study of different algorithmic complexity classes using Java.

This project corresponds to **Variant 1 — Mapping the Complexity Landscape** from the Big Data course.

## Objective

The goal is to compare theoretical Big-O complexity with real execution-time measurements using synthetic datasets of increasing size.

Three different complexity classes are studied:

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
