/** TkType.java

All possible types of tokens found in a BNF/EBNF grammar specification.

*/

package com.andispajk.backuslpa;

public enum TkType {
    IDENT,      // non-terminals
    LITERAL,    // BNF unquoted terminals
    CHAR,       // char literal, single quotes
    STRING,     // string literal, double quotes
    EQUAL,      // =
    DERIVES,    // ::=
    LPAREN,     // (
    RPAREN,     // )
    LCURLY,     // {
    RCURLY,     // }
    LBRACKET,   // [
    RBRACKET,   // ]
    LCHEVRON,   // <
    RCHEVRON,   // >
    PIPE,       // |
    STAR,       // *
    PLUS,       // +
    QUESTION,   // ?
    DIRECTIVE,  // illegal tokens will have garbage lexemes that shouldn't be
                // accessed
    ILLEGAL,
    EOF
}
