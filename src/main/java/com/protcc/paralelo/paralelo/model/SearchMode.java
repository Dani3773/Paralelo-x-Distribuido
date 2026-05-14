package com.protcc.paralelo.paralelo.model;

public enum SearchMode {
    STANDARD_COMPARISON(
            1,
            "Comparacao padrao: sequencial normal x paralelo por arquivo"
    ),
    SEQUENTIAL_ALL(
            2,
            "Sequencial normal: procura do inicio ao fim e mostra todas as ocorrencias"
    ),
    SEQUENTIAL_FIRST(
            3,
            "Sequencial com parada: para quando encontra a primeira ocorrencia"
    ),
    SEQUENTIAL_BIDIRECTIONAL(
            4,
            "Sequencial bidirecional: procura do inicio e do fim do arquivo"
    ),
    SEQUENTIAL_SORTED(
            5,
            "Sequencial ordenado ABC: ordena as linhas e depois procura"
    ),
    PARALLEL_EXECUTOR_ALL(
            6,
            "Paralelo ExecutorService: divide os arquivos entre Threads"
    ),
    PARALLEL_MANUAL_THREADS(
            7,
            "Paralelo Thread manual: cria uma Thread para cada arquivo"
    ),
    PARALLEL_FIRST(
            8,
            "Paralelo com parada: para quando alguma Thread encontra a primeira ocorrencia"
    );

    private final int option;
    private final String description;

    SearchMode(int option, String description) {
        this.option = option;
        this.description = description;
    }

    public int getOption() {
        return option;
    }

    public String getDescription() {
        return description;
    }

    public static SearchMode fromOption(int option) {
        for (SearchMode mode : values()) {
            if (mode.option == option) {
                return mode;
            }
        }

        throw new IllegalArgumentException("Opcao invalida: " + option);
    }
}
