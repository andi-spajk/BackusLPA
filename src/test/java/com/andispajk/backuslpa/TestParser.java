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
}
