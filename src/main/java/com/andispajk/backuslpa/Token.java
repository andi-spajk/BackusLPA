/** Token.java

Represent the tokens found in BNF/EBNF grammar specifications.

*/

package com.andispajk.backuslpa;

/* Token()
    @lexeme      string representation of the token
    @type        token type represented as the static class vars
    @startPos    index of the token's first char in the file line

    Construct a Token containing all its relevant info.
*/
public record Token(String lexeme, TkType type, int startPos) {
    public void print() {
        if (type == TkType.NEWLINE)
            System.out.print("    NEWLINE:\n");
        else
            System.out.printf("%11s: |%s|\n", type.toString(), lexeme);
    }
}