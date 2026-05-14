# Guia do Trabalho de Paralelismo

Este README e um guia interno do grupo. A ideia e ajudar todo mundo a entender como rodar, onde mexer e como explicar o projeto.

## Objetivo

O programa procura um nome dentro de arquivos `.txt`.

Ele deve mostrar:

```text
Arquivo: a1.txt | Linha: 85 | Conteudo: Jessica Herring
```

Depois disso, mostra os tempos e, quando for o modo de comparacao, o Speedup.

Formula:

```text
Speedup = tempo_sequencial / tempo_paralelo
```

## Como Rodar

No IntelliJ, rode a classe:

```text
com.protcc.paralelo.ParaleloApplication
```

Tambem funciona rodando:

```text
com.protcc.paralelo.paralelo.runner.SearchRunner
```

O programa sempre trabalha dentro desta pasta:

```text
src/main/java/com/protcc/paralelo/paralelo/texto
```

Fluxo no terminal:

```text
1. Escolher a pasta: maior ou menor
2. Digitar o nome que sera pesquisado
3. Escolher o tipo de pesquisa
4. Ver os resultados e o relatorio
```

Exemplo:

```text
Escolha uma pasta dentro de texto:
1 - maior
2 - menor

Digite o termo de busca:
Jessica Herring

Escolha o tipo de pesquisa:
1 - Comparacao padrao
...
```

## Tipos de Pesquisa

Use o menu para escolher um algoritmo.

### 1. Comparacao padrao

Roda dois modos:

- busca sequencial normal;
- busca paralela por arquivo usando `ExecutorService`.

Esse e o modo principal para apresentar Speedup.

### 2. Sequencial normal

Busca do inicio ao fim.

Caracteristicas:

- percorre todos os arquivos `.txt`;
- percorre todas as linhas;
- mostra todas as ocorrencias.

### 3. Sequencial com parada

Busca do inicio ao fim, mas para quando acha a primeira ocorrencia.

Use para explicar:

- e mais rapido quando so queremos saber se o nome existe;
- nao lista todas as ocorrencias.

### 4. Sequencial bidirecional

Busca dentro de cada arquivo por dois lados:

- uma parte comeca no inicio;
- outra parte comeca no fim;
- as duas caminham ate o meio.

Use para explicar uma forma diferente de varrer o arquivo.

### 5. Sequencial ordenado ABC

Carrega as linhas, ordena em ordem alfabetica e depois procura.

Importante:

- o resultado continua mostrando a linha original do arquivo;
- e um exemplo de pre-processamento antes da busca.

### 6. Paralelo com ExecutorService

Usa pool de Threads.

Ideia:

- cada arquivo vira uma tarefa;
- o Java distribui as tarefas entre as Threads;
- no final junta os resultados.

Esse e o paralelo mais organizado.

### 7. Paralelo com Thread manual

Cria uma `Thread` manualmente para cada arquivo.

Ideia:

- 5 arquivos = 5 Threads;
- cada Thread procura em um arquivo;
- o programa espera todas terminarem com `join()`.

Bom para mostrar uso direto de `Thread`.

### 8. Paralelo com parada

Varios arquivos sao pesquisados ao mesmo tempo.

Quando uma Thread encontra o nome:

- ela marca que encontrou;
- as outras param o quanto antes;
- o programa retorna uma ocorrencia.

Bom para explicar cooperacao entre Threads.

## Onde Esta Cada Coisa

```text
src/main/java/com/protcc/paralelo/
  ParaleloApplication.java
  paralelo/
    model/
      SearchResult.java
      SearchMode.java
    service/
      FileFinder.java
      SequentialSearchService.java
      ParallelSearchService.java
      SearchAlgorithmService.java
    runner/
      SearchRunner.java
    texto/
      maior/
      menor/
```

Resumo das classes:

- `ParaleloApplication`: entrada principal do projeto.
- `SearchRunner`: menu do terminal e relatorio.
- `SearchResult`: guarda arquivo, linha e conteudo encontrado.
- `SearchMode`: lista os tipos de pesquisa do menu.
- `FileFinder`: encontra arquivos `.txt`.
- `SequentialSearchService`: busca sequencial base.
- `ParallelSearchService`: busca paralela base.
- `SearchAlgorithmService`: algoritmos extras.

## O Que Comparar

Para a apresentacao, rodar algumas vezes:

```text
pasta menor + modo 1
pasta maior + modo 1
pasta maior + modos 2, 3, 6, 7 e 8
```

Anotar:

- nome buscado;
- pasta usada;
- algoritmo escolhido;
- tempo;
- quantidade encontrada;
- Speedup, quando tiver.

## Observacoes

- O projeto usa Java 21.
- Se o terminal usar Java 8, pode dar erro. Nesse caso, rodar pelo IntelliJ com Java 21.
- A funcionalidade principal ja esta pronta. Daqui para frente, evitar mudar o fluxo base sem necessidade.
