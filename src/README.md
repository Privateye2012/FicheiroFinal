
Projeto: efolioB
Localização: COMPILACAO/monty-phyton

## Descrição

O eFolioA definiu a implementação de um parser para a linguagem MontyPython,
uma simplificação da linguagem Python.

O eFolioB acrescenta mais uma fase, nomeadamente a geração de código de três
endereços e a implementação de algumas optimizações sobre este código.

Foi também implementado um pré-processador para resolver problema do significado
semantico da indentação na linguagem Python, nomeadamente significar o inicio
de um bloco (quando indentado) e o fim (quando desindentado).

Tecnologias utilizadas: Antlr 4; Bash; Java

## Implementação

A arquitectura neste momento implementada, usa várias componentes que implementam
as fases que seguem:

* Pré-processamento
  * Input : Nome do ficheiro, Output: StringBuffer
* Parsing
  * Análise lexicográfica
    * Input : Sequência de caracteres, Output: Sequencia de tokens
  * Análise semântica
    * Input : Sequência de tokens, Output: Árvore sintática
* Geração de TAC (Three Address Code):
  * Input : Árvore Sintática, Output: ArrayList de instruções TAC
* Optimização:
  * Input : ArrayList de instruções TAC, Output: ArrayList de instruções TAC optimizada

Adivinha-se que a próxima entrega, se baseie no ArrayList de instruções TAC e efectue
a geraçção de Assembly ou de uma IR, por exemplo do LLVM, com o objectivo final de executar
programas na linguagem Monty Python.


### Pre-processador

O pré processador, efectua uma leitura do ficheiro e, tem apenas a preocupação
de inserir um marcador de início de bloco com o caracter '{'  e de fim de bloco,
inserindo o caracter '}'.

Além destes dois marcadores, todo o código fica exactamente igual. O resultado
do pré-processamento é "deixado" num StringBuffer que será na fase seguinte,
usado para efcetuar as análises lexicográficas e semânticas.

Esta abordagem simplifica a gramática do parser e permite efectuar um debug
mais fácil.

A implementação do pré-processador está efectuada na classe MontyPythonPreProcessor

## Análise lexicográfica

Mantivemos basicamente a versão anterior apenas modificando com a declaração dos
novos tokens que identificam o inicio e fim de bloco.

O Lexer está implementado em separado da gramática no ficheiro MontyPythonLexer.g4

## Análise Semântica

Mantivemos também quase tudo o que existuia no efolioA com excepção do inicio e fim de blocos

O Parser está implementado em separado da gramática no ficheiro MontyPythonParser.g4

## Geração de Código de Três Endereços

O código de três endereços ( em inglês Three-Address Code, TAC) é uma possibilidade de
representação intermédia de um programa (IR). Trata-se de uma técnica muito usada nos
compiladores por forma a simplificar a análise e a otimização do código.

É constituído por uma sequência de instruções, cada qual envolve no máximo três endereços
(variáveis ou literais), e realiza uma operação simples, como uma operação aritmética
ou lógica, atribuição, ou salto condicional.

Num compilador, após a análise sintática e validação da semântica, é efectuado para cada
regra, um mapeamento para código de três endereços. Este mapeamento é estruturado e top
down, na medida em que uma regra de alto nível delega a geração no mapeador das regras de
baixo nível.

No caso concreto do nosso trabalho, a geração é realizada a partir da árvore sintática
gerada pelo antlr. Para navegar na árvore, utiliza-se um padrão "visitor", baseado
no visitor que o antlr gera a partir da gramática. A partir, significa que se extende
o vistor gerado pelo antlr, com a implementação concreta do que se pretende realizar
sempre que se visita uma regra, que neste caso consiste ou em gerar instruções TAC ou
visitar regras de mais baixo nível.

## Optimização de TACs

Os tipos de optimização sobre código de três endereços estão bem tipificados e documentados.
Para obter os tipos de optimização mais usuais usámos o ChatGPT que se revelou muito
útil.

No nosso caso tentámos implementar as seguintes optimizações:
* Dobragem de constantes - Efectuar o cálculo directo na compilação em vez de o fazer em run-time
* Propagação de constantes - Substituir sempre que possível a utilização de variáveis por constantes
evitando assim afectações desnecessárias
* Eliminação de código morto

## Conversão de Código de Três Endereços para representação intermédia de IR

Após a optimização do código de três endereços, este é convertido em código intermédio
do LLVM. O LLVM - Low Level Virtual Machine é um projecto livre de infraestrutura moderna
de compiladores.

Esta fase foi efectuada no contexto do eFolio Global da disciplina de compilação.

O LLVM está estruturado por forma a permitir a existência de front-end(s) e back-end(s) que
comunicam através de uma linguagem/representação intermédia, o LLVM IR.

A optarmos por converter a nossa linguagem de três endereços em LLVM IR, ganhamos
a versatilidade de conseguir usar backends diferentes que permitirão a execução 
do(s) programa(s) em sistemas com diferentes arquitecturas, como o Intel (x86),
mas também ARM, Risc V ou PowerPC.

Esta representação intermédia pode ser descrita num formato textual, tal como
o assembly.

É estruturada em:

* Instruções : Operações atómicas como adição, multiplicação, comparação, entre outras.
* Tipos : Definição de tipos, nomeadamente primitivos, por exemplo inteiros ou
numeros em virgula flutuante, arrays, etc.
* Funções : Unidades de código que contêm um conjunto de instruções. Têm parâmetros
e retornam valores.
* Blocos básicos: Unidades de controlo de fluxo básicas, constituída por uma label
(exemplo: "inicio:"), instruções (exemplo: "%resultado = add i32 %a, %b") e terminação
que define qual o próximo bloco a ser executado (ex: "ret i32 %resultado",
"br i1 %cmp, label %then, label %else").

A geração de um objecto (código máquina fonte ainda não linkado) a partir de um programa
representado em LLVM IR é efectuada através do comando clang, por exemplo:

```
clang -c -o programa.o programa.ll
```

Depois, a 

Este processo é muito similar ao processo, caso se gerasse assembly, onde a sequência seria:

```
nasm -f elf64 -o programa.o programa.asm
```

Finalmente, para obter o executável basta efectuar a linkagem final com as libs do sistema.

Por exemplo:

```
ld -o programa  programa.o -lc -e main
```

Concluindo, somos de opinião que ao gerar o LLVM IR acrescentamos versatilidade ao
compilador (embora também mais um dependência) e demonstramos que ao usar
ferramentas como o antlr e o clang/llvm compreendemos de que forma se cria
um compilador, e ainda por cima num contexto de reutilização de ferramentas
existentes que contribuem para uma efectiva versatilidade.

Acresce que, no momento em que realizámos esta implementção/evolução, ainda
era desconhecido o feedback do eFolio B, pelo que não procedemos a eventuais
modificações, mas no computo global implementámos todas as fases de um compilador
pensando por isso que cumprimos o objectivo da UC.

## Build

Para facilitar o desenvolvimento, a compilação foi automatizada num script em bash
que efectua a geração do código java a partir da gramática e do analisador lexicográfico
e de seguida a compilação dos ficheiros .java para .class(es).

Infelizmente, como ocorreram problemas com o plugin do antlr4 para maven,
não nos foi ainda possível gerar uma versão mais compacta num .jar, mas
estamos convictos de que conseguiremos lá chegar na próxima versão.

Assim, para compilar, deve-se executar o seguinte comando:

```
./build-monty-python.sh
```

## Executar

A execução, neste eFolio, não ficou resolvida de forma que nos satisfaça.
O resultado da compilação fica em target/classes/pt/uab/compilacao
Para executar, a variável CLASSPATH deve estar devidamente afectada com a
localização dos jar(s) do antlr4 e com .

Por exemplo executar:
```
export CLASSPATH=/opt/antlr4/antlr-4.13.1-complete.jar:.
```

Depois, ao posicionarem-se na pasta target/classes, basta executar o seguinte comando:

```
java pt.uab.compilacao.Main ../../src/tests/ex1.mp
```

O objectivo pretendido é que se crie um jar compacto que contenha as classes geradas
pelo nosso trabalho e todas as classes devidamente extraídas dos jar(s) do antlr4.
Este resultado era suposto ser obtido ao usar o plugin antlr4 do maven.


## Testes

Os testes efectuados foram sobretudo ao nível da leitura da árvore sintática e para
tal adicionámos a possibilidade de executar o programa com as várias fases e a mostrar
no final a árvore sintática sob a forma de GUI.

Na próxima versão gostaríamos de poder fazer a execução de cada passo em separado e
ter testes unitários que irão permitir demonstrar a qualidade do código gerado e das
optimizações.


## Método de desenvolvimento

O ciclo de desenvolvimento/testes, neste momento, consiste na modificação da gramática,
a execução com um dos ficheiros de testes, análise da árvore no GUI e do código de três
endereços.


## Conclusão

Embora não tenhamos conseguido obter a qualidade desejada nesta entrega, sobretudo
devido a grande consumo de tempo a implementar a hierarquia, esta está funcional
e permitirá que na próxima fase se termine com qualidade, o compilador pretendido.

Os principais problemas que nos ocuparam muito tempo foram:
  * Build system - Sendo fundamental para conseguir desenvolver com a rapidez suficiente
tentámos uma primeira abordagem utilizando o maven. Infelizmente, após vários dias,
não conseguimos que funcionasse correctamente e decidimos efectuar a compilação com
um shell script.
  * Geração do código TAC - O visitor deu bastante trabalho coolocar a funcionar e
quando finalmente funcionou, limitou a alteração do parser, dado que obriga a
reimplementar cada visitor.

Os objectivos não foram totalmente atingidos, mas dada a qualidade da infraestrutura
e arquitectura montada, esperamos ainda vir a conseguir atingi-lo no final. Baseamos
esta assunção no facto de neste momento conseguirmos executar o parser sobre diferentes
programas, ter as fases correctamente implementadas, ter os tipos de optimização
devidamente identificados, prevendo assim apenas a necessidade de conseguir executar
e validar o programa sobre mais exemplos e mais complexos.

Ao nível dos conhecimentos adquiridos, demos um enorme passo. No eFolio anterior
já tinha sido possível compreender de que forma o parsing, nomeadamente as análises
sintáticas e semanticas eram efectuadas e o que é e para que serve a Árvore Sintática.

Agora compreendemos o que é o código de três endereço, como se consegue mapear instruções
de alto nível para este código intermédio, bem mais próximo da linguagem máquina e quais
os padrões de optimização já estudados e consolidados na comunidade e como os implementar.


## Pŕoximos passos

Realizar mais testes concretos de geração de Código de Três Endereços e optimização
Geração de Assembly ou código intermédio do LLVM