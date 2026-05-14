package com.protcc.paralelo.paralelo.service;

import com.protcc.paralelo.paralelo.model.SearchResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class SearchAlgorithmService {

    private final FileFinder fileFinder = new FileFinder();

    public List<SearchResult> searchSequentialAll(String directoryPath, String searchTerm) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        Pattern pattern = createPattern(searchTerm);

        for (Path file : fileFinder.findTxtFiles(directoryPath)) {
            results.addAll(searchFileAll(file, pattern));
        }

        return results;
    }

    public List<SearchResult> searchSequentialFirst(String directoryPath, String searchTerm) throws IOException {
        Pattern pattern = createPattern(searchTerm);

        for (Path file : fileFinder.findTxtFiles(directoryPath)) {
            Optional<SearchResult> result = searchFileFirst(file, pattern);
            if (result.isPresent()) {
                return List.of(result.get());
            }
        }

        return List.of();
    }

    public List<SearchResult> searchSequentialBidirectional(String directoryPath, String searchTerm) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        Pattern pattern = createPattern(searchTerm);

        for (Path file : fileFinder.findTxtFiles(directoryPath)) {
            results.addAll(searchFileBidirectional(file, pattern));
        }

        results.sort(Comparator.comparing(SearchResult::getFilePath).thenComparingInt(SearchResult::getLineNumber));
        return results;
    }

    public List<SearchResult> searchSequentialSorted(String directoryPath, String searchTerm) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        Pattern pattern = createPattern(searchTerm);

        for (Path file : fileFinder.findTxtFiles(directoryPath)) {
            List<LineInfo> lines = readLineInfo(file);
            lines.sort(Comparator.comparing(line -> line.content.toLowerCase()));

            for (LineInfo line : lines) {
                if (pattern.matcher(line.content).find()) {
                    results.add(new SearchResult(file.toString(), line.number, line.content));
                }
            }
        }

        results.sort(Comparator.comparing(SearchResult::getFilePath).thenComparingInt(SearchResult::getLineNumber));
        return results;
    }

    public List<SearchResult> searchParallelExecutor(String directoryPath, String searchTerm) throws IOException, InterruptedException {
        List<Path> files = fileFinder.findTxtFiles(directoryPath);
        Pattern pattern = createPattern(searchTerm);
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Callable<List<SearchResult>>> tasks = new ArrayList<>();

        for (Path file : files) {
            tasks.add(() -> searchFileAll(file, pattern));
        }

        List<SearchResult> results = new ArrayList<>();
        for (Future<List<SearchResult>> future : executor.invokeAll(tasks)) {
            try {
                results.addAll(future.get());
            } catch (Exception e) {
                System.err.println("Erro em uma Thread: " + e.getMessage());
            }
        }

        executor.shutdown();
        results.sort(Comparator.comparing(SearchResult::getFilePath).thenComparingInt(SearchResult::getLineNumber));
        return results;
    }

    public List<SearchResult> searchParallelManualThreads(String directoryPath, String searchTerm) throws IOException, InterruptedException {
        List<Path> files = fileFinder.findTxtFiles(directoryPath);
        Pattern pattern = createPattern(searchTerm);
        List<SearchResult> results = Collections.synchronizedList(new ArrayList<>());
        List<Thread> threads = new ArrayList<>();

        for (Path file : files) {
            Thread thread = new Thread(() -> results.addAll(searchFileAll(file, pattern)));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        results.sort(Comparator.comparing(SearchResult::getFilePath).thenComparingInt(SearchResult::getLineNumber));
        return results;
    }

    public List<SearchResult> searchParallelFirst(String directoryPath, String searchTerm) throws IOException, InterruptedException {
        List<Path> files = fileFinder.findTxtFiles(directoryPath);
        Pattern pattern = createPattern(searchTerm);
        AtomicBoolean found = new AtomicBoolean(false);
        List<SearchResult> results = Collections.synchronizedList(new ArrayList<>());
        List<Thread> threads = new ArrayList<>();

        for (Path file : files) {
            Thread thread = new Thread(() -> {
                Optional<SearchResult> result = searchFileFirstWithStop(file, pattern, found);
                result.ifPresent(results::add);
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        results.sort(Comparator.comparing(SearchResult::getFilePath).thenComparingInt(SearchResult::getLineNumber));
        return results.isEmpty() ? List.of() : List.of(results.get(0));
    }

    private Pattern createPattern(String searchTerm) {
        return Pattern.compile("\\b" + Pattern.quote(searchTerm) + "\\b", Pattern.CASE_INSENSITIVE);
    }

    private List<SearchResult> searchFileAll(Path file, Pattern pattern) {
        List<SearchResult> results = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String lineContent;
            int lineNumber = 1;

            while ((lineContent = reader.readLine()) != null) {
                if (pattern.matcher(lineContent).find()) {
                    results.add(new SearchResult(file.toString(), lineNumber, lineContent));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + file + " - " + e.getMessage());
        }

        return results;
    }

    private Optional<SearchResult> searchFileFirst(Path file, Pattern pattern) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String lineContent;
            int lineNumber = 1;

            while ((lineContent = reader.readLine()) != null) {
                if (pattern.matcher(lineContent).find()) {
                    return Optional.of(new SearchResult(file.toString(), lineNumber, lineContent));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + file + " - " + e.getMessage());
        }

        return Optional.empty();
    }

    private Optional<SearchResult> searchFileFirstWithStop(Path file, Pattern pattern, AtomicBoolean found) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String lineContent;
            int lineNumber = 1;

            while (!found.get() && (lineContent = reader.readLine()) != null) {
                if (pattern.matcher(lineContent).find() && found.compareAndSet(false, true)) {
                    return Optional.of(new SearchResult(file.toString(), lineNumber, lineContent));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + file + " - " + e.getMessage());
        }

        return Optional.empty();
    }

    private List<SearchResult> searchFileBidirectional(Path file, Pattern pattern) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<SearchResult> results = new ArrayList<>();
        int left = 0;
        int right = lines.size() - 1;

        while (left <= right) {
            String leftLine = lines.get(left);
            if (pattern.matcher(leftLine).find()) {
                results.add(new SearchResult(file.toString(), left + 1, leftLine));
            }

            if (left != right) {
                String rightLine = lines.get(right);
                if (pattern.matcher(rightLine).find()) {
                    results.add(new SearchResult(file.toString(), right + 1, rightLine));
                }
            }

            left++;
            right--;
        }

        return results;
    }

    private List<LineInfo> readLineInfo(Path file) throws IOException {
        List<LineInfo> lines = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String lineContent;
            int lineNumber = 1;

            while ((lineContent = reader.readLine()) != null) {
                lines.add(new LineInfo(lineNumber, lineContent));
                lineNumber++;
            }
        }

        return lines;
    }

    private record LineInfo(int number, String content) {
    }
}
