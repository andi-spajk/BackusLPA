/** Parser.java

Parse a BNF/EBNF grammar for syntactic correctness using recursive descent.

*/

package com.andispajk.backuslpa;

public class Parser {
    private final Lexer lexer;
    private Token tk;
    private TkType mode;
    private boolean foundUnmatchedSymbol;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        mode = TkType.ILLEGAL;
        foundUnmatchedSymbol = false;
    }

    /* foundUnmatchedSymbol()
        @return     whether the parser, at any point, saw an unexpected symbol

        Reset internal state of the parser.
    */
    public boolean foundUnmatchedSymbol() {
        return foundUnmatchedSymbol;
    }

    /* resetUnmatchedSymbol()

        Reset the boolean flag that checks for unexpected symbols.

        This was really only useful for testing. The user shouldn't have to call
        this since parseProduction resets the flag just fine.
    */
    public void resetUnmatchedSymbol() {
        foundUnmatchedSymbol = false;
    }

    /* trimNewlines()

        Consume all newline tokens.
    */
    public void trimNewlines() {
        tk = lexer.peek();
        while (tk.type() == TkType.NEWLINE) {
            // consume newline
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
        // consume nonterminal symbol
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
            // consume terminal symbol
            lexer.lex();
            return true;
        }

        // sometimes parseSymbol fails but parseRhs doesn't
        // the caller must use a separate boolean to check if a symbol was
        // actually an error
        // if parseRhs() fails, check if foundUnmatchedSymbol!
        // if parseRhs() doesn't fail, it doesn't matter if foundUnmatchedSymbol
        // the next parse procedure will take care of it
        foundUnmatchedSymbol = true;
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
            // consume modifier
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

    /* parseTerm()
        @return     true if EBNF term is found, else false

        Parse a term and consume its tokens.
    */
    public boolean parseTerm() {
        tk = lexer.peek();
        TkType opening = tk.type();
        TkType closing;
        String expected;
        if (opening == TkType.LPAREN) {
            // consume left parenthesis
            lexer.lex();

            if (!parseRhs())
                return false;

            tk = lexer.lex();
            if (tk.type() != TkType.RPAREN) {
                lexer.error(tk.startPos(), "expected \")\"");
                return false;
            }

            matchModifier();
        } else if (opening == TkType.LBRACKET || opening == TkType.LCURLY) {
            // consume left curly brace or square bracket
            lexer.lex();

            if (!parseRhs())
                return false;

            if (opening == TkType.LBRACKET) {
                closing = TkType.RBRACKET;
                expected = "]";
            } else {
                closing = TkType.RCURLY;
                expected = "}";
            }
            tk = lexer.lex();
            if (tk.type() != closing) {
                lexer.error(tk.startPos(), String.format("expected \"%s\"",
                                                         expected));
                return false;
            }
        } else {
            return parseFactor();
        }
        return true;
    }

    /* parseRhs()
        @return     true if valid RHS of production rule is found, else false

        Parse the right-hand side of a production rule and consume its tokens.
    */
    public boolean parseRhs() {
        if (mode == TkType.BNF_MODE) {
            // consume list of symbols
            if (!parseSymbol())
                return false;
            while (parseSymbol()) {}

            // consume optional list of alternate productions
            while (parseAlternation()) {}

            return true;
        } else if (mode == TkType.EBNF_MODE) {
            if (!parseTerm())
                return false;
            while (parseTerm()) {}

            while (parseAlternation()) {}

            return true;
        }
        return false;
    }

    /* parseAlternation()
        @return     true if valid alternate production rule is found, else false

        Parse an alternate production rule and consume its tokens.
    */
    public boolean parseAlternation() {
        trimNewlines();
        tk = lexer.peek();
        if (tk.type() != TkType.PIPE) {
            // no alternation, so just end the parsing procedure
            return false;
        }
        // consume pipe
        lexer.lex();
        return parseRhs();
    }

    /* parseProduction()
        @return     true if valid production rule was found, else false

        Parse a production rule and consume its tokens.
    */
    public boolean parseProduction() {
        if (!matchNonterminal()) {
            lexer.error(tk.startPos(), "expected nonterminal symbol");
            return false;
        }

        tk = lexer.lex();
        if (mode == TkType.BNF_MODE && tk.type() != TkType.DERIVES) {
            lexer.error(tk.startPos(), "expected \"::=\"");
            return false;
        } else if (mode == TkType.EBNF_MODE && tk.type() != TkType.EQUAL) {
            lexer.error(tk.startPos(), "expected \"=\"");
            return false;
        }

        foundUnmatchedSymbol = false;
        String unexpected;
        if (!parseRhs() && foundUnmatchedSymbol) {
            if (tk.type() == TkType.EOF)
                unexpected = "EOF";
            else if (tk.type() == TkType.NEWLINE)
                unexpected = "'\\n'";
            else
                unexpected = String.format("\"%s\"",tk.lexeme());
            lexer.error(tk.startPos(), String.format("unexpected %s",
                                                     unexpected));
            return false;
        }
        return true;
    }
}
