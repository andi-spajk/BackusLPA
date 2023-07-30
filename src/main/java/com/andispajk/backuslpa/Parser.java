/** Parser.java

Parse a BNF/EBNF grammar for syntactic correctness.

*/

package com.andispajk.backuslpa;

public class Parser {
    private final Lexer lexer;
    private Token tk;
    private TkType mode;
    private boolean haltParse;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        haltParse = false;
    }

    /* trimNewlines()

        Consume all newline tokens.
    */
    public void trimNewlines() {
        tk = lexer.peek();
        while (tk.type() == TkType.NEWLINE) {
            lexer.lex();
            tk = lexer.peek();
        }
    }
}
