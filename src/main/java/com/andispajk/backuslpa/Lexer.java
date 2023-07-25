/** Lexer.java

Perform lexical analysis on grammars written in BNF or EBNF. Consumes text and
produces tokens.

*/

package com.andispajk.backuslpa;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

public class Lexer {
    private StringBuilder source;
    private int sourceLen;
    private int currPos;
    private int lineNum;
    private boolean endOfFile;

    /* Lexer()
        @inputFile      the file containing a grammar

        Construct a Lexer object that will read @inputFile and lexically analyze
        it.
    */
    public Lexer(String inputFile) {
        source = new StringBuilder();
        try {
            Scanner reader = new Scanner(new File(inputFile));
            while (reader.hasNextLine()) {
                source.append(reader.nextLine());
                // nextLine trims newline chars, so we can add our own \n
                source.append('\n');
                // then we don't have to deal with windows \r bullshit
            }
            sourceLen = source.length();
        } catch (FileNotFoundException e) {
            System.err.printf("ERROR: could not open input file %s\n",
                              inputFile);
            System.exit(1);
        }
        currPos = 0;
        lineNum = 0;
        endOfFile = false;
    }

    /* nextChar()

        Read the next character in the input file. Update currPos to this
        character's index in the line.
    */
    private char nextChar() {
        char c;
        if (currPos >= sourceLen) {
            endOfFile = true;
            return '\0';
        }

        c = source.charAt(currPos);
        currPos++;
        if (c == '\n')
            lineNum++;
        return c;
    }

    /* peek()
        @return     the char right after the char retrieved by nextChar()

        Retrieve the next character without consuming it.
    */
    private char peek() {
        if (currPos >= sourceLen)
            return '\0';
        // currPos is always one ahead of the char read by nextChar()
        return source.charAt(currPos);
    }

    /* trimLeft()

        Skip all whitespace characters at the current position of the file
        reader. this.ch will store the next, unconsumed, non-whitespace
        character in the input file.
    */
    private void trimLeft() {
        char c = peek();
        while (c == ' ' || c == '\t') {
            nextChar();
            c = peek();
        }
    }

    /* lex()
        @return         the next token

        Find the next token and return it.
    */
    public void lex() {
        char c;
        trimLeft();
        while (!endOfFile) {
            c = nextChar();
            System.out.printf("%c", c);
        }
        System.out.printf("%d lines\n", lineNum);
    }
}