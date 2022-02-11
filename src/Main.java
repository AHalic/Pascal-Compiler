
import java.io.File;
import parser.PascalParser;
import parser.PascalLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import checker.SemanticChecker;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {
    public static void main(String[] args) {
        try {
            // 
            if (args.length == 0) {
                System.out.println("Provide an input file.");
                return;

            } else {
                //
                File file = new File(args[0]);
                
                if (file.exists()) {
                    //
                    CharStream input = CharStreams.fromFileName(args[0], UTF_8);
                    PascalLexer lexer = new PascalLexer(input);
                    PascalParser parser = new PascalParser(new CommonTokenStream(lexer));
                    ParseTree tree = parser.program();

                    if (parser.getNumberOfSyntaxErrors() != 0) {
                        return;
                    }

                    //
                    SemanticChecker checker = new SemanticChecker();
                    checker.visit(tree);

                    //
                    System.out.println("Parse successfull!");

                    checker.printTables();
                    checker.printAST();

                } else {
                    //
                    System.out.println("Provided file does not exist.");
                    return;
                }
            }

        } catch (Exception exception) {
            //
            System.err.println(
                exception.getClass().getSimpleName() + " at " + exception.getMessage());
        }
    }
}
