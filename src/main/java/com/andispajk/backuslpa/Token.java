/** Token.java

Represent the tokens found in BNF/EBNF grammar specifications.

*/

package com.andispajk.backuslpa;

public class Token {
    private String lexeme;
    private TkType type;
    private int startPos;

    /* Token()
        @lexeme      string representation of the token
        @type        token type represented as the static class vars
        @startPos    index of the token's first char in the file line

        Construct a Token containing all its relevant info.
    */
    public Token(String lexeme, TkType type, int startPos) {
        this.lexeme = lexeme;
        this.type = type;
        this.startPos = startPos;
    }

    public String lexeme() {
        return lexeme;
    }

    public TkType type() {
        return type;
    }

    public int startPos() {
        return startPos;
    }

    public void print() {
        if (type == TkType.NEWLINE)
            System.out.printf("%11s:\n", "NEWLINE");
        else
            System.out.printf("%11s: |%s|\n", type.toString(), lexeme);
    }
}