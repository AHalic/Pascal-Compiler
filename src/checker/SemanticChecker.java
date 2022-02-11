package checker;

import org.antlr.v4.runtime.Token;
import java.util.List;
import ast.AST;
import parser.PascalParser;
import parser.PascalParserBaseVisitor;
import tables.StringTable;
import tables.VariableTable;
import typing.Conversion;
import typing.Type;
import static typing.Type.*;
import parser.PascalParser.*;
import static ast.NodeKind.*;

import exception.SemanticException;
import static typing.Conversion.*;

public class SemanticChecker extends PascalParserBaseVisitor<AST> {
    //
    private StringTable stringTable = new StringTable();
    private VariableTable variableTable = new VariableTable();
    
    //
    Type lastDeclaredType;

    //
    AST root;

    public AST checkVariable(Token token) {
        String text = token.getText();
        int line = token.getLine();
        
        if (!variableTable.contains(text)) {
            String message = String.format(
                "line %d: variable '%s' was not declared.\n",
                line, text);
            throw new SemanticException(message);
        }

        int idx = variableTable.getIndex(text);
        return new AST(VAR_USE_NODE, idx, variableTable.getType(idx));
    }

    public AST newVariable(Token token) {
        String text = token.getText();
        int line = token.getLine();
        
        if (this.variableTable.contains(text)) {
            int idx = variableTable.getIndex(text);

            String message = String.format(
                "line %d: variable '%s' already declared at line %d.\n",
                line, text, variableTable.get(idx).getLine());

            throw new SemanticException(message);
        }

        int idx = variableTable.put(text, line, lastDeclaredType);
        return new AST(VAR_DECL_NODE, idx, lastDeclaredType);
    }

    public void printTables() {
        System.out.println();
        System.out.println(stringTable);
        System.out.println(variableTable);
    }

    @Override
    public AST visitExprStrVal(ExprStrValContext ctx) {
        int idx = stringTable.add(ctx.string().getText());
        return new AST(STR_VAL_NODE, idx, STR_TYPE);
    }

    @Override
    public AST visitVariableDeclarationPart(VariableDeclarationPartContext ctx) {
        AST node = AST.newSubtree(VAR_LIST_NODE, NO_TYPE);

        for (VariableDeclarationContext varList : ctx.variableDeclaration()) {
            List<IdentifierContext> idList = varList.identifierList().identifier();
            visit(varList.type_());

            for (IdentifierContext idContext : idList) {
                node.addChild(newVariable(idContext.IDENT().getSymbol()));
            }
        }

        return node;
    }

    @Override
    public AST visitVariableDeclaration(VariableDeclarationContext ctx) {
        visit(ctx.type_());
        return newVariable(ctx.identifierList().identifier(0).IDENT().getSymbol());
    }

    @Override
    public AST visitProgram(ProgramContext ctx) {
        AST varsSect = null;

        if (ctx.block().variableDeclarationPart().size() > 0) {
            varsSect = visit(ctx.block().variableDeclarationPart(0));
        }

        AST stmtSect = visit(ctx.block().compoundStatement());
        this.root = AST.newSubtree(PROGRAM_NODE, NO_TYPE, varsSect, stmtSect);
        return this.root;
    }

    @Override
    public AST visitAssignmentStatement(AssignmentStatementContext ctx) {
        if (ctx.variable().size() > 1) {
            Token leftToken = ctx.variable(0).identifier(0).IDENT().getSymbol();
            Token rightToken = ctx.variable(1).identifier(0).IDENT().getSymbol();
            AST leftNode = checkVariable(leftToken);
            AST rightNode = checkVariable(rightToken);
            return checkAssign(leftToken.getLine(), leftNode, rightNode);
        } else {
            AST exprNode = visit(ctx.expression());
            Token idToken = ctx.variable(0).identifier(0).IDENT().getSymbol();
            AST idNode = checkVariable(idToken);
            return checkAssign(idToken.getLine(), idNode, exprNode);
        }
    }

    //
    private static void typeError(int lineNo, String op, Type t1, Type t2) {
        String message = String.format(
            "line %d: incompatible types for operator '%s'," +
            " LHS is '%s' and RHS is '%s'.",
            lineNo, op, t1.toString(), t2.toString());
        throw new SemanticException(message);
    }

    private static AST checkAssign(int lineNo, AST l, AST r) {
        Type lt = l.type;
        Type rt = r.type;

        if (lt == BOOL_TYPE && rt != BOOL_TYPE) typeError(lineNo, ":=", lt, rt);
        if (lt == STR_TYPE  && rt != STR_TYPE)  typeError(lineNo, ":=", lt, rt);
        if (lt == INT_TYPE  && rt != INT_TYPE)  typeError(lineNo, ":=", lt, rt);

        if (lt == REAL_TYPE) {
            if (rt == INT_TYPE) {
                r = Conversion.createConversionNode(INT2REAL, r);
            } else if (rt != REAL_TYPE) {
                typeError(lineNo, ":=", lt, rt);
            }
        }

        return AST.newSubtree(ASSIGN_NODE, NO_TYPE, l, r);
    }

    // TODO: Adicionar char e string.
    @Override
    public AST visitExprIntegerVal(ExprIntegerValContext ctx) {
        int intData = Integer.parseInt(ctx.getText());
        return new AST(INT_VAL_NODE, intData, INT_TYPE);
    }

    @Override
    public AST visitExprRealVal(ExprRealValContext ctx) {
        float floatData = Float.parseFloat(ctx.getText());
        return new AST(REAL_VAL_NODE, floatData, REAL_TYPE);
    }

    @Override
    public AST visitSignedFactor(SignedFactorContext ctx) {
        AST exprNode = visit(ctx.factor());
        
        if (ctx.MINUS() != null) {
            if (exprNode.type == REAL_TYPE) {
                return new AST(exprNode.kind, exprNode.floatData*(-1), REAL_TYPE);
            } else {
                return new AST(exprNode.kind, exprNode.intData*(-1), INT_TYPE);
            }
        }
        
        return exprNode;
    }

    @Override
    public AST visitExprTrue(ExprTrueContext ctx) {
        return new AST(BOOL_VAL_NODE, 1, BOOL_TYPE);
    }

    @Override
    public AST visitExprFalse(ExprFalseContext ctx) {
        return new AST(BOOL_VAL_NODE, 0, BOOL_TYPE);
    }

    // Types declarations
    public AST visitStrType(StrTypeContext ctx) {
        this.lastDeclaredType = Type.STR_TYPE;
        return null;
    }

    public AST visitIntType(IntTypeContext ctx) {
        this.lastDeclaredType = Type.INT_TYPE;
        return null;
    }

    public AST visitRealType(RealTypeContext ctx) {
        this.lastDeclaredType = Type.REAL_TYPE;
        return null;
    }

    public AST visitCharType(CharTypeContext ctx) {
        this.lastDeclaredType = Type.CHAR_TYPE;
        return null;
    }

    public AST visitBoolType(BoolTypeContext ctx) {
        this.lastDeclaredType = Type.BOOL_TYPE;
        return null;
    }

    @Override
    public AST visitSimpleExpression(SimpleExpressionContext ctx) {
        if (ctx.additiveoperator() != null) {
            AST left = visit(ctx.term());
            AST right = visit(ctx.simpleExpression());

            Unified unified;

            if (ctx.additiveoperator().operator.getType() == PascalParser.PLUS) {
                unified = left.type.unifyPlus(right.type);
            } else {
                unified = left.type.unifyOtherArith(right.type);
            }

            //
            if (unified.type == NO_TYPE) {
                typeError(
                    ctx.additiveoperator().operator.getLine(),
                    ctx.additiveoperator().operator.getText(),
                    left.type, right.type);
            }

            left = Conversion.createConversionNode(unified.leftConversion, left);
            right = Conversion.createConversionNode(unified.rightConversion, right);

            if (ctx.additiveoperator().operator.getType() == PascalParser.PLUS) {
                return AST.newSubtree(PLUS_NODE, unified.type, left, right);
            } else { // MINUS
                return AST.newSubtree(MINUS_NODE, unified.type, left, right);
            }
        }

        return super.visitSimpleExpression(ctx);
    }

    @Override
    public AST visitCompoundStatement(CompoundStatementContext ctx) {
        AST node = AST.newSubtree(BLOCK_NODE, NO_TYPE);

        for (StatementContext context : ctx.statements().statement()) {
            node.addChild(visit(context.unlabelledStatement()));
        }

        return node;
    }

    @Override
    public AST visitExpression(ExpressionContext ctx) {
        if (ctx.expression() != null) {
            AST left = visit(ctx.simpleExpression());
            AST right = visit(ctx.expression());

            //
            Type leftType = left.type;
            Type rightType = right.type;
            Unified unified = leftType.unifyComp(rightType);

            if (unified.type == NO_TYPE) {
                typeError(
                    ctx.relationaloperator().operator.getLine(),
                    ctx.relationaloperator().operator.getText(),
                    leftType, rightType);
            }

            left = Conversion.createConversionNode(unified.leftConversion, left);
            right = Conversion.createConversionNode(unified.rightConversion, right);

            return relationalNode(
                ctx.relationaloperator().operator,
                unified, left, right);
        }

        return super.visitExpression(ctx);
    }

    @Override
    public AST visitFactor(FactorContext ctx) {
        if (ctx.variable() != null) {
            return checkVariable(ctx.variable().identifier(0).IDENT().getSymbol());
        }

        if (ctx.LPAREN() != null) {
            return visitExpression(ctx.expression());
        }

        return super.visitFactor(ctx);
    }

    private AST relationalNode(Token operator, Unified unified, AST left, AST right) {
        switch (operator.getType()) {
            case PascalParser.EQUAL:
                return AST.newSubtree(EQ_NODE, unified.type, left, right);
            case PascalParser.LT:
                return AST.newSubtree(LT_NODE, unified.type, left, right);
            case PascalParser.LE:
                return AST.newSubtree(LE_NODE, unified.type, left, right);
            case PascalParser.GE:
                return AST.newSubtree(GE_NODE, unified.type, left, right);
            case PascalParser.GT:
                return AST.newSubtree(GT_NODE, unified.type, left, right);
            case PascalParser.NOT_EQUAL:
                return AST.newSubtree(NOT_EQUAL_NODE, unified.type, left, right);
            default:
                String message = String.format(
                    "%line %d: invalid operator '%s'.\n",
                    operator.getLine(), operator.getText());
                throw new SemanticException(message);
        }
    }

    public void printAST() {
        AST.printDot(root, variableTable);
    }
}
