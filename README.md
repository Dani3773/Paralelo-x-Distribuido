# Guia do Trabalho de Paralelismo

Este README e um guia interno do grupo. A ideia e ajudar todo mundo a entender como rodar, onde mexer e como explicar o projeto.

## Objetivo

O programa procura um nome dentro de arquivos `.txt`.

Ele mostra:

```text
Arquivo: a1.txt | Linha: 85 | Conteudo: Jessica Herring
```

Depois mostra tempo de execucao e, no modo de comparacao, o Speedup.

Formula:

```text
Speedup = tempo_sequencial / tempo_paralelo
```

## Como Rodar

No IntelliJ, rode:

```text
com.protcc.paralelo.ParaleloApplication
```

O programa sempre usa os arquivos dentro de:

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

## Exemplos Usados nos Testes

Durante os testes, usamos dois tipos de busca:

```text
Primeiro nome:
Pasta: menor
Busca: Carolyn
Resultado: 6 ocorrencias

Nome completo:
Pasta: maior
Busca: Jessica Herring
Resultado: 1 ocorrencia
```

Os tempos abaixo sao aproximados. Eles podem mudar dependendo do computador e da execucao.

## Tipos de Pesquisa

### 1. Comparacao padrao

O que faz:

- roda a busca sequencial normal;
- depois roda a busca paralela com `ExecutorService`;
- compara os dois tempos;
- calcula o Speedup.

Como percorre:

- sequencial: um arquivo por vez, linha por linha, do inicio ao fim;
- paralelo: varios arquivos podem ser processados ao mesmo tempo, cada arquivo como uma tarefa.

Para ou nao para:

- nao para na primeira ocorrencia;
- procura todas as ocorrencias.

Exemplo com primeiro nome:

```text
Busca: Carolyn
Pasta: menor
Total encontrado: 6

Sequencial: aproximadamente 115.0863 ms
Paralelo: aproximadamente 22.5614 ms
Speedup: 5.10x
```

Exemplo com nome completo:

```text
Busca: Jessica Herring
Pasta: maior
Total encontrado: 1

Sequencial: aproximadamente 154.0655 ms
Paralelo: aproximadamente 28.6824 ms
Speedup: 5.37x
```

Comparacao:

- esse modo e o principal para falar de Speedup;
- nos dois exemplos, o paralelo foi mais rapido;
- se a pasta for pequena, o sequencial pode se aproximar ou ate ganhar, porque Threads tambem tem custo.

### 2. Sequencial normal

O que faz:

- pesquisa todos os arquivos `.txt` da pasta escolhida;
- procura todas as ocorrencias;
- nao usa Threads.

Como percorre:

- pega o primeiro arquivo;
- le da primeira ate a ultima linha;
- passa para o proximo arquivo;
- repete ate terminar todos os `.txt`.

Para ou nao para:

- nao para quando encontra;
- continua ate o final para listar tudo.

Exemplo com primeiro nome:

```text
Busca: Carolyn
Pasta: menor
Tempo: aproximadamente 59.1069 ms
Total encontrado: 6
```

Exemplo com nome completo:

```text
Busca: Jessica Herring
Pasta: maior
Tempo: aproximadamente 105.4521 ms
Total encontrado: 1
```

Comparacao:

- concorrente direto: modo 3, sequencial com parada;
- modo 2 e melhor quando precisamos listar todas as ocorrencias;
- modo 3 costuma ser mais rapido quando so precisamos da primeira ocorrencia;
- no teste `Jessica Herring`, modo 2 continuou procurando depois de achar, por isso foi mais lento que o modo 3.

### 3. Sequencial com parada

O que faz:

- pesquisa em ordem, igual ao sequencial normal;
- para assim que encontra a primeira ocorrencia.

Como percorre:

- comeca no primeiro arquivo;
- le da primeira linha em diante;
- se encontrar, encerra a busca;
- se nao encontrar, passa para o proximo arquivo.

Para ou nao para:

- para na primeira ocorrencia;
- nao lista todas as ocorrencias.

Exemplo com primeiro nome:

```text
Busca: Carolyn
Pasta: menor
Tempo: aproximadamente 28.9922 ms
Total encontrado: 1
Resultado: arq_1.txt, linha 1, Carolyn Hale
```

Exemplo com nome completo:

```text
Busca: Jessica Herring
Pasta: maior
Tempo: aproximadamente 43.7671 ms
Total encontrado: 1
Resultado: a1.txt, linha 85, Jessica Herring
```

Comparacao:

- concorrente direto: modo 2, sequencial normal;
- modo 3 foi mais rapido nos dois exemplos porque parou cedo;
- ele nao serve quando a pergunta e "quantas vezes aparece?";
- ele serve melhor quando a pergunta e "existe esse nome?".

### 4. Sequencial bidirecional

O que faz:

- pesquisa todos os arquivos;
- dentro de cada arquivo, procura do inicio e do fim ao mesmo tempo.

Como percorre dentro de cada arquivo:

```text
linha 1, ultima linha
linha 2, penultima linha
linha 3, antepenultima linha
...
```

Para ou nao para:

- nao para na primeira ocorrencia;
- procura todas as ocorrencias.

Exemplo com primeiro nome:

```text
Busca: Carolyn
Pasta: menor
Tempo: aproximadamente 64.3686 ms
Total encontrado: 6
```

Exemplo com nome completo:

```text
Busca: Jessica Herring
Pasta: maior
Tempo: aproximadamente 138.2704 ms
Total encontrado: 1
```

Comparacao:

- concorrente direto: modo 2, sequencial normal;
- modo 4 usa uma estrategia diferente, mas ainda precisa ler o arquivo inteiro para listar tudo;
- no teste, foi mais lento que o sequencial normal;
- isso acontece porque ele carrega as linhas na memoria para conseguir acessar inicio e fim.

### 5. Sequencial ordenado ABC

O que faz:

- carrega as linhas de cada arquivo;
- guarda o numero original da linha;
- ordena as linhas em ordem alfabetica;
- depois procura o termo.

Como percorre:

- um arquivo por vez;
- le todas as linhas;
- ordena pelo conteudo;
- procura na lista ordenada;
- mostra a linha original no resultado.

Para ou nao para:

- nao para na primeira ocorrencia;
- procura todas as ocorrencias.

Exemplo com primeiro nome:

```text
Busca: Carolyn
Pasta: menor
Tempo: aproximadamente 83.7579 ms
Total encontrado: 6
```

Exemplo com nome completo:

```text
Busca: Jessica Herring
Pasta: maior
Tempo: aproximadamente 261.4032 ms
Total encontrado: 1
```

Comparacao:

- concorrente direto: modo 2, sequencial normal;
- modo 5 foi mais lento nos dois exemplos;
- motivo: alem de procurar, ele tambem faz ordenacao;
- serve para mostrar uma estrategia com pre-processamento, mas para essa busca simples nao compensa.

### 6. Paralelo com ExecutorService

O que faz:

- usa um pool de Threads controlado pelo Java;
- cada arquivo vira uma tarefa;
- o Java distribui as tarefas entre as Threads.

Quantidade de Threads:

```text
Threads criadas = Runtime.getRuntime().availableProcessors()
Tarefas criadas = quantidade de arquivos .txt
```

Exemplo:

```text
Computador com 8 processadores disponiveis
Pasta com 20 arquivos .txt

Modo 6:
- cria 8 Threads no pool;
- cria 20 tarefas, uma para cada arquivo;
- as tarefas entram em uma fila;
- as Threads pegam tarefas ate acabar.
```

Como percorre:

- varios arquivos podem ser lidos ao mesmo tempo;
- dentro de cada arquivo, a leitura e do inicio ao fim;
- as Threads sao reutilizadas.

Para ou nao para:

- nao para na primeira ocorrencia;
- procura todas as ocorrencias.

Exemplo com primeiro nome:

```text
Busca: Carolyn
Pasta: menor
Tempo: aproximadamente 58.4827 ms
Total encontrado: 6
```

Exemplo com nome completo:

```text
Busca: Jessica Herring
Pasta: maior
Tempo: aproximadamente 137.1476 ms
Total encontrado: 1
```

Comparacao:

- concorrente direto: modo 7, paralelo com Thread manual;
- modo 6 cria um numero controlado de Threads;
- modo 7 cria uma Thread por arquivo;
- modo 6 tende a ser mais organizado e melhor quando existem muitos arquivos;
- nos testes pequenos, os tempos ficaram proximos.

### 7. Paralelo com Thread manual

O que faz:

- cria uma `Thread` manualmente para cada arquivo `.txt`;
- cada Thread pesquisa um arquivo.

Quantidade de Threads:

```text
Threads criadas = quantidade de arquivos .txt
Tarefas criadas = quantidade de arquivos .txt
```

Exemplo:

```text
Pasta com 20 arquivos .txt

Modo 7:
- cria 20 Threads;
- cada Thread fica responsavel por 1 arquivo;
- nao existe fila controlada;
- as Threads nao sao reutilizadas.
```

Como percorre:

- todos os arquivos podem comecar quase ao mesmo tempo;
- dentro de cada arquivo, a leitura e do inicio ao fim;
- o programa espera todas terminarem com `join()`.

Para ou nao para:

- nao para na primeira ocorrencia;
- procura todas as ocorrencias.

Exemplo com primeiro nome:

```text
Busca: Carolyn
Pasta: menor
Tempo: aproximadamente 49.0012 ms
Total encontrado: 6
Observacao: pasta menor tem 5 arquivos, entao cria 5 Threads.
```

Exemplo com nome completo:

```text
Busca: Jessica Herring
Pasta: maior
Tempo: aproximadamente 140.5146 ms
Total encontrado: 1
Observacao: pasta maior tem 7 arquivos, entao cria 7 Threads.
```

Comparacao:

- concorrente direto: modo 6, paralelo com ExecutorService;
- modo 7 e mais facil de visualizar porque cada arquivo ganha uma Thread;
- pode ficar pesado se houver muitos arquivos;
- no teste `Carolyn`, ficou um pouco mais rapido que o modo 6;
- no teste `Jessica Herring`, ficou um pouco mais lento que o modo 6.

### Diferenca direta entre 6 e 7

```text
Modo 6:
- Threads = Runtime.getRuntime().availableProcessors()
- Arquivos = tarefas em uma fila
- Threads sao reutilizadas
- Mais organizado

Modo 7:
- Threads = quantidade de arquivos .txt
- Cada arquivo ganha uma Thread propria
- Threads nao sao reutilizadas
- Mais simples para visualizar
```

### 8. Paralelo com parada

O que faz:

- pesquisa varios arquivos ao mesmo tempo;
- usa Threads;
- retorna apenas a primeira ocorrencia encontrada.

Como percorre:

- cria Threads para os arquivos;
- cada Thread comeca lendo seu arquivo do inicio;
- se uma Thread encontra o termo, marca uma flag de parada;
- as outras Threads verificam essa flag e param o quanto antes.

Para ou nao para:

- para quando alguma Thread encontra uma ocorrencia;
- nao lista todas as ocorrencias.

Exemplo com primeiro nome:

```text
Busca: Carolyn
Pasta: menor
Tempo: aproximadamente 39.7697 ms
Total encontrado: 1
Resultado: arq_1.txt, linha 1, Carolyn Hale
```

Exemplo com nome completo:

```text
Busca: Jessica Herring
Pasta: maior
Tempo: aproximadamente 60.6472 ms
Total encontrado: 1
Resultado: a1.txt, linha 85, Jessica Herring
```

Comparacao:

- concorrente direto: modo 3, sequencial com parada;
- modo 8 usa Threads e parada antecipada;
- modo 3 tambem para cedo, mas nao tem custo de Threads;
- nos dois exemplos, modo 3 foi mais rapido porque encontrou cedo;
- modo 8 pode ser melhor se o nome estiver em um arquivo mais distante e uma Thread encontrar antes do sequencial chegar nele.

## Ordem Esperada de Velocidade

Nao existe uma ordem fixa que sempre vai acontecer. O tempo muda conforme:

- tamanho dos arquivos;
- quantidade de arquivos;
- se o nome aparece no inicio ou no fim;
- se precisa achar todas as ocorrencias ou so a primeira;
- custo de criar e controlar Threads.

Quando queremos apenas a primeira ocorrencia:

```text
1. Paralelo com parada
2. Sequencial com parada
3. Paralelo com ExecutorService
4. Paralelo com Thread manual
5. Sequencial normal
6. Sequencial bidirecional
7. Sequencial ordenado ABC
```

Quando queremos todas as ocorrencias:

```text
1. Paralelo com ExecutorService
2. Paralelo com Thread manual
3. Sequencial normal
4. Sequencial bidirecional
5. Sequencial ordenado ABC
```

Observacao:

- em pastas pequenas, o sequencial pode ganhar do paralelo;
- o paralelo tende a ficar melhor com mais arquivos ou arquivos maiores.

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
pasta menor + Carolyn + modos 1 a 8
pasta maior + Jessica Herring + modos 1 a 8
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
- A funcionalidade principal ja esta pronta. Evitar mudar o fluxo base sem necessidade.
