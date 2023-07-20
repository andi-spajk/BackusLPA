/** Lexer.java

Perform lexical analysis on grammars written in BNF or EBNF. Consumes text and
produces tokens.

*/

package com.andispajk.backuslpa;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

public class Lexer {
    private Scanner reader;
    private int currPos;
    private boolean endOfFile;

    /* Lexer()
        @inputFile      the file containing a grammar

        Construct a Lexer object that will read @inputFile and lexically analyze
        it.
    */
    public Lexer(String inputFile) {
        try {
            reader = new Scanner(new File(inputFile));
        } catch (FileNotFoundException e) {
            System.err.printf("ERROR: could not open input file %s\n",
                              inputFile);
            System.exit(1);
        }
        // we will read one char at a time
        reader.useDelimiter("");
        currPos = 0;
        endOfFile = false;
    }

    /* nextChar()
        @return         the next character in the file stream

        Read and return the next character in the input file. Update currPos
        to the character's index in the line.
    */
    private char nextChar() {
        char c;
        if (reader.hasNext()) {
            c = reader.next().charAt(0);
            if (c == '\n')
                currPos = 0;
            else
                currPos++;
        } else {
            endOfFile = true;
            c = 0;
        }
        return c;
    }

    /* lex()
        @return         the next token

        Find the next token and return it.
    */
    public void lex() {
        char c;
        while (!endOfFile) {
            c = nextChar();
            System.out.printf("%c", c);
        }
    }
}