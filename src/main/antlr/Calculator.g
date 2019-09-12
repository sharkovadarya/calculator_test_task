grammar Calculator;

query: statement EOF;

statement: expression | variableDeclaration;

variableDeclaration: LET name = IDENTIFIER ASSIGN initialValue = expression;

expression: left = expression op = MUL right = expression #MultiplicationExpr
            | left = expression op = ADD right = expression #AdditionExpr
            | op = ADD expression #UnaryExpr
            | IDENTIFIER #IdentifierExpr
            | LITERAL #LiteralExpr
            | LEFT_PARENTHESIS expression RIGHT_PARENTHESIS #ParenthesisedExpr;

LET: 'let';

fragment NON_DIGIT: ('a'..'z' | 'A'..'Z' | UNDERSCORE);

fragment DIGIT: ('0'..'9');

LITERAL: ('1'..'9') DIGIT* | '0';

ADD: '-' | '+';
MUL: '*' | '/' | '%';

IDENTIFIER: NON_DIGIT (LITERAL | NON_DIGIT)*;

WHITESPACE: (' ' | '\t' | '\r') -> skip;

UNDERSCORE: '_';
COMMA: ',';
ASSIGN: '=';

LEFT_PARENTHESIS: '(';
RIGHT_PARENTHESIS: ')';

EOL: '\n';