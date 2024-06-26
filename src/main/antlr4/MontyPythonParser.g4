
parser grammar MontyPythonParser;

options { tokenVocab=MontyPythonLexer; }

/**
 * Start rule
 */
program
    : (NEWLINE | statement )* EOF
    ;

statement
    : oper
    | decl_var
    | decl_list
    | atrib_var
    | atrib_list
    | list_function
    | function
    | ciclos
    | input
    | print
    ;

decl_var
    : float
    | int
    ;

decl_list
    : float_list
    | int_list
    ;

float
    : (FLOAT_TOK)? ID (EQUAL (FLOAT|INT))? (COMMA ID (EQUAL (FLOAT|INT))?)*
    ;

int
    : INT_TOK ID (EQUAL INT)? (COMMA ID (EQUAL INT)?)*
    ;

float_list
    : (FLOAT_TOK)? LBRACKET RBRACKET ID (EQUAL LBRACKET (FLOAT|INT) (COMMA (FLOAT|INT))* RBRACKET)?
    ;

int_list
    : INT_TOK LBRACKET RBRACKET ID (EQUAL LBRACKET INT (COMMA INT)* RBRACKET)?
    ;

atrib_var
    : ID EQUAL oper
    ;

atrib_list
    : ID EQUAL LBRACKET oper (COMMA oper)* RBRACKET // se já foi declarado não sei se tem o tipo da declaração
    | list_index EQUAL oper
    ;
list_index
    : ID LBRACKET INT RBRACKET //tenho duvidas que consigo identificar as posições da lista
    ;
oper
    : oper AND oper
    | oper OR oper
    | NOT LPAREN oper (AND oper)* RPAREN
    | oper EQUAL_EQUAL oper
    | oper DIFF oper
    | oper MINOR oper
    | oper MINOR_EQUAL oper
    | oper MAJOR oper
    | oper MAJOR_EQUAL oper
    | oper PLUS oper
    | oper MINUS oper
    | oper MULT oper
    | oper DIV oper
    | oper POWER oper
    | oper DIV_INT oper
    | oper MOD oper
    | INT
    | FLOAT
    | ID
    ;

list_function
    : size_function
    | add_function
    | remove_function
    ;

size_function
    : 'size' LPAREN ID RPAREN
    ;

add_function
    : 'add' LPAREN ID COMMA INT RPAREN
    ;

remove_function
    : 'remove' LPAREN ID COMMA INT RPAREN
    ;
function
    : 'def' type? ID LPAREN (type? ID (COMMA type? ID)*)? RPAREN COLON
    ;
type
    : INT_TOK
    | FLOAT_TOK
    | INT_TOK LBRACKET RBRACKET
    | FLOAT_TOK LBRACKET RBRACKET
    ;
input
    : ID EQUAL INPUT LPAREN STRING RPAREN //erro
    ;
print
    : PRINT LPAREN (STRING (COMMA oper)*)? RPAREN //erro
    ;

ciclos
    : for
    | while
    | if
    | elif
    | else
    ;

block
    : START_BLOCK  statement* END_BLOCK
    ;

for
    : FOR ID IN (LIST | RANGE LPAREN oper (COMMA oper)* RPAREN) COLON block
    ;

while
    : WHILE oper COLON block
    ;
if
    : IF oper COLON block
    ;
elif
    : ELIF oper COLON block
    ;
else
    : ELSE COLON block // Instrução else
    ;


// verificar as conversões de int para float e vice-versa nas operações
// verificar as aspas (input e print)