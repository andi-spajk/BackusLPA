# BackusLPA

BackusLPA (Backus-Naur Form Lexer, Parser, and Analyzer) is a program that
reads and checks formal grammars (specified in Backus-Naur Form or Extended
Backus-Naur Form) for syntactic correctness. It also verifies LL(1) and LR(1)
grammars.

This project is still a work in progress.


# TODO

* Lexer
* Parser
* LL(1) and LR(1) analysis
* Linux support


# Building

## Requirements
* Maven
* Java 19
* Windows

## Instructions
* `$ mvn package` to build BackusLPA.
* `$ mvn test` to run the BackusLPA test suites.
* `$ mvn compile` to just compile BackusLPA source files.
* `$ mvn clean` to remove all compiled files and reset to a clean, unbuilt
slate.


# Usage
Write your BNF/EBNF grammars in a `.txt` file. Then use the command
`$ BackusLPA.bat grammar.txt` to run the program with your specified grammar
file. Some sample grammars are provided in `examples/`.