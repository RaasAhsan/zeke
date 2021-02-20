parser grammar ZekeParser;

options {
    tokenVocab=ZekeLexer;
}

compop
    : EQ
    | GT
    | GTE
    | LT
    | LTE
    ;
