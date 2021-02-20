lexer grammar ZekeLexer;

EQ : '==';
GT : '>';
GTE : '>=';
LT : '<';
LTE : '<=';

WS: [ \t\r\n\u000C]+ -> skip;
