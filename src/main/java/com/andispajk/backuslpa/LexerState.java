/** LexerState.java

All states in the Lexer's finite state machine.

*/

package com.andispajk.backuslpa;

public enum LexerState {
    START,          // 0
    LCHEVRON,       // 1
    BNF_CHAR,       // 2
    EBNF_CHAR,      // 3
    BEGIN_CHAR,     // 4
    CHAR_ESCAPE,    // 5
    END_CHAR,       // 6
    IN_STRING,      // 7
    STRING_ESCAPE,  // 8
    DERIVES1,       // 9
    DERIVES2,       // 10
    MAYBE_LPAREN,   // 11
    BEGIN_COMMENT,  // 12
    END_COMMENT,    // 13
    DIRECTIVE,      // 14
    READ_E,         // 15
    READ_B,         // 16
    READ_N,         // 17
    INLINE_COMMENT, // 18
    ACCEPT          // 19
}
