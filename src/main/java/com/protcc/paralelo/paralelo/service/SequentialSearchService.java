package com.protcc.paralelo.paralelo.service;

import com.protcc.paralelo.paralelo.model.SearchResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SequentialSearchService {

    private final FileFinder fileFinder;

    public SequentialSearchService() {
        this.fileFinder = new FileFinder();
    }

    public List<SearchResult> search(String directoryPath, String searchTerm) throws IOException {
        List<SearchResult> allResults = new ArrayList<>();
        Pattern searchPattern = Pattern.compile("\\b" + Pattern.quote(searchTerm) + "\\b", Pattern.CASE_INSENSITIVE);

        List<Path> filesToSearch = fileFinder.findTxtFiles(directoryPath);

        for (Path file : filesToSearch) {
            allResults.addAll(processFile(file, searchPattern));
        }

        return allResults;
    }

    private List<SearchResult> processFile(Path file, Pattern searchPattern) {
        List<SearchResult> fileResults = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String lineContent;
            int lineNumber = 1;

            while ((lineContent = reader.readLine()) != null) {
                if (searchPattern.matcher(lineContent).find()) {
                    fileResults.add(new SearchResult(file.toString(), lineNumber, lineContent));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + file + " - " + e.getMessage());
        }

        return fileResults;
    }
}
