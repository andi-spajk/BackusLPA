# BackusLPA

BackusLPA (_**Backus**-Naur Form **L**exer, **P**arser, and **A**nalyzer_) is a
program that reads and checks formal grammars for syntactic correctness. It also
verifies if they are LL(1) or LR(1) grammars. Supplied grammars must be
specified in Backus-Naur Form or Extended Backus-Naur Form.

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
Write your BNF/EBNF grammar in a `.txt` file.

Then use the command `$ BackusLPA.bat grammar.txt` to run the program with your
specified grammar file.

Some sample grammars are provided in `examples/`.