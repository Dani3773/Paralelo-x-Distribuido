package com.protcc.paralelo.paralelo.runner;

import com.protcc.paralelo.paralelo.model.SearchResult;
import com.protcc.paralelo.paralelo.model.SearchMode;
import com.protcc.paralelo.paralelo.service.ParallelSearchService;
import com.protcc.paralelo.paralelo.service.SearchAlgorithmService;
import com.protcc.paralelo.paralelo.service.SequentialSearchService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class SearchRunner {

    private static final Path TEXT_FOLDER = Paths.get("src", "main", "java", "com", "protcc", "paralelo", "paralelo", "texto");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SequentialSearchService sequentialService = new SequentialSearchService();
        ParallelSearchService parallelService = new ParallelSearchService();
        SearchAlgorithmService algorithmService = new SearchAlgorithmService();

        System.out.println("=== Benchmarking: Busca Sequencial vs Paralela ===");

        try {
            String directory;
            String searchTerm;
            SearchMode searchMode;

            if (args.length >= 3) {
                directory = args[0];
                searchTerm = args[1];
                searchMode = SearchMode.fromOption(Integer.parseInt(args[2]));
            } else if (args.length >= 2) {
                directory = args[0];
                searchTerm = args[1];
                searchMode = SearchMode.STANDARD_COMPARISON;
            } else {
                directory = chooseFolder(scanner);

                System.out.print("\nDigite o termo de busca: ");
                searchTerm = scanner.nextLine();

                searchMode = chooseSearchMode(scanner);
            }

            if (searchMode != SearchMode.STANDARD_COMPARISON) {
                runSingleSearchMode(algorithmService, searchMode, directory, searchTerm);
                return;
            }

            System.out.println("\n[1] Executando Busca Sequencial...");
            long startSeq = System.nanoTime();
            List<SearchResult> seqResults = sequentialService.search(directory, searchTerm);
            long endSeq = System.nanoTime();
            double timeSeqMs = (endSeq - startSeq) / 1_000_000.0;

            System.out.println("[2] Executando Busca Paralela...");
            long startPar = System.nanoTime();
            List<SearchResult> parResults = parallelService.search(directory, searchTerm);
            long endPar = System.nanoTime();
            double timeParMs = (endPar - startPar) / 1_000_000.0;

            double speedup = timeSeqMs / timeParMs;

            System.out.println("\n========== RESULTADOS ENCONTRADOS ==========");
            printResults(seqResults);

            System.out.println("\n================ RELATORIO ================");
            System.out.printf("Tempo Sequencial : %.4f ms\n", timeSeqMs);
            System.out.printf("Tempo Paralelo   : %.4f ms\n", timeParMs);
            System.out.printf("Speedup Alcancado: %.2fx\n", speedup);
            System.out.println("===========================================");

            if (seqResults.size() == parResults.size()) {
                System.out.println("Integridade: OK (Ambas as buscas encontraram " + seqResults.size() + " itens)");
            } else {
                System.err.println("Aviso: Divergencia na contagem de resultados!");
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Erro durante os testes: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static String chooseFolder(Scanner scanner) throws IOException {
        List<Path> folders = listFolders(TEXT_FOLDER);

        System.out.println("\nEscolha uma pasta dentro de texto:");
        for (int i = 0; i < folders.size(); i++) {
            System.out.printf("%d - %s\n", i + 1, folders.get(i).getFileName());
        }

        int folderOption = readOption(scanner, 1, folders.size());
        return folders.get(folderOption - 1).toString();
    }

    private static SearchMode chooseSearchMode(Scanner scanner) {
        System.out.println("\nEscolha o tipo de pesquisa:");

        for (SearchMode mode : SearchMode.values()) {
            System.out.printf("%d - %s\n", mode.getOption(), mode.getDescription());
        }

        int option = readOption(scanner, 1, SearchMode.values().length);
        return SearchMode.fromOption(option);
    }

    private static void runSingleSearchMode(
            SearchAlgorithmService algorithmService,
            SearchMode searchMode,
            String directory,
            String searchTerm
    ) throws IOException, InterruptedException {
        System.out.println("\nExecutando: " + searchMode.getDescription());

        long start = System.nanoTime();
        List<SearchResult> results = switch (searchMode) {
            case SEQUENTIAL_ALL -> algorithmService.searchSequentialAll(directory, searchTerm);
            case SEQUENTIAL_FIRST -> algorithmService.searchSequentialFirst(directory, searchTerm);
            case SEQUENTIAL_BIDIRECTIONAL -> algorithmService.searchSequentialBidirectional(directory, searchTerm);
            case SEQUENTIAL_SORTED -> algorithmService.searchSequentialSorted(directory, searchTerm);
            case PARALLEL_EXECUTOR_ALL -> algorithmService.searchParallelExecutor(directory, searchTerm);
            case PARALLEL_MANUAL_THREADS -> algorithmService.searchParallelManualThreads(directory, searchTerm);
            case PARALLEL_FIRST -> algorithmService.searchParallelFirst(directory, searchTerm);
            case STANDARD_COMPARISON -> throw new IllegalStateException("Modo de comparacao deve ser executado separadamente.");
        };
        long end = System.nanoTime();
        double timeMs = (end - start) / 1_000_000.0;

        System.out.println("\n========== RESULTADOS ENCONTRADOS ==========");
        printResults(results);

        System.out.println("\n================ RELATORIO ================");
        System.out.printf("Algoritmo        : %s\n", searchMode.getDescription());
        System.out.printf("Tempo de execucao: %.4f ms\n", timeMs);
        System.out.printf("Total encontrado : %d itens\n", results.size());
        System.out.println("===========================================");
    }

    private static List<Path> listFolders(Path baseFolder) throws IOException {
        try (Stream<Path> paths = Files.list(baseFolder)) {
            return paths
                    .filter(Files::isDirectory)
                    .toList();
        }
    }

    private static int readOption(Scanner scanner, int min, int max) {
        while (true) {
            System.out.print("Opcao: ");
            String input = scanner.nextLine().trim();

            try {
                int option = Integer.parseInt(input);
                if (option >= min && option <= max) {
                    return option;
                }
            } catch (NumberFormatException ignored) {
                // Continua pedindo uma opcao valida.
            }

            System.out.printf("Digite uma opcao entre %d e %d.\n", min, max);
        }
    }

    private static void printResults(List<SearchResult> results) {
        if (results.isEmpty()) {
            System.out.println("Nenhuma ocorrencia encontrada.");
            return;
        }

        for (SearchResult result : results) {
            System.out.println(result);
        }
    }
}
