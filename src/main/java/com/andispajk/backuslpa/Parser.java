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
        mode = TkType.ILLEGAL;
        haltParse = false;
    }

    /* reset()

        Reset internal state of the parser.
    */
    public void reset() {
        mode = TkType.ILLEGAL;
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
        @return     true if mode directive is found, else false

        Parse a .BNF or .EBNF mode directive and consume it.
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

    /* matchNonterminal()
        @return     true if nonterminal symbol is found, else false

        Read a nonterminal symbol token and consume it.
    */
    public boolean matchNonterminal() {
        tk = lexer.peek();
        TkType type = tk.type();
        if (mode == TkType.BNF_MODE && type != TkType.BNF_IDENT)
            return false;
        else if (mode == TkType.EBNF_MODE && type != TkType.EBNF_IDENT)
            return false;
        // consume the token
        lexer.lex();
        return true;
    }

    /* parseSymbol()
        @return     true if nonterminal or terminal symbol is found, else false

        Parse a grammar symbol token and consume it.
    */
    public boolean parseSymbol() {
        if (matchNonterminal())
            return true;
        // matchNonterminal failed, so tk wasn't consumed
        TkType type = tk.type();
        if (type == TkType.CHAR || type == TkType.STRING) {
            lexer.lex();
            return true;
        }

        String modeStr = "";
        if (mode == TkType.BNF_MODE)
            modeStr = "BNF";
        else if (mode == TkType.EBNF_MODE)
            modeStr = "EBNF";
        String errorMsg = String.format("expected char, string, or %s nonterminal symbol",
                                        modeStr);
        lexer.error(tk.startPos(), errorMsg);
        haltParse = true;
        return false;
    }

    /* matchModifier()
        @return     true if modifier is found, else false

        Read a modifier token and consume it.
    */
    public boolean matchModifier() {
        tk = lexer.peek();
        TkType type = tk.type();
        if (type == TkType.STAR || type == TkType.PLUS ||
            type == TkType.QUESTION) {
            lexer.lex();
            return true;
        }
        return false;
    }

    /* parseFactor()
        @return     true if EBNF factor is found, else false

        Parse a factor and consume its tokens.
    */
    public boolean parseFactor() {
        if (!parseSymbol())
            return false;
        matchModifier();
        return true;
    }
}
