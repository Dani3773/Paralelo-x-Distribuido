package com.protcc.paralelo.paralelo.model;

import java.nio.file.Path;

public class SearchResult {
    private final String filePath;
    private final int lineNumber;
    private final String lineContent;

    // Construtor
    public SearchResult(String filePath, int lineNumber, String lineContent) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
    }

    // Métodos Getter
    public String getFilePath() {
        return filePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLineContent() {
        return lineContent;
    }

    @Override
    public String toString() {
        return String.format(
                "Arquivo: %s | Linha: %d | Conteudo: %s",
                Path.of(filePath).getFileName(),
                lineNumber,
                lineContent.trim()
        );
    }
}
