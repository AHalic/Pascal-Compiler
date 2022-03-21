
import java.io.File;
import parser.PascalParser;
import parser.PascalLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.BufferedWriter;
import java.io.FileWriter;

import code.CodeGen;
import checker.SemanticChecker;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {
    public static void main(String[] args) {
        try {
            //  Verifica se foi informado um arquivo de entrada
            if (args.length == 0) {
                System.out.println("Provide an input file.");
                return;

            } else if (args.length == 1) {
                System.out.println("Provide an ouput file.");
                return;

            } else {
                // Verifica se o arquivo informado existe
                File file = new File(args[0]);
                
                if (file.exists()) {
                    // Lê o arquivo caso exista
                    CharStream input = CharStreams.fromFileName(args[0], UTF_8);
                    PascalLexer lexer = new PascalLexer(input);
                    PascalParser parser = new PascalParser(new CommonTokenStream(lexer));
                    ParseTree tree = parser.program();

                    // Retorna caso ocorra erros durante o parser
                    if (parser.getNumberOfSyntaxErrors() != 0) {
                        return;
                    }

                    // Faz a análise semântica
                    SemanticChecker checker = new SemanticChecker();
                    checker.visit(tree);

                    // O parser ocorreu sem problemas
                    System.out.println("Parse successfull!");

                    // Verifica se foi informado a flag da ast
                    if (args.length >= 3 && args[2].contains("--tables"))
                        checker.printTables();
                    
                    // Verifica se foi informado a flag da ast
                    if (args.length >= 3 && args[2].contains("--ast"))
                        checker.printAST();

                    // Carrega o arquivo de saída
                    FileWriter tmp = new FileWriter(args[1]);
                    BufferedWriter outputFile = new BufferedWriter(tmp);

                    // Executa o gerador de código
                    CodeGen codeGen = new CodeGen(
                        checker.stringTable,
                        checker.ProgramVariableTable,
                        checker.functionTable,
                        outputFile);
                    codeGen.execute(checker.getAST());

                } else {
                    // Arquivo informado, porém não existe
                    System.out.println("Provided file does not exist.");
                    return;
                }
            }

        } catch (Exception exception) {
            // Mostra a mensagem da exceção caso tenha ocorrido
            System.err.println(
                exception.getClass().getSimpleName() + " at " + exception.getMessage());
        }
    }
}
