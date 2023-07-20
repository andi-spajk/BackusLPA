/** Token.java

Represent the tokens found in BNF/EBNF grammar specifications.

*/

package com.andispajk.backuslpa;

public class Token {
    public static int IDENT      = 0;   // non-terminals
    public static int LITERAL    = 1;   // BNF unquoted terminals
    public static int CHAR       = 2;   // char literal, single quotes
    public static int STRING     = 3;   // string literal, double quotes
    public static int EQUAL      = 4;   // =
    public static int DERIVES    = 5;   // ::=
    public static int LPAREN     = 6;   // (
    public static int RPAREN     = 7;   // )
    public static int LCURLY     = 9;   // {
    public static int RCURLY     = 10;  // }
    public static int LBRACKET   = 11;  // [
    public static int RBRACKET   = 12;  // ]
    public static int LCHEVRON   = 13;  // <
    public static int RCHEVRON   = 14;  // >
    public static int PIPE       = 15;  // |
    public static int STAR       = 16;  // *
    public static int PLUS       = 17;  // +
    public static int QUESTION   = 19;  // ?
    public static int DIRECTIVE  = 20;
    public static int ILLEGAL    = 21;  // illegal tokens will have garbage
                                        // lexemes that shouldn't be used
    public static int EOF        = 22;

    private String lexeme;
    private int type;
    private int startPos;

    /* Token()
        @newLexeme      string representation of the token
        @newType        token type represented as the static class vars
        @newStartPos    index of the token's first char in the file line

        Construct a Token containing all its relevant info.
    */
    public Token(String lexeme, int type, int startPos) {
        this.lexeme = lexeme;
        this.type = type;
        this.startPos = startPos;
    }

    public String lexeme() {
        return lexeme;
    }

    public int type() {
        return type;
    }

    public int startPos() {
        return startPos;
    }
}