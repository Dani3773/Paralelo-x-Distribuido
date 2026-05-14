package com.protcc.paralelo.paralelo.service;

import com.protcc.paralelo.paralelo.model.SearchResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ParallelSearchService {

    private final FileFinder fileFinder;
    private final int numThreads = Runtime.getRuntime().availableProcessors();

    public ParallelSearchService() {
        this.fileFinder = new FileFinder();
    }

    public List<SearchResult> search(String directoryPath, String searchTerm) throws IOException, InterruptedException {
        List<Path> filesToSearch = fileFinder.findTxtFiles(directoryPath);
        Pattern searchPattern = Pattern.compile("\\b" + Pattern.quote(searchTerm) + "\\b", Pattern.CASE_INSENSITIVE);
        List<SearchResult> allResults = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (Path file : filesToSearch) {
            tasks.add(() -> {
                processFile(file, searchPattern, allResults);
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        return allResults;
    }

    private void processFile(Path file, Pattern searchPattern, List<SearchResult> results) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String lineContent;
            int lineNumber = 1;

            while ((lineContent = reader.readLine()) != null) {
                if (searchPattern.matcher(lineContent).find()) {
                    results.add(new SearchResult(file.toString(), lineNumber, lineContent));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + file + " - " + e.getMessage());
        }
    }
}
