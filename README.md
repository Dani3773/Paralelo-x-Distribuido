```

It then prints the execution time and, in comparison mode, the speedup.

## Running

Run `com.protcc.paralelo.ParaleloApplication` from IntelliJ. The program reads the files in `src/main/java/com/protcc/paralelo/paralelo/texto`.

Terminal flow:

1. Choose the folder (larger or smaller dataset)
2. Type the name to search
3. Choose the search type
4. Read the results and the timing report

## Search strategies

| # | Strategy | Idea |
|---|---|---|
| 1 | Default comparison | Runs sequential, then parallel (`ExecutorService`), compares times and computes speedup. Finds all occurrences. |
| 2 | Sequential | One file at a time, line by line. |
| 3 | Sequential with early stop | Stops at the first match. |
| 4 | Sequential bidirectional | Within each file, scans from the start and the end at the same time (first and last line, second and second-to-last, ...). Finds all matches. |
| 5 | Sequential, ordered (A-Z) | Loads each file's lines (keeping the original line number), sorts them alphabetically, then searches. Reports the original line. |
| 6 | Parallel with `ExecutorService` | One task per file, scheduled on a thread pool. |
| 7 | Parallel with manual `Thread` | One thread created by hand per file. |
| 8 | Parallel with early stop | Threads search several files at once; the first thread to find the term sets a stop flag and only the first match is returned. |

Strategies 6 and 7 do the same job. The difference is that `ExecutorService` reuses a managed pool of threads, while manual threads are created and joined by the code itself.

## Observations from the tests

On the smaller dataset (searching a first name, 6 matches), the bidirectional variant took about 64 ms and the sorted variant about 84 ms. The bidirectional variant was slower than the plain sequential search in our tests because it loads the lines into memory to reach both ends. Thread overhead can also cancel the parallel gain on small inputs, which is exactly what the speedup metric makes visible.

## What to compare

- Sequential vs parallel time on the same input
- Speedup on the small vs the large dataset
- Early-stop vs full search
- `ExecutorService` vs manual threads

---

*The original Portuguese internal group guide is available in this repository's git history.*

