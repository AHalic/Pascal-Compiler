package checker;

import org.antlr.v4.runtime.Token;
// import org.antlr.v4.runtime.tree.TerminalNode;

import ast.AST;
import ast.NodeKind;

import static ast.NodeKind.ASSIGN_NODE;
import static ast.NodeKind.BOOL_VAL_NODE;
import static ast.NodeKind.BLOCK_NODE;
import static ast.NodeKind.INT_VAL_NODE;
import static ast.NodeKind.PROGRAM_NODE;
import static ast.NodeKind.STR_VAL_NODE;
import static ast.NodeKind.REAL_VAL_NODE;
import static ast.NodeKind.VAR_DECL_NODE;
import static ast.NodeKind.VAR_LIST_NODE;
import static ast.NodeKind.VAR_USE_NODE;

import parser.pascalParser;
import parser.pascalParser.AssignmentStatementContext;
import parser.pascalParser.CompoundStatementContext;
import parser.pascalParser.ConditionalStatementContext;
import parser.pascalParser.ExprFalseContext;
import parser.pascalParser.ExprIntegerValContext;
import parser.pascalParser.ExprRealValContext;
import parser.pascalParser.ExprStrValContext;
import parser.pascalParser.ExprTrueContext;
import parser.pascalParser.IdentifierContext;
import parser.pascalParser.IdentifierListContext;
import parser.pascalParser.ProgramContext;
import parser.pascalParser.SimpleStatementContext;
import parser.pascalParser.StructuredStatementContext;
import parser.pascalParser.VariableDeclarationContext;
import parser.pascalParser.VariableDeclarationPartContext;
import parser.pascalParserBaseVisitor;

import tables.StrTable;
import tables.VarTable;

import typing.Conv;
import typing.Conv.Unif;
import typing.Type;
import static typing.Conv.I2R;
import static typing.Type.BOOL_TYPE;
import static typing.Type.INT_TYPE;
import static typing.Type.NO_TYPE;
import static typing.Type.REAL_TYPE;
import static typing.Type.STR_TYPE;

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

    	return new AST(VAR_USE_NODE, idx, vt.getType(idx));
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

        return new AST(VAR_DECL_NODE, idx, lastDeclType);
    }

    // ----------------------------------------------------------------------------
    // Type checking and inference.

    private static void typeError(int lineNo, String op, Type t1, Type t2) {
    	System.out.printf("SEMANTIC ERROR (%d): incompatible types for operator '%s', LHS is '%s' and RHS is '%s'.\n",
    			lineNo, op, t1.toString(), t2.toString());
    	System.exit(1);
    }

    private static void checkBoolExpr(int lineNo, String cmd, Type t) {
        if (t != BOOL_TYPE) {
            System.out.printf("SEMANTIC ERROR (%d): conditional expression in '%s' is '%s' instead of '%s'.\n",
               lineNo, cmd, t.toString(), BOOL_TYPE.toString());
            System.exit(1);
        }
    }

    // ----------------------------------------------------------------------------

    // Exibe o conteúdo das tabelas em stdout.
    void printTables() {
        System.out.print("\n\n");
        System.out.print(st);
        System.out.print("\n\n");
    	System.out.print(vt);
    	System.out.print("\n\n");
    }

    // Exibe a AST no formato DOT em stderr.
    void printAST() {
        AST.printDot(root, vt);
    }

    // ----------------------------------------------------------------------------
    // Visitadores.

    // Visita a regra program: programHeading (INTERFACE)? block DOT
    @Override
	public AST visitProgram(ProgramContext ctx) {
    	// Visita recursivamente os filhos para construir a AST.
		AST varsSect = null;

		if (ctx.block().variableDeclarationPart().size() > 0) {
			varsSect = visit(ctx.block().variableDeclarationPart(0));
		}

    	AST stmtSect = visit(ctx.block().compoundStatement());
    	// Como esta é a regra inicial, chegamos na raiz da AST.
    	this.root = AST.newSubtree(PROGRAM_NODE, NO_TYPE, varsSect, stmtSect);

		return this.root;
	}

    // Visita a regra variableDeclarationPart: VAR variableDeclaration (SEMI variableDeclaration)* SEMI
    @Override
	public AST visitVariableDeclarationPart(VariableDeclarationPartContext ctx) {
    	AST node = AST.newSubtree(VAR_LIST_NODE, NO_TYPE);

    	for (int i = 0; i < ctx.variableDeclaration().size(); i++) {
			for (int j = 0; j < ctx.variableDeclaration(i).identifierList().identifier().size(); j++) {
				visit(ctx.variableDeclaration(i).type_());
				AST child = newVar(ctx.variableDeclaration(i).identifierList().identifier(j).IDENT().getSymbol());
				node.addChild(child);
			}
    	}
    	return node;
	}

	// Visita a regra identifierList: identifier (COMMA identifier)*
	@Override
	public AST visitVariableDeclaration(VariableDeclarationContext ctx) {
		visit(ctx.type_());
		return newVar(ctx.identifierList().identifier(0).IDENT().getSymbol());
	}

	// Visita a regra compoundStatement: BEGIN statements END
	@Override
	public AST visitCompoundStatement(CompoundStatementContext ctx) {
		AST node = AST.newSubtree(BLOCK_NODE, NO_TYPE);

		for (int i = 0; i < ctx.statements().statement().size(); i++) {
			// AST child = visit(ctx.statements().statement(i).unlabelledStatement().structuredStatement());
			AST child = visit(ctx.statements().statement(i).unlabelledStatement());
			node.addChild(child);
		}

		return node;
	}

	// Visita a regra simpleStatement: assignmentStatement | procedureStatement | gotoStatement | emptyStatement;
	@Override
	public AST visitSimpleStatement(SimpleStatementContext ctx) {
		return super.visitSimpleStatement(ctx);
	}

	// // Visita a regra structuredStatement: compoundStatement | conditionalStatement | repetetiveStatement | withStatement
	// @Override
	// public AST visitStructuredStatement(StructuredStatementContext ctx) {
	// 	return super.visitStructuredStatement(ctx);
	// }

    // Visita a regra typeIdentifier: BOOLEAN
    @Override
    public AST visitBoolType(pascalParser.BoolTypeContext ctx) {
    	this.lastDeclType = Type.BOOL_TYPE;
    	// Não tem problema retornar null aqui porque o método chamador
    	// ignora o valor de retorno.
    	return null;
    }

	// Visita a regra typeIdentifier: INTEGER
	@Override
	public AST visitIntType(pascalParser.IntTypeContext ctx) {
		this.lastDeclType = Type.INT_TYPE;
		// Não tem problema retornar null aqui porque o método chamador
    	// ignora o valor de retorno.
		return null;
	}

	// Visita a regra typeIdentifier: REAL
	@Override
	public AST visitRealType(pascalParser.RealTypeContext ctx) {
		this.lastDeclType = Type.REAL_TYPE;
		// Não tem problema retornar null aqui porque o método chamador
    	// ignora o valor de retorno.
		return null;
    }

	// Visita a regra typeIdentifier: STRING
	@Override
	public AST visitStrType(pascalParser.StrTypeContext ctx) {
		this.lastDeclType = Type.STR_TYPE;
		// Não tem problema retornar null aqui porque o método chamador
    	// ignora o valor de retorno.
		return null;
	}

	// Método auxiliar criado só para separar melhor a parte de visitador
	// da parte de análise semântica.
    private static AST checkAssign(int lineNo, AST l, AST r) {
    	Type lt = l.type;
    	Type rt = r.type;

        if (lt == BOOL_TYPE && rt != BOOL_TYPE) typeError(lineNo, ":=", lt, rt);
        if (lt == STR_TYPE  && rt != STR_TYPE)  typeError(lineNo, ":=", lt, rt);
        if (lt == INT_TYPE  && rt != INT_TYPE)  typeError(lineNo, ":=", lt, rt);

        if (lt == REAL_TYPE) {
        	if (rt == INT_TYPE) {
        		r = Conv.createConvNode(I2R, r);
        	} else if (rt != REAL_TYPE) {
        		typeError(lineNo, ":=", lt, rt);
            }
        }

        return AST.newSubtree(ASSIGN_NODE, NO_TYPE, l, r);
    }

	// Visita a regra assignmentStatement: variable ASSIGN expression;
	@Override
	public AST visitAssignmentStatement(AssignmentStatementContext ctx) {
		// Visita a expressão da direita.
		AST exprNode = visit(ctx.expression());
		// Visita o identificador da esquerda.
		Token idToken = ctx.variable().identifier(0).IDENT().getSymbol();
		AST idNode = checkVar(idToken);
		// Faz as verificações de tipos.
		return checkAssign(idToken.getLine(), idNode, exprNode);
	}

	// Visita a regra bool_: TRUE
	@Override
	public AST visitExprTrue(ExprTrueContext ctx) {
		return new AST(BOOL_VAL_NODE, 1, BOOL_TYPE);
	}

	// Visita a regra bool_: FALSE
	@Override
	public AST visitExprFalse(ExprFalseContext ctx) {
		return new AST(BOOL_VAL_NODE, 0, BOOL_TYPE);
	}

	// Visita a regra unsignedInteger
	@Override
	public AST visitExprIntegerVal(ExprIntegerValContext ctx) {
		int intData = Integer.parseInt(ctx.getText());

		return new AST(INT_VAL_NODE, intData, INT_TYPE);
	}

	// visita a regra unsignedReal
	@Override
	public AST visitExprRealVal(ExprRealValContext ctx) {
		float floatData = Float.parseFloat(ctx.getText());
		
		return new AST(REAL_VAL_NODE, floatData, REAL_TYPE);
	}

	// Visita a regra unsignedConstant: string
	@Override
	public AST visitExprStrVal(ExprStrValContext ctx) {
		// Adiciona a string na tabela de strings.
		int idx = st.putStr(ctx.string().getText());
		// Campo 'data' do nó da AST guarda o índice na tabela.
		return new AST(STR_VAL_NODE, idx, STR_TYPE);
	}

	// // Visita a regra identifier: IDENT
	// @Override
	// public AST visitIdentifier(IdentifierContext ctx) {
	// 	return checkVar(ctx.IDENT().getSymbol());
	// }

}