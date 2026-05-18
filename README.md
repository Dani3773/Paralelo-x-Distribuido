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

O que faz:

- roda a busca sequencial normal;
- depois roda a busca paralela por arquivo usando `ExecutorService`;
- compara os dois tempos;
- calcula o Speedup.

Como percorre:

- sequencial: um arquivo por vez, linha por linha, do inicio ao fim;
- paralelo: varios arquivos podem ser processados ao mesmo tempo, cada arquivo em uma tarefa.

Para ou nao para:

- nao para na primeira ocorrencia;
- procura todas as ocorrencias em todos os `.txt` da pasta escolhida.

Tempo esperado:

- em pasta pequena, o sequencial pode ser parecido ou ate mais rapido;
- em pasta maior, o paralelo tende a melhorar;

Exemplo testado:

```text
Pasta: menor
Busca: Carolyn

Resultado:
- encontrou 6 ocorrencias;
- mostrou arquivo, linha e conteudo;
- comparou sequencial x paralelo;
- calculou Speedup;
- confirmou integridade entre os dois modos.
```

### 2. Sequencial normal

O que faz:

- pesquisa todos os arquivos `.txt` da pasta escolhida;
- procura todas as ocorrencias do termo;
- nao usa Threads.

Como percorre:

- pega o primeiro arquivo;
- le da primeira ate a ultima linha;
- depois passa para o proximo arquivo;
- repete isso ate acabar a pasta.

Para ou nao para:

- nao para quando encontra;
- continua procurando para listar tudo.

Tempo esperado:

- e o algoritmo mais simples;
- costuma ser bom para arquivos pequenos;
- pode ficar lento quando existem muitos arquivos ou arquivos grandes;
- serve como base de comparacao para os outros.

Exemplo testado:

```text
Pasta: menor
Busca: Carolyn

Resultado:
- encontrou 6 ocorrencias;
- percorreu todos os arquivos;
- nao parou na primeira ocorrencia.
```

### 3. Sequencial com parada

O que faz:

- pesquisa em ordem, igual ao sequencial normal;
- mas retorna apenas a primeira ocorrencia encontrada.

Como percorre:

- comeca no primeiro arquivo;
- le da primeira linha ate encontrar o nome;
- se nao encontrar, vai para o proximo arquivo;
- quando encontra, encerra tudo.

Para ou nao para:

- para na primeira ocorrencia.

Tempo esperado:

- tende a ser muito rapido se o nome aparece no primeiro arquivo ou nas primeiras linhas;
- pode ter tempo parecido com o sequencial normal se o nome aparece so no final ou nao existe;
- nao deve ser usado quando precisamos mostrar todas as ocorrencias.

Exemplo testado:

```text
Pasta: menor
Busca: Carolyn

Resultado:
- encontrou Carolyn Hale em arq_1.txt, linha 1;
- parou imediatamente;
- retornou 1 ocorrencia, mesmo existindo outras.
```

### 4. Sequencial bidirecional

O que faz:

- pesquisa todos os arquivos;
- dentro de cada arquivo, procura por dois lados.

Como percorre:

- uma parte comeca no inicio;
- outra parte comeca no fim;
- as duas caminham ate o meio.

Exemplo:

```text
linha 1, ultima linha
linha 2, penultima linha
linha 3, antepenultima linha
...
```

Para ou nao para:

- nao para na primeira ocorrencia;
- procura todas as ocorrencias.

Tempo esperado:

- pode encontrar visualmente resultados do fim mais cedo;
- mas, como precisa listar tudo, ainda percorre o arquivo inteiro;
- pode ser um pouco mais lento que o sequencial normal porque carrega as linhas do arquivo na memoria;
- e bom para demonstrar uma estrategia diferente, mas nao necessariamente mais rapida.

Exemplo testado:

```text
Pasta: menor
Busca: Carolyn

Resultado:
- encontrou 6 ocorrencias;
- nao parou na primeira;
- dentro de cada arquivo, comparou inicio e fim ate chegar ao meio.
```

### 5. Sequencial ordenado ABC

O que faz:

- carrega as linhas de cada arquivo;
- ordena as linhas em ordem alfabetica;
- depois procura o nome na lista ordenada.

Como percorre:

- um arquivo por vez;
- le todas as linhas;
- guarda o numero original da linha;
- ordena pelo conteudo;
- procura na lista ordenada.

Importante:

- o resultado continua mostrando a linha original do arquivo;
- e um exemplo de pre-processamento antes da busca.

Para ou nao para:

- nao para na primeira ocorrencia;
- procura todas as ocorrencias.

Tempo esperado:

- tende a ser um dos mais lentos;
- isso acontece porque alem de procurar, ele tambem ordena;
- pode ser interessante para explicar que preparar dados antes da busca tem custo;
- em uma busca simples, ordenar antes normalmente nao compensa.

Exemplo testado:

```text
Pasta: menor
Busca: Carolyn

Resultado:
- encontrou 6 ocorrencias;
- carregou as linhas;
- ordenou em ordem alfabetica;
- manteve a linha original no resultado.
```

### 6. Paralelo com ExecutorService

O que faz:

- usa um pool de Threads controlado pelo Java;
- divide a pesquisa por arquivos;
- cada arquivo vira uma tarefa;
- a quantidade de Threads criadas nao depende diretamente da quantidade de arquivos.

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
- as 20 tarefas entram em uma fila;
- as 8 Threads vao pegando tarefas ate acabar;
- quando uma Thread termina um arquivo, ela pega outro da fila.
```

Ideia:

- cada arquivo vira uma tarefa;
- o Java distribui essas tarefas entre um numero controlado de Threads;
- as Threads sao reutilizadas;
- no final junta os resultados.

Como percorre:

- varios arquivos podem ser lidos ao mesmo tempo;
- dentro de cada arquivo, a leitura continua sendo do inicio ao fim;
- cada tarefa retorna os resultados do seu arquivo.

Para ou nao para:

- nao para na primeira ocorrencia;
- procura todas as ocorrencias.

Tempo esperado:

- tende a ser melhor que o sequencial quando existem varios arquivos ou arquivos maiores;
- pode perder para o sequencial em pastas pequenas por causa do custo de criar e gerenciar tarefas;
- e a forma paralela mais organizada do projeto.
- tende a ser melhor que Thread manual quando existem muitos arquivos, porque nao cria uma Thread nova para cada arquivo.

Exemplo testado:

```text
Pasta: menor
Busca: Carolyn

Resultado:
- encontrou 6 ocorrencias;
- criou tarefas para os arquivos;
- distribuiu as tarefas no pool de Threads;
- nao parou na primeira ocorrencia.
```

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
- nao existe fila controlada igual ao ExecutorService;
- as Threads nao sao reutilizadas.
```

Ideia:

- 5 arquivos = 5 Threads;
- 20 arquivos = 20 Threads;
- cada Thread procura em um arquivo;
- o programa espera todas terminarem com `join()`.

Como percorre:

- todos os arquivos podem comecar quase ao mesmo tempo;
- dentro de cada arquivo, a leitura e do inicio ao fim;
- os resultados sao guardados em uma lista compartilhada.

Para ou nao para:

- nao para na primeira ocorrencia;
- procura todas as ocorrencias.

Tempo esperado:

- pode ser rapido quando existem varios arquivos;
- pode ser pior que `ExecutorService`, porque criar Thread manualmente para cada arquivo tem mais custo;
- e util para demonstrar uso direto de `Thread`, mesmo nao sendo a forma mais organizada.
- se houver muitos arquivos, pode criar Threads demais e pesar mais no sistema.

Exemplo testado:

```text
Pasta: menor
Busca: Carolyn

Resultado:
- encontrou 6 ocorrencias;
- a pasta menor possui 5 arquivos .txt;
- criou 5 Threads, uma para cada arquivo;
- nao parou na primeira ocorrencia.
```

### Diferenca direta entre 6 e 7

Os dois fazem busca paralela e podem ler varios arquivos ao mesmo tempo. A diferenca principal e a quantidade e o controle das Threads.

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
- retorna apenas uma ocorrencia;
- usa uma flag compartilhada para avisar que alguem ja encontrou.

Quando uma Thread encontra o nome:

- ela marca que encontrou;
- as outras param o quanto antes;
- o programa retorna uma ocorrencia.

Como percorre:

- cria Threads para os arquivos;
- cada Thread comeca lendo seu arquivo do inicio;
- se uma Thread encontra o termo, marca a flag de parada;
- as outras Threads verificam essa flag e encerram.

Para ou nao para:

- para quando alguma Thread encontra a primeira ocorrencia;
- nao lista todas as ocorrencias.

Tempo esperado:

- pode ser o mais rapido quando so precisamos saber se o nome existe;
- funciona muito bem se alguma Thread encontra o nome cedo;
- pode nao ser tao bom se o nome nao existe, porque mesmo assim as Threads precisam procurar bastante;
- e bom para explicar cooperacao entre Threads e parada antecipada.

Exemplo testado:

```text
Pasta: menor
Busca: Carolyn

Resultado:
- encontrou Carolyn Hale em arq_1.txt, linha 1;
- retornou 1 ocorrencia;
- parou a busca apos alguma Thread encontrar o termo;
- nao lista todas as ocorrencias.
```

## Exemplos Testados com Tempo

Os tempos abaixo sao exemplos reais medidos no computador de teste. Eles podem mudar em outro computador ou em outra execucao.

### Exemplo 1: busca por primeiro nome

Entrada usada:

```text
Pasta: menor
Busca: Carolyn
```

Esse caso encontra 6 ocorrencias, porque existem varias linhas com o primeiro nome `Carolyn`.

```text
Modo 1 - Comparacao padrao
Sequencial: 115.0863 ms
Paralelo: 22.5614 ms
Speedup: 5.10x
Total encontrado: 6

Modo 2 - Sequencial normal
Tempo: 59.1069 ms
Total encontrado: 6

Modo 3 - Sequencial com parada
Tempo: 28.9922 ms
Total encontrado: 1

Modo 4 - Sequencial bidirecional
Tempo: 64.3686 ms
Total encontrado: 6

Modo 5 - Sequencial ordenado ABC
Tempo: 83.7579 ms
Total encontrado: 6

Modo 6 - Paralelo com ExecutorService
Tempo: 58.4827 ms
Total encontrado: 6

Modo 7 - Paralelo com Thread manual
Tempo: 49.0012 ms
Total encontrado: 6

Modo 8 - Paralelo com parada
Tempo: 39.7697 ms
Total encontrado: 1
```

Analise desse exemplo:

- modo 2 encontrou todas as 6 ocorrencias;
- modo 3 foi mais rapido que o modo 2 porque parou na primeira ocorrencia;
- modo 6 e modo 7 encontraram todas as ocorrencias usando Threads;
- modo 8 retornou apenas 1 ocorrencia porque tambem usa parada antecipada;
- modo 5 foi mais lento porque ordena as linhas antes de procurar.

Comparacoes diretas:

```text
Modo 2 x Modo 3
- Modo 2: procura tudo, encontrou 6.
- Modo 3: para na primeira, encontrou 1.
- Nesse teste, modo 3 foi mais rapido.

Modo 6 x Modo 7
- Modo 6: usa pool controlado de Threads.
- Modo 7: cria uma Thread por arquivo.
- Nesse teste pequeno, modo 7 ficou um pouco mais rapido, mas isso pode mudar.

Modo 2 x Modo 5
- Modo 2: apenas le e procura.
- Modo 5: le, ordena e depois procura.
- Modo 5 foi mais lento por causa da ordenacao.
```

### Exemplo 2: busca por nome completo

Entrada usada:

```text
Pasta: maior
Busca: Jessica Herring
```

Esse caso encontra 1 ocorrencia exata:

```text
Arquivo: a1.txt | Linha: 85 | Conteudo: Jessica Herring
```

Tempos medidos:

```text
Modo 1 - Comparacao padrao
Sequencial: 154.0655 ms
Paralelo: 28.6824 ms
Speedup: 5.37x
Total encontrado: 1

Modo 2 - Sequencial normal
Tempo: 105.4521 ms
Total encontrado: 1

Modo 3 - Sequencial com parada
Tempo: 43.7671 ms
Total encontrado: 1

Modo 4 - Sequencial bidirecional
Tempo: 138.2704 ms
Total encontrado: 1

Modo 5 - Sequencial ordenado ABC
Tempo: 261.4032 ms
Total encontrado: 1

Modo 6 - Paralelo com ExecutorService
Tempo: 137.1476 ms
Total encontrado: 1

Modo 7 - Paralelo com Thread manual
Tempo: 140.5146 ms
Total encontrado: 1

Modo 8 - Paralelo com parada
Tempo: 60.6472 ms
Total encontrado: 1
```

Analise desse exemplo:

- como o nome completo aparece uma vez, os modos com parada fazem mais sentido;
- modo 3 foi mais rapido que o modo 2 porque encontrou e parou;
- modo 8 tambem parou, mas ainda tem custo de criar Threads;
- modo 5 foi o mais lento porque ordenou antes de procurar;
- modo 6 e modo 7 tiveram tempos proximos, porque ambos dividiram a busca entre arquivos.

Comparacoes diretas:

```text
Modo 2 x Modo 3
- Modo 2: continuou procurando mesmo depois de achar Jessica Herring.
- Modo 3: parou quando encontrou Jessica Herring.
- Modo 3 foi melhor para busca de uma ocorrencia especifica.

Modo 6 x Modo 7
- Modo 6: criou um numero controlado de Threads.
- Modo 7: criou uma Thread para cada arquivo.
- Os tempos ficaram proximos nesse teste.

Modo 3 x Modo 8
- Modo 3: parada sequencial.
- Modo 8: parada paralela.
- Nesse teste, modo 3 foi mais rapido, provavelmente porque encontrou cedo e nao teve custo de Threads.
```

## Ordem Esperada de Velocidade

Nao existe uma ordem fixa que sempre vai acontecer. O tempo muda conforme:

- tamanho dos arquivos;
- quantidade de arquivos;
- se o nome aparece no inicio ou no fim;
- se o algoritmo precisa achar todas as ocorrencias ou so a primeira;
- custo de criar e controlar Threads.

Mesmo assim, a expectativa geral e esta:

### Quando queremos apenas a primeira ocorrencia

Mais rapido para mais lento, em teoria:

```text
1. Paralelo com parada
2. Sequencial com parada
3. Paralelo com ExecutorService
4. Paralelo com Thread manual
5. Sequencial normal
6. Sequencial bidirecional
7. Sequencial ordenado ABC
```

Explicacao:

- os modos com parada tendem a ser mais rapidos porque nao precisam ler tudo;
- o paralelo com parada pode ganhar quando o nome esta em um arquivo que alguma Thread encontra cedo;
- o sequencial com parada pode ganhar se o nome estiver logo no primeiro arquivo;
- o ordenado ABC costuma ser mais lento porque precisa carregar e ordenar linhas antes de procurar.

### Quando queremos todas as ocorrencias

Mais rapido para mais lento, em teoria:

```text
1. Paralelo com ExecutorService
2. Paralelo com Thread manual
3. Sequencial normal
4. Sequencial bidirecional
5. Sequencial ordenado ABC
```

Explicacao:

- para listar tudo, nao adianta parar cedo;
- os modos paralelos podem dividir arquivos entre Threads;
- o `ExecutorService` tende a ser mais organizado e eficiente que criar Threads manualmente;
- o sequencial normal e simples, mas usa uma linha de execucao;
- o bidirecional continua lendo o arquivo inteiro, entao nem sempre melhora;
- o ordenado ABC faz trabalho extra de ordenacao.

### Observacao

Se a pasta for pequena, o sequencial pode ganhar do paralelo. Isso acontece porque criar e gerenciar Threads tambem tem custo. O paralelo tende a ficar melhor quando usamos mais arquivos ou arquivos maiores.

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
