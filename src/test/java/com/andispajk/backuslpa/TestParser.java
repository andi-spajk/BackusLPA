package com.andispajk.backuslpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@TestMethodOrder(OrderAnnotation.class)
public class TestParser {
    private Lexer lexer;
    private Parser parser;
    private Token tk;

    @BeforeEach
    public void setUp() {
        lexer = new Lexer();
        parser = new Parser(lexer);
    }

    @Test
    @Order(1)
    public void testTrimNewlines() {
        lexer.readString("\n\n<hi>\n");
        parser.trimNewlines();
        tk = lexer.lex();
        assertEquals("<hi>", tk.lexeme());
        assertEquals(TkType.BNF_IDENT, tk.type());
        assertEquals(2, tk.startPos());
        parser.trimNewlines();
        tk = lexer.lex();
        assertEquals(TkType.EOF, tk.type());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "\n",
        "\n\n",
        " \t  \n   \t\n  \n\t\t \n  "
    })
    @Order(2)
    public void testOnlyNewlines(String input) {
        lexer.readString(input);
        parser.trimNewlines();
        tk = lexer.lex();
        assertEquals(TkType.EOF, tk.type());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "\t\t.BNF\n",
        ".EBNF",
        "\n.BNF",
        "\n\n\n\n\n\n\t\t.EBNF"
    })
    @Order(3)
    public void testParseDirective(String input) {
        lexer.readString(input);
        parser.trimNewlines();
        assertTrue(parser.parseDirective());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<nonterminal>",
        "::=",
        "     \"woah memes\""
    })
    @Order(4)
    public void parseDirectiveError(String input) {
        lexer.readString(input);
        parser.trimNewlines();
        assertFalse(parser.parseDirective());
    }

    @Test
    @Order(5)
    public void testMatchNonterminal() {
        lexer.readString(".bnf");
        assertTrue(parser.parseDirective());
        lexer.readString("<bnf-non-terminal> bad_ebnf");
        assertTrue(parser.matchNonterminal());
        assertFalse(parser.matchNonterminal());

        lexer.readString(".ebnf");
        assertTrue(parser.parseDirective());
        lexer.readString("   ebnf_nonterminal12345\n <WTFFFF9999>");
        assertTrue(parser.matchNonterminal());
        tk = lexer.lex();
        assertEquals(TkType.NEWLINE, tk.type());
        assertFalse(parser.matchNonterminal());
    }

    @Test
    @Order(6)
    public void testParseSymbol() {
        lexer.readString(".bnf");
        assertTrue(parser.parseDirective());
        lexer.readString("<bnf-non-terminal> \"hello\" '$' *");
        assertTrue(parser.parseSymbol());
        assertTrue(parser.parseSymbol());
        assertTrue(parser.parseSymbol());
        assertFalse(parser.parseSymbol());

        lexer.readString(".EBNF");
        assertTrue(parser.parseDirective());
        lexer.readString("\"look\" '>' 9ebnf9nonterminal9 <no>");
        assertTrue(parser.parseSymbol());
        assertTrue(parser.parseSymbol());
        assertTrue(parser.parseSymbol());
        assertFalse(parser.parseSymbol());

        lexer.readString("whatif <error");
        assertTrue(parser.parseSymbol());
        assertFalse(parser.parseSymbol());
    }

    @Test
    @Order(7)
    public void testMatchModifier() {
        //                1 23 45 67
        lexer.readString("* ++ *? ?? nomore");
        for (int i = 0; i < 7; i++)
            assertTrue(parser.matchModifier());
        assertFalse(parser.matchModifier());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "nonterminal*",
        "\"withspace\" +",
        "'*'?",
    })
    @Order(8)
    public void testParseFactor(String input) {
        lexer.readString(input);
        assertTrue(parser.parseFactor());
        tk = lexer.lex();
        assertEquals(TkType.EOF, tk.type());
    }

    @Test
    @Order(9)
    public void testParseFactorNoModifier() {
        lexer.readString("notmodifier (\n");
        assertTrue(parser.parseFactor());
        tk = lexer.lex();
        assertEquals(TkType.LPAREN, tk.type());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<symbol> <symbol2> \"wow\" <more> 'k'\n",
        "'a' | 'b' | 'c'",
        "<symbol>\n| <newline-occurred>  \n\n| <two-newlines>\n",
        "\"grammar\" \n | <rules> <are> <cool> \n | 'b' 'y' 'e'"
    })
    @Order(10)
    public void testBNFparseRhsAndAlternation(String input) {
        // must test both rhs and alternation since they're mutually recursive
        lexer.readString(".BNF");
        parser.parseDirective();
        lexer.readString(input);
        assertTrue(parser.parseRhs());
        tk = lexer.lex();
        assertEquals(TkType.EOF, tk.type());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "term1 \t '.' \t term2 \t \">>>\" \t term3",
        "q | x | c",
        "alts\n | newline \n\n | two \"of them\"",
        "loooooong | looooooong more more more | 'a' 'b' 'c'",
        "(match (\"the\" ( parentheses ) ) )\n | [add (modifiers)*] {lol}",
        "([{{([{([({\"lol\"})])}])}}])",
        "(a)*(a)+(a)?",
        "(alts | inside) \n | [brackets | wow] \n | {\"epic\" | hi}",
        "(alts | with \n | newlines ) \n | [alt \n | newline] \n | {alt \n | alt}",
        "(one)",
        "[two]",
        "{three}"
    })
    @Order(11)
    public void testEBNFparseRhs(String input) {
        // must test parseTerm, parseAlternation, and parseRhs all at once
        // since they are all mutually recursive
        lexer.readString(".EBNF");
        parser.parseDirective();
        lexer.readString(input);
        assertTrue(parser.parseRhs());
        tk = lexer.lex();
        assertEquals(TkType.EOF, tk.type());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "  |",
        "::=",
        "\n"
    })
    @Order(12)
    public void testBNFparseRhsError(String input) {
        lexer.readString(".BNF");
        parser.parseDirective();
        lexer.readString(input);
        assertFalse(parser.parseRhs());
        assertTrue(parser.foundUnmatchedSymbol());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<bad-rhs> ::=\n",
        "<more-bad>\n | \n",
        "<more-bad>\n | \n <surprise>"
    })
    @Order(13)
    public void testBNFparseRhsError2ndCall(String input) {
        lexer.readString(".BNF");
        parser.parseDirective();
        lexer.readString(input);
        assertTrue(parser.parseRhs());
        parser.resetUnmatchedSymbol();
        assertFalse(parser.parseRhs());
        assertTrue(parser.foundUnmatchedSymbol());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "()",
        "[]",
        "{}",
        "*+?",
        "\n",
        "(oops",
        "[ it \"doesn't close o h noes\"\n",
        "{{{{lol"
    })
    @Order(14)
    public void testEBNFparseRhsError(String input) {
        lexer.readString(".EBNF");
        parser.parseDirective();
        lexer.readString(input);
        assertFalse(parser.parseRhs());
        assertTrue(parser.foundUnmatchedSymbol());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "(watch)**",
        "(unbalanced(like(hell(uhoh))))))))",
        "[b]*",
        "[b]+",
        "[b]?",
        "{c}*",
        "{c}+",
        "{c}?"
        })
    @Order(15)
    public void testEBNFparseRhsError2ndCall(String input) {
        lexer.readString(".EBNF");
        parser.parseDirective();
        lexer.readString(input);
        assertTrue(parser.parseRhs());
        parser.resetUnmatchedSymbol();
        assertFalse(parser.parseRhs());
        assertTrue(parser.foundUnmatchedSymbol());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<1> ::= <symbol> <symbol2> \"wow\" <more> 'k'\n",
        "<2> ::= 'a' | 'b' | 'c'",
        "<3> ::= <symbol>\n| <newline-occurred>  \n\n| <two-newlines>\n",
        "<4> ::= \"grammar\" \n | <rules> <are> <cool> \n | 'b' 'y' 'e'"
    })
    @Order(16)
    public void testBNFparseProduction(String input) {
        lexer.readString(".bnf");
        parser.parseDirective();
        lexer.readString(input);
        assertTrue(parser.parseProduction());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "uh = term1 \t '.' \t term2 \t \">>>\" \t term3",
        "uh = q | x | c",
        "uh = alts\n | newline \n\n | two \"of them\"",
        "uh = loooooong | looooooong more more more | 'a' 'b' 'c'",
        "uh = (match (\"the\" ( parentheses ) ) )\n | [add (modifiers)*] {lol}",
        "uh = ([{{([{([({\"lol\"})])}])}}])",
        "uh = (a)*(a)+(a)?",
        "uh = (alts | inside) \n | [brackets | wow] \n | {\"epic\" | hi}",
        "uh = (alts | with \n | newlines ) \n | [alt \n | newline] \n | {alt \n | alt}",
        "uh = (one)",
        "uh = [two]",
        "uh = {three}"
    })
    @Order(17)
    public void testEBNFparseProduction(String input) {
        lexer.readString(".ebnf");
        parser.parseDirective();
        lexer.readString(input);
        assertTrue(parser.parseProduction());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<rule> ::=   |",
        "<rule> ::= ::=",
        "<rule> ::= \n",
        "<rule> ::= "
    })
    @Order(18)
    public void testBNFparseProductionError(String input) {
        lexer.readString(".bnf");
        parser.parseDirective();
        lexer.readString(input);
        assertFalse(parser.parseProduction());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "rule = ()",
        "rule = []",
        "rule = {}",
        "rule = *+?",
        "rule = \n",
        "rule = (oops",
        "rule = [ it \"doesn't close o h noes\"\n",
        "rule = {{{{lol",
        "rule = "
    })
    @Order(19)
    public void testEBNFparseProductionError(String input) {
        lexer.readString(".ebnf");
        parser.parseDirective();
        lexer.readString(input);
        assertFalse(parser.parseProduction());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<rule> ::= <bad-rhs> ::=\n",
        "<rule> ::= <more-bad>\n | \n",
        "<rule> ::= <more-bad>\n | \n <surprise>"
    })
    @Order(20)
    public void testBNFparseProductionError2ndCall(String input) {
        lexer.readString(".bnf");
        parser.parseDirective();
        lexer.readString(input);
        assertTrue(parser.parseProduction());
        assertFalse(parser.parseProduction());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "rule = (watch)**",
        "rule = (unbalanced(like(hell(uhoh))))))))",
        "rule = [b]*",
        "rule = [b]+",
        "rule = [b]?",
        "rule = {c}*",
        "rule = {c}+",
        "rule = {c}?"
    })
    @Order(21)
    public void testEBNFparseProductionError2ndCall(String input) {
        lexer.readString(".ebnf");
        parser.parseDirective();
        lexer.readString(input);
        assertTrue(parser.parseProduction());
        assertFalse(parser.parseProduction());
    }
}
