package checker;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import parser.pascalParser.ExprStrValContext;
import parser.pascalParserBaseVisitor;

import tables.StrTable;
// import tables.VarTable;

import typing.Type;

import ast.AST;

public class SemanticChecker extends pascalParserBaseVisitor<AST> {
    private StrTable st = new StrTable();

    Type lastDeclType;

    AST root;

    void printTables() {
        System.out.print("\n\n");
        System.out.print(st);
        System.out.print("\n\n");
    	// System.out.print(vt);
    	// System.out.print("\n\n");
    }


    @Override
	// Visita a regra expr: STR_VAL
	public AST visitExprStrVal(ExprStrValContext ctx) {
		// Adiciona a string na tabela de strings.
		String key = st.put(ctx.string().getText(), ctx.string().getText());
		// Campo 'data' do nó da AST guarda o índice na tabela.
		// return new AST(STR_VAL_NODE, idx, STR_TYPE);
        return null;
	}

}