# Pascal-Compiler

This is an implementation of a Pascal compiler using Java made by:
Gabriel Soares, Marco Oliari and Sophie Dilhon

In this stage of the project, the compiler generates JVM (bytecode) based on the AST implemented in a prior stage.

## How to run
To compile the code, you can run in your command line:
```sh
make
make run_java FILE=examples/file_name.pas OUTPUT=outputs/file_name.pas.j
```

The `file_name` must be replaced. After that the class file will be generated, to run that you should do:

```sh
java ClassName
```
