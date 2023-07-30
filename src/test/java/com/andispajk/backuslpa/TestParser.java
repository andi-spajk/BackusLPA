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
}
