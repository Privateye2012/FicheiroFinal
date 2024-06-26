
lexer grammar MontyPythonLexer;

/**
 * Analisador lexicográfico para a linguagem Monty MontyPython
 * Lê sequências de caracteres que converte em Tokens, para utilização na
 * gramática
 */


// O pré processador adiciona os caracteres que se seguem para marcar
// o inicio e fim de um bloco
START_BLOCK : '{' ;
END_BLOCK : '}' ;

// Palavras reservadas literais
INT_TOK: 'int';
FLOAT_TOK: 'float';
AND: 'and';
OR: 'or';
NOT: 'not';
PRINT: 'print';
FOR: 'for';
IN: 'in';
LIST: 'list';
SIZE: 'size';
ADD: 'add';
REMOVE: 'remove';
DEF: 'def';
IF: 'if';
ELIF: 'elif';
ELSE: 'else';
WHILE: 'while';
INPUT: 'input';
RANGE: 'range';

// Tokens baseados em expressões regulares
ID: [a-zA-Z_][a-zA-Z0-9_]*;
INT: [+-]?[0-9]+;
FLOAT: [+-]?[0-9]+('.'[0-9]+)?([eE][+-]?[0-9]+)?;
NEWLINE : '\r'? '\n'; // Unix, Windows

// Separadores
QUOTES: '"';
COLON: ':';
COMMA: ',';
LPAREN: '(';
RPAREN: ')';
LBRACKET: '[';
RBRACKET: ']';

// Operadores matemáticos
EQUAL: '=';
PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
POWER: '**';
DIV_INT: '//';
MOD: '%';

// Operadores condicionais
MINOR: '<';
MINOR_EQUAL: '<=';
MAJOR: '>';
MAJOR_EQUAL: '>=';
DIFF: '!=';
EQUAL_EQUAL: '==';

// Comentários e espaços em branco
WS: [ \t\r\n]+ -> skip;
COMMENT: '#' ~[\r\n]* -> skip;
STRING: '"' ~["\r\n]* '"';
