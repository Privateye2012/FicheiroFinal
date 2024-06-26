
Projeto: efolioA
Localização: COMPILACAO/efolioA

Descrição:

O eFolioA define a implementação de um parser para a linguagem MontyPython,
uma simplificação da libguagem Python.

Tecnologias utilizadas: Antlr 4; Make; Java

Build

Para facilitar o desenvolvimento, a compilação foi automatizada num Makefile
muito simples que contem as regras:

  build - Compila os analisadores léxico e sintático para Java
          e de seguida compila os .java para .class, por forma a
          que o parser possa possa ser executado, por exemplo com o grun;
  clean - Limpa todos os ficheiros gerados pela compilação;
  tests - Executa o TestRig do ANTLR através do comando grun


Testes

Foram criados um conjunto de ficheiros de teste que, ao executar com o grun, facilitassem
o desenvolvimento do analisador léxico e (ou) sintático.

Dois desses ficheiros servem para gerar os resultados solicitados, contendo sintaxes
correctas e(ou) com erros. Para os gerar, deve ser efectuada a seguinte instrução:

  make tests

Após a execução, que executa o grun sobre os ficheiros com extensão .mp existentes
na directoria tests, gerando o ficheiro result.txt que contém o resultado da execução
sobre todos os ficheiros.

Opções

Numa primeira versão, implementámos o analisar léxico e sintático no mesmo ficheiro.
Numa segunda fase, ao tentar resolver o problema de a indentação, no python, definir
os blocos, optámos por separar em dois ficheiros:
  MontyPythonParser.g4 - Gramática para análise sintática em EBNF
  MontyPythonLexer.g4 - Definição da identificação dos tokens a passar ao parser

Esta separação tornou possível a utilização de uma super classe para o lexer
através da sua configuração nas options:

  options {
    superClass = LexerBaseMontyPython;
  }

Sendo a classe base implementada em LexerBaseMontyPython.java, na qual, nomeadamente se torna
possível reescrever o método nextToken() que será usado em vez do existent na
classe Lexer, interna ao ANTLR.

Implementação do reconhecimento dos blocos indentados

Infelizmente não conseguimos completar esta implementação (que se entrega em indent_atempt).
Para estudar mais profundamente a resolução deste problema, usámos:
  - O livro do autor do ANTLR, Terrence Parr - The definitive ANTLR reference, concretamente
  no capítulo acerca da análise lexicográfica dependente do contexto (Wielding Lexical Black
  Magic)
  - As implementações de parsers para Python, nomeadamente as que existem na
    biblioteca do ANTLR (ver https://github.com/antlr/grammars-v4/tree/master/python) e
    num projecto encontrado no github (https://github.com/RobEin/tiny-python);

Destas leituras (e de mais umas quantas discussões em grupos), percebemos que a solução
passa por usar dois tokens adicionais (INDENT, DEDENT) que, através de um stack identificam
sempre que o número de espaços ou TABs aumentam ou diminuam e assim são reconhecidos no
EBNF da análise sintática.
A forma de o fazer passa por usar uma classe base para o analisador léxico, no
qual seja detectado o contexto em um NEWLINE aparece (dentro de um comentário, após
uma instrução simples e(ou) após um início de bloco).

Próximos passos:
- Terminar a implementação do reconhecimento de blocos pela sua indentação

Conclusão:

Foi implementada uma versão que analisa as sintaxes correctas de código em
Monty Python, detectando erros de sintaxe, cumprindo por isso os principais
objectivos de implementação. Infelizmente nõ se conseguiu terminar a implementação
correcta do reconhecimento de blocos indentados, mas cumpriu-se o objectivo
pedagógico de perceber o problema e a respectiva dificuldade e procurar
uma solução.

Além de ter cumprido os objectivos de compreensão dos diferentes tipos de parsers
a implementação prática permitiu-nos adquirir competências para a definição de
analisadores léxicos e sintáticos através da utilização de expressões regulares
e gramáticas em EBNF.
