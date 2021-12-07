# Pascal-Compiler

This is an implementation of a Pascal compiler using Java made by:

Gabriel Soares, Marco Oliari and Sophie Dilhon

In this stage of the project, the lexer and the parser were implemented. The lexer used was originally made by [antlr4](https://github.com/antlr/grammars-v4/tree/master/pascal).

## How to run
To start, it is necessary to enter the directory where you may find the code and the Makefile, thus run in the command line:
```sh
cd src
```

To compile and run the code, you'll first need to edit the ROOT and ANTLR_PATH variables in the Makefile. Then you can run in your command line:
```sh
make
make runall
```