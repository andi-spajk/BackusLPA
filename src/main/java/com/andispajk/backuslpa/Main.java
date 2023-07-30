/** Main.java

Read a grammar file written in BNF or EBNF, parse it for syntax errors, and
analyze if it specifies an LL(1) or LR(1) grammar.

*/

package com.andispajk.backuslpa;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("invalid number of cmdline args");
            System.exit(1);
        }
        Lexer lexer = new Lexer();
        lexer.readFile(args[0]);
        Parser parser = new Parser(lexer);
        if (parser.parseGrammar())
            System.out.println("success");
    }
}