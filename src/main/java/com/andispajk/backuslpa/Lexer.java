/** Lexer.java

Perform lexical analysis on grammars written in BNF or EBNF. Consumes text and
produces tokens.

*/

package com.andispajk.backuslpa;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

public class Lexer {
    private String fileName;
    private final StringBuilder source;
    private int sourceLen;
    private int currPos;
    private int lineNum;
    private int beginningOfLine;
    private boolean endOfFile;

    /* Lexer()

        Construct a Lexer object that will read input and lexically analyze it.
    */
    public Lexer() {
        fileName = "";
        source = new StringBuilder();
        sourceLen = 0;
        currPos = 0;
        lineNum = 0;
        beginningOfLine = 0;
        endOfFile = false;
    }

    /* readFile()
        @inputFile      name of text file containing a grammar

        Open a file and read its entire contents into the Lexer. Reset the old
        values of all class variables.
    */
    public void readFile(String inputFile) {
        try {
            fileName = inputFile;
            source.setLength(0);

            Scanner reader = new Scanner(new File(inputFile));
            while (reader.hasNextLine()) {
                source.append(reader.nextLine());
                // nextLine trims newline chars, so we can add our own \n
                source.append('\n');
                // then we don't have to deal with windows \r bullshit
            }
        } catch (FileNotFoundException e) {
            System.err.printf("ERROR: could not open input file %s\n",
                              inputFile);
            System.exit(1);
        }

        sourceLen = source.length();
        currPos = 0;
        lineNum = 0;
        beginningOfLine = 0;
        endOfFile = false;
    }

    /* readString
        @inputString        a string

        Read any string into the Lexer. Reset the old values of all class
        variables.
    */
    public void readString(String inputString) {
        fileName = "";
        source.setLength(0);
        source.append(inputString);
        sourceLen = source.length();
        currPos = 0;
        lineNum = 0;
        beginningOfLine = 0;
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
        if (c == '\n') {
            beginningOfLine = currPos;
            lineNum++;
        }
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
        // Why we use peek() to update loop conditions:
        // After reading the last whitespace char, currPos increments to the
        // char right after that whitespace. If that new char is NOT whitespace,
        // we want to end the loop so nextChar() reads that new char at currPos!
        // But if we end the loop by updating c with nextChar(), we'll increment
        // currPos to the 2nd char after the last whitespace.
    }

    /* lex()
        @return         the next token

        Find the next token and return it.
    */
    public Token lex() {
        char c;
        StringBuilder lexeme = new StringBuilder();
        int lexemeStart;
        LexerState state;
        TkType type = TkType.ILLEGAL;

        // get next meaningful character
        trimLeft();
        lexemeStart = currPos;
        c = nextChar();

        if (endOfFile)
            return new Token("", TkType.EOF, currPos);

        state = LexerState.START;
        while (!endOfFile && state != LexerState.ACCEPT) {
            if (state == LexerState.START) {
                lexeme.append(c);
                if (Character.isLetterOrDigit(c) || c == '_')
                    state = LexerState.EBNF_CHAR;
                switch (c) {
                case '<':
                    state = LexerState.LCHEVRON;
                    break;
                case '\'':
                    state = LexerState.BEGIN_CHAR;
                    break;
                case '"':
                    state = LexerState.IN_STRING;
                    break;
                case '=':
                    return new Token("=", TkType.EQUAL, lexemeStart);
                case ':':
                    state = LexerState.DERIVES1;
                    break;
                case '(':
                    state = LexerState.MAYBE_LPAREN;
                    break;
                case ')':
                    return new Token(")", TkType.RPAREN, lexemeStart);
                case '{':
                    return new Token("{", TkType.LCURLY, lexemeStart);
                case '}':
                    return new Token("}", TkType.RCURLY, lexemeStart);
                case '[':
                    return new Token("[", TkType.LBRACKET, lexemeStart);
                case ']':
                    return new Token("]", TkType.RBRACKET, lexemeStart);
                case '|':
                    return new Token("|", TkType.PIPE, lexemeStart);
                case '*':
                    return new Token("*", TkType.STAR, lexemeStart);
                case '+':
                    return new Token("+", TkType.PLUS, lexemeStart);
                case '?':
                    return new Token("?", TkType.QUESTION, lexemeStart);
                case '\n':
                    return new Token("\n", TkType.NEWLINE, lexemeStart);
                case '.':
                    state = LexerState.DIRECTIVE;
                    break;
                case ';':
                    state = LexerState.INLINE_COMMENT;
                    lexeme.deleteCharAt(0);
                    break;
                case '\0':
                    return new Token("", TkType.EOF, currPos);
                }

                // no state transition ocurred, ie error
                if (state == LexerState.START) {
                    error(currPos-1, "illegal character");
                    break;
                }
                // do NOT put in a default switch case
                // switch can't check for '_' and A..Za..z0..9, so those chars
                // would trigger a default, but they're not illegal
            } else if (state == LexerState.LCHEVRON) {
                c = nextChar();
                if (Character.isLetterOrDigit(c) || c == '-') {
                    state = LexerState.BNF_CHAR;
                    lexeme.append(c);
                } else {
                    error(currPos-1, "illegal nonterminal character");
                    break;
                }
            } else if (state == LexerState.BNF_CHAR) {
                c = nextChar();
                if (Character.isLetterOrDigit(c) || c == '-') {
                    lexeme.append(c);
                } else if (c == '>') {
                    state = LexerState.ACCEPT;
                    type = TkType.BNF_IDENT;
                    lexeme.append(c);
                } else {
                    error(currPos-1, "unclosed nonterminal");
                    break;
                }
            } else if (state == LexerState.EBNF_CHAR) {
                c = peek();
                if (Character.isLetterOrDigit(c) || c == '_') {
                    // stay in this state
                    lexeme.append(c);
                    nextChar();
                } else {
                    state = LexerState.ACCEPT;
                    type = TkType.EBNF_IDENT;
                }
            } else if (state == LexerState.BEGIN_CHAR) {
                c = nextChar();
                if (c == '\\') {
                    state = LexerState.CHAR_ESCAPE;
                    lexeme.append(c);
                } else if (c == '\'') {
                    error(currPos-1, "empty char literal");
                    break;
                } else if (c >= 32 && c <= 126) {
                    state = LexerState.END_CHAR;
                    lexeme.append(c);
                } else {
                    error(currPos-1, "illegal char literal");
                    break;
                }
            } else if (state == LexerState.CHAR_ESCAPE) {
                c = nextChar();
                switch (c) {
                case '\\':
                case '\'':
                case 'n':
                case 'r':
                case 't':
                case 'b':
                case 'f':
                case 'v':
                    state = LexerState.END_CHAR;
                    lexeme.append(c);
                    break;
                }
                // no state transition happened
                if (state != LexerState.END_CHAR) {
                    // not in a default case because we can't break out of the
                    // while loop if we're in a switch
                    error(currPos-1, "illegal char escape sequence");
                    break;
                }
            } else if (state == LexerState.END_CHAR) {
                c = nextChar();
                if (c == '\'') {
                    state = LexerState.ACCEPT;
                    type = TkType.CHAR;
                    lexeme.append(c);
                } else {
                    error(currPos-1, "char literal size exceeds 1 char");
                    break;
                }
            } else if (state == LexerState.IN_STRING) {
                c = nextChar();
                if (c == '"') {
                    state = LexerState.ACCEPT;
                    type = TkType.STRING;
                    lexeme.append(c);
                } else if (c == '\\') {
                    state = LexerState.STRING_ESCAPE;
                    lexeme.append(c);
                } else if (c >= 32 && c <= 126) {
                    lexeme.append(c);
                } else {
                    error(currPos-1, "illegal string literal");
                    break;
                }
            } else if (state == LexerState.STRING_ESCAPE) {
                c = nextChar();
                switch (c) {
                case '\\':
                case '"':
                case 'n':
                case 'r':
                case 't':
                case 'b':
                case 'f':
                case 'v':
                    state = LexerState.IN_STRING;
                    lexeme.append(c);
                    break;
                }
                // no state transition happened
                if (state != LexerState.IN_STRING) {
                    // not in a default case because we can't break out of the
                    // while loop if we're in a switch
                    error(currPos-1, "illegal string escape sequence");
                    break;
                }
            } else if (state == LexerState.DERIVES1) {
                c = nextChar();
                if (c == ':') {
                    state = LexerState.DERIVES2;
                    lexeme.append(c);
                } else {
                    error(currPos-1, "expected ':'");
                    break;
                }
            } else if (state == LexerState.DERIVES2) {
                c = nextChar();
                if (c == '=') {
                    state = LexerState.ACCEPT;
                    type = TkType.DERIVES;
                    lexeme.append(c);
                } else {
                    error(currPos-1, "expected '='");
                    break;
                }
            }
        } // endwhile
        return new Token(lexeme.toString(), type, lexemeStart);
    }

    private void printCurrLine() {
        int i = beginningOfLine;
        char c = '\0';
        while (c != '\n' && i < sourceLen) {
            c = source.charAt(i);
            System.out.print(c);
            i++;
        }
        System.out.print("\n");
    }

    public void error(int errorPos, String errorMsg) {
        System.out.printf("%s:%d:%d: error: %s\n", fileName, lineNum+1,
                          errorPos, errorMsg);
        System.out.printf("    %-3d|", lineNum+1);
        printCurrLine();

        System.out.print("       |");
        StringBuilder offset = new StringBuilder();
        // TODO: 8-wide tab alignment
        for (int i = 0; i < errorPos; i++)
            offset.append(" ");
        // print(StringBuilder) auto-calls .toString()
        System.out.print(offset);
        System.out.print("^\n\n");
    }
}