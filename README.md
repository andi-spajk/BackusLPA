# BackusLPA

BackusLPA (_**Backus**-Naur Form **L**exer, **P**arser, and **A**nalyzer_) is a
program that reads and checks formal grammars for syntactic correctness. It also
verifies if they are LL(1) or LR(1) grammars. Supplied grammars must be
specified in Backus-Naur Form or Extended Backus-Naur Form.

This project is still a work in progress.


# TODO

* LL(1) and LR(1) analysis


# Building

## Requirements
* Maven
* Java 19
* Windows or Linux

## Instructions
* `$ mvn package` to build BackusLPA.
* `$ mvn test` to run the BackusLPA test suites.
* `$ mvn clean` to remove all compiled files and reset to a clean, unbuilt
slate.


# Grammars

Write your BNF/EBNF grammar in a `.txt` file.
* The grammar file must begin by specifying BNF or EBNF mode, using either the
  directive `.BNF` or `.EBNF` respectively.
* One production rule cannot span multiple lines.
  * Alternate productions can go onto new line(s), but the line must begin with
  `|`.
* The empty string is always represented by `""`, not `''`.
* Some sample grammars that illustrate these rules are provided in `examples/`.

# Usage

Use one of these commands to run the program with your specified grammar
file:
* Windows: `$ BackusLPA.bat grammar.txt` 
* Linux: `$ ./BackusLPA.sh grammar.txt`
