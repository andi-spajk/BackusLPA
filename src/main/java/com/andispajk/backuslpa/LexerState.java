/** LexerState.java

All states in the Lexer's finite state machine.

*/

package com.andispajk.backuslpa;

public enum LexerState {
    START,
    LCHEVRON,
    BNF_CHAR,
    EBNF_CHAR,
    BEGIN_CHAR,
    CHAR_ESCAPE,
    END_CHAR,
    IN_STRING,
    STRING_ESCAPE,
    DERIVES1,
    DERIVES2,
    MAYBE_LPAREN,
    BEGIN_COMMENT,
    END_COMMENT,
    DIRECTIVE,
    READ_E,
    READ_B,
    READ_N,
    INLINE_COMMENT,
    ACCEPT
}
