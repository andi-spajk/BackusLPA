/** TkType.java

All possible types of tokens found in a BNF/EBNF grammar specification.

*/

package com.andispajk.backuslpa;

public enum TkType {
    BNF_IDENT,  // BNF non-terminals
    EBNF_IDENT, // EBNF non-terminals
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
    PIPE,       // |
    STAR,       // *
    PLUS,       // +
    QUESTION,   // ?
    BNF_MODE,   // .BNF
    EBNF_MODE,  // .EBNF
    NEWLINE,    // \n
                // windows \r is handled under the hood, user need not worry
    ILLEGAL,    // illegal tokens will have garbage lexemes that shouldn't be
                // used
    EOF
}
