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

    /* parseDirective()
        @return     true if mode directive found, else false

        Parse a .BNF or .EBNF mode directive.
    */
    public boolean parseDirective() {
        tk = lexer.lex();
        TkType type = tk.type();
        if (type == TkType.BNF_MODE || type == TkType.EBNF_MODE) {
            mode = type;
            return true;
        } else {
            lexer.error(tk.startPos(), "no directive found");
            return false;
        }
    }
}
