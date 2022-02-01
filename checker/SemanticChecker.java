package checker;

import org.antlr.v4.runtime.Token;
// import org.antlr.v4.runtime.tree.TerminalNode;

import parser.pascalParser.ExprStrValContext;
import parser.pascalParserBaseVisitor;

import tables.StrTable;
import tables.VarTable;

import typing.Type;

import ast.AST;

public class SemanticChecker extends pascalParserBaseVisitor<AST> {
    private StrTable st = new StrTable();
    private VarTable vt = new VarTable();

    Type lastDeclType;

    AST root;

    // Testa se o dado token foi declarado antes.
    // Se sim, cria e retorna um nó de 'var use'.
    AST checkVar(Token token) {
    	String text = token.getText();
    	int line = token.getLine();
   		int idx = vt.lookupVar(text);
    	if (idx == -1) {
    		System.err.printf("SEMANTIC ERROR (%d): variable '%s' was not declared.\n", line, text);
    		// A partir de agora vou abortar no primeiro erro para facilitar.
    		System.exit(1);
            return null; // Never reached.
        }

        return null;
    	// return new AST(VAR_USE_NODE, idx, vt.getType(idx));
    }

    // Cria uma nova variável a partir do dado token.
    // Retorna um nó do tipo 'var declaration'.
    AST newVar(Token token) {
    	String text = token.getText();
    	int line = token.getLine();
   		int idx = vt.lookupVar(text);
        if (idx != -1) {
        	System.err.printf("SEMANTIC ERROR (%d): variable '%s' already declared at line %d.\n", line, text, vt.getLine(idx));
        	// A partir de agora vou abortar no primeiro erro para facilitar.
        	System.exit(1);
            return null; // Never reached.
        }
        idx = vt.addVar(text, line, lastDeclType);

        return null;
        // return new AST(VAR_DECL_NODE, idx, lastDeclType);
    }

    // ----------------------------------------------------------------------------
    // Type checking and inference.

    private static void typeError(int lineNo, String op, Type t1, Type t2) {
    	System.out.printf("SEMANTIC ERROR (%d): incompatible types for operator '%s', LHS is '%s' and RHS is '%s'.\n",
    			lineNo, op, t1.toString(), t2.toString());
    	System.exit(1);
    }

    // ----------------------------------------------------------------------------

    // Exibe o conteúdo das tabelas em stdout.
    void printTables() {
        System.out.print("\n\n");
        System.out.print(st);
        System.out.print("\n\n");
    	// System.out.print(vt);
    	// System.out.print("\n\n");
    }

    // ----------------------------------------------------------------------------
    // Visitadores.

    @Override
	// Visita a regra expr: STR_VAL
	public AST visitExprStrVal(ExprStrValContext ctx) {
		// Adiciona a string na tabela de strings.
		int key = st.putStr(ctx.string().getText());
		// Campo 'data' do nó da AST guarda o índice na tabela.
		// return new AST(STR_VAL_NODE, idx, STR_TYPE);
        return null;
	}

}