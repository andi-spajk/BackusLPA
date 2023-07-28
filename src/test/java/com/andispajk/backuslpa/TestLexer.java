package com.andispajk.backuslpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


public class TestLexer {
    private Lexer lexer;
    private Token tk;

    @BeforeEach
    public void setUp() {
        lexer = new Lexer();
    }

    @Test
    public void testLexSingleChars() {
        int i;
        String input;

        input = "=)*|{}\n[+]?\n";
        lexer.readString(input);
        TkType[] expectedTypes = {TkType.EQUAL, TkType.RPAREN, TkType.STAR,
                                  TkType.PIPE, TkType.LCURLY, TkType.RCURLY,
                                  TkType.NEWLINE, TkType.LBRACKET, TkType.PLUS,
                                  TkType.RBRACKET, TkType.QUESTION,
                                  TkType.NEWLINE, TkType.EOF};

        for (i = 0; i < expectedTypes.length; i++) {
            tk = lexer.lex();
            // verify type, start position, and lexeme
            assertEquals(expectedTypes[i], tk.type());
            // no spaces/tabs so we can use i as expected startPos:
            assertEquals(i, tk.startPos());
            if (tk.type() != TkType.EOF) {
                assertEquals(1, tk.lexeme().length());
                assertEquals(input.charAt(i), tk.lexeme().charAt(0));
            } else {
                assertEquals(0, tk.lexeme().length());
            }
        }

        //       012 3456789 0 1234567890 1 23456789 01
        input = "   \t=  ) *\t\t| {    } \n\t[ + ] ?\n";
        // test if repeated call to readString() will reset class vars properly
        lexer.readString(input);
        int[] expectedStarts = {4, 7, 9, 12, 14, 19, 21, 23, 25, 27, 29, 30,
                                31};

        for (i = 0; i < expectedTypes.length; i++){
            tk = lexer.lex();
            // verify type, start position, and lexeme
            assertEquals(expectedTypes[i], tk.type());
            assertEquals(expectedStarts[i], tk.startPos());
            if (tk.type() != TkType.EOF) {
                assertEquals(1, tk.lexeme().length());
                assertEquals(input.charAt(expectedStarts[i]),
                             tk.lexeme().charAt(0));
            } else {
                assertEquals(0, tk.lexeme().length());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    ", "\t  ", " \t   "})
    public void testEOF(String input) {
        lexer.readString(input);
        tk = lexer.lex();
        assertEquals(TkType.EOF, tk.type());
        // an EOF token's startPos and lexeme should be disregarded by the user
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '"', textBlock = """
        "<a>",                      3,  0
        "  <->",                    3,  2
        "\t<identifier>",           12, 1
        "<many1-words2-here3>   ",  20, 0
        """)
    public void testLexBNFident(String input, int len, int startPos) {
        lexer.readString(input);
        tk = lexer.lex();
        assertEquals(TkType.BNF_IDENT, tk.type());
        assertEquals(input.trim(), tk.lexeme());
        assertEquals(len, tk.lexeme().length());
        assertEquals(startPos, tk.startPos());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<error",
        "<>",
        "<!@#$%>",
        "<bad\t  ",
        "<   >"
    })
    public void testLexBNFidentError(String input) {
        lexer.readString(input);
        tk = lexer.lex();
        assertEquals(TkType.ILLEGAL, tk.type());
    }

}