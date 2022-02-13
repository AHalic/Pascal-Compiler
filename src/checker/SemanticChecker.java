package checker;

import org.antlr.v4.runtime.Token;
import java.util.List;
import java.util.ArrayList;
import ast.AST;
import parser.PascalParser;
import parser.PascalParserBaseVisitor;
import tables.Function;
import tables.FunctionTable;
import tables.StringTable;
import tables.VariableTable;
import typing.Conversion;
import typing.Type;
import static typing.Type.*;
import parser.PascalParser.*;
import static ast.NodeKind.*;

import array.Array;
import array.Range;

import exception.SemanticException;
import static typing.Conversion.*;

public class SemanticChecker extends PascalParserBaseVisitor<AST> {
    //
    private StringTable stringTable = new StringTable();
    private FunctionTable functionTable = new FunctionTable();
    
    /* Cria a tabela de variáveis e o ponteiro para não ser preciso
       diferenciar se a tabela é da função ou do programa. */
    private VariableTable ProgramVariableTable = new VariableTable();
    private VariableTable variableTable = ProgramVariableTable;

    //
    Type lastDeclaredType;
    Range lastDeclaredRange;
    Array lastArrayDeclared;

    //
    AST root;

    // Cria um nó de utilização de variável caso já tenha sido declarada
    public AST checkVariable(Token token) {
        String text = token.getText();
        int line = token.getLine();

        // Verifica se é uma chamada de função
        if (functionTable.contains(text)) {
            int idx = functionTable.getIndex(text);
            return new AST(FUNC_USE_NODE, idx, functionTable.getType(idx));
        }

        // Verifica se existe na tabela de variáveis
        if (!variableTable.contains(text)) {
            String message = String.format(
                "line %d: variable '%s' was not declared.\n",
                line, text);
            throw new SemanticException(message);
        }

        int idx = variableTable.getIndex(text);
        return new AST(VAR_USE_NODE, idx, variableTable.getType(idx));
    }

    // Cria uma nova variável caso ainda não tenha sido declarada
    public AST newVariable(Token token) {
        String text = token.getText();
        int line = token.getLine();

        // Verifica se existe uma função com mesmo nome
        if (this.functionTable.contains(text)) {
            String message = String.format(
                "line %d: A function declaration already exists for '%s'.",
                line, text);

            throw new SemanticException(message);
        }

        // Verifica se existe uma variável com o mesmo nome
        if (this.variableTable.contains(text)) {
            int idx = variableTable.getIndex(text);

            String message = String.format(
                "line %d: variable '%s' already declared at line %d.\n",
                line, text, variableTable.get(idx).getLine());

            throw new SemanticException(message);
        }
        
        // Verifica se é uma array ou um tipo normal
        if (lastDeclaredType == ARRAY_TYPE) {
            lastArrayDeclared.setName(text);
            int idx = variableTable.put(lastArrayDeclared);
            return new AST(VAR_DECL_NODE, idx, lastDeclaredType);
        } else {
            int idx = variableTable.put(text, line, lastDeclaredType);
            return new AST(VAR_DECL_NODE, idx, lastDeclaredType);
        }
    }

    // Visita a chamada de função
    @Override
    public AST visitProcedureStatement(ProcedureStatementContext ctx) {
        String name = ctx.identifier().IDENT().getSymbol().getText();
        int line = ctx.identifier().IDENT().getSymbol().getLine();

        // Verifica se a função existe na tabela
        if (!functionTable.contains(name)) {
            String message = String.format(
                "line %d: undeclared function '%s'.",
                line, name);
            throw new SemanticException(message);
        }

        return AST.newSubtree(
            FUNC_USE_NODE,
            functionTable.getType(name),
            functionTable.getIndex(name));
    }

    //
    @Override
    public AST visitExprStrVal(ExprStrValContext ctx) {
        int idx = stringTable.add(ctx.string().getText());
        return new AST(STR_VAL_NODE, idx, STR_TYPE);
    }

    //
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

    //
    @Override
    public AST visitVariableDeclaration(VariableDeclarationContext ctx) {
        visit(ctx.type_());
        return newVariable(ctx.identifierList().identifier(0).IDENT().getSymbol());
    }

    //
    @Override
    public AST visitProgram(ProgramContext ctx) {
        AST varsSect = null;
        AST funcSect = AST.newSubtree(FUNC_LIST_NODE, NO_TYPE);

        for (ProcedureAndFunctionDeclarationPartContext function :
             ctx.block().procedureAndFunctionDeclarationPart()) {
            funcSect.addChild(visit(function));
        }

        if (ctx.block().variableDeclarationPart().size() > 0) {
            varsSect = visit(ctx.block().variableDeclarationPart(0));
        }

        AST stmtSect = visit(ctx.block().compoundStatement());
        this.root = AST.newSubtree(PROGRAM_NODE, NO_TYPE, varsSect, funcSect, stmtSect);
        return this.root;
    }

    //
    @Override
    public AST visitAssignmentStatement(AssignmentStatementContext ctx) {
        // Verifica se é uma utilização de função
        if ((ctx.expression() != null) &&
            (ctx.expression().simpleExpression().term().signedFactor().factor().functionDesignator() != null)) {
            Token leftToken = ctx.variable(0).identifier(0).IDENT().getSymbol();
            AST leftNode = checkVariable(leftToken);
            AST rightNode = visit(ctx.expression());
            return checkAssign(leftToken.getLine(), leftNode, rightNode);
        }

        // Verifica se esta atribuindo uma variável ou se é um valor
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

    public AST visitFunctionDesignator(FunctionDesignatorContext ctx) {
        String name = ctx.identifier().getText();
        int line = ctx.identifier().IDENT().getSymbol().getLine();
        VariableTable table = functionTable.getVariableTable(name);
        List<ActualParameterContext> list = ctx.parameterList().actualParameter();

        // Árvore de chamada de função
        AST node = AST.newSubtree(
            FUNC_USE_NODE,
            functionTable.getType(name),
            functionTable.getIndex(name));

        // Verifica se os parâmetros estão certos
        checkMissingParameters(list.size(), table, line);
        checkExtraParameters(list.size(), list, table, line);

        // Insere todos parâmetros no nó de chamada de função
        for (int i = 0; i < list.size(); i++) {
            AST param = visit(list.get(i).expression().simpleExpression().term());
            param = createConversionNode(table.getType(i + 1), param.type, param, line);
            node.addChild(param);
        }

        return node;
    }

    // Verifica se foi informado mais parâmetros do que o necessário
    void checkExtraParameters(
            int parametersQuantity, List<ActualParameterContext> list,
            VariableTable table, int lineNo) {
        //
        if (parametersQuantity > table.size() - 1) {
            int nextParamID = parametersQuantity - 1;

            String message = String.format(
                "line %d: '%s' parameter value not referenced in " +
                "'%s' function declaration.",
                lineNo, list.get(nextParamID).getText(), table.getName(0));
            throw new SemanticException(message);
        }
    }

    // Verifica se esta faltando parâmetros
    void checkMissingParameters(int quantity, VariableTable table, int lineNo) {
        if (quantity < table.size() - 1) {
            int nextParamID = quantity + 1;

            String message = String.format(
                "line %d: '%s' parameter not provided during '%s' call.",
                lineNo, table.get(nextParamID).getName(), table.getName(0));
            throw new SemanticException(message);
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

    private static void checkBoolExpr(int lineNo, String cmd, Type t) {
        if (t != BOOL_TYPE) {
            String message = String.format(
                "line %d: conditional expression in '%s' is '%s' instead of '%s'.\n",
                lineNo, cmd, t.toString(), BOOL_TYPE.toString());
                
            throw new SemanticException(message);
        }
    }

    private static AST createConversionNode(Type lt, Type rt, AST node, int lineNo) {
        if (lt == BOOL_TYPE && rt != BOOL_TYPE) typeError(lineNo, ":=", lt, rt);
        if (lt == STR_TYPE  && rt != STR_TYPE)  typeError(lineNo, ":=", lt, rt);
        if (lt == INT_TYPE  && rt != INT_TYPE)  typeError(lineNo, ":=", lt, rt);

        if (lt == REAL_TYPE) {
            if (rt == INT_TYPE) {
                node = Conversion.createConversionNode(INT2REAL, node);
            } else if (rt != REAL_TYPE) {
                typeError(lineNo, ":=", lt, rt);
            }
        }

        return node;
    }

    //
    private static AST checkAssign(int lineNo, AST left, AST right) {
        Type lt = left.type;
        Type rt = right.type;

        right = createConversionNode(lt, rt, right, lineNo);

        return AST.newSubtree(ASSIGN_NODE, NO_TYPE, left, right);
    }

    // TODO: Adicionar char e string.
    @Override
    public AST visitExprIntegerVal(ExprIntegerValContext ctx) {
        int intData = Integer.parseInt(ctx.getText());
        return new AST(INT_VAL_NODE, intData, INT_TYPE);
    }

    //
    @Override
    public AST visitExprRealVal(ExprRealValContext ctx) {
        float floatData = Float.parseFloat(ctx.getText());
        return new AST(REAL_VAL_NODE, floatData, REAL_TYPE);
    }

    //
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

    //
    @Override
    public AST visitExprTrue(ExprTrueContext ctx) {
        return new AST(BOOL_VAL_NODE, 1, BOOL_TYPE);
    }

    //
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

    public AST visitArrayType(ArrayTypeContext ctx) {
        visit(ctx.componentType().type_());
        this.lastArrayDeclared = new Array(
            "",
            ctx.ARRAY().getSymbol().getLine(),
            ARRAY_TYPE,
            lastDeclaredType);

        for (IndexTypeContext typeContext : ctx.typeList().indexType()) {
            visit(typeContext);
            lastArrayDeclared.addRange(lastDeclaredRange);
        }
        
        this.lastDeclaredType = Type.ARRAY_TYPE;
        return null;
    }

    // Visita o subrange e salva como o último intervalo
    @Override
    public AST visitIndexType(IndexTypeContext ctx) {
        this.lastDeclaredRange = new Range(
            Integer.parseInt(ctx.simpleType().subrangeType().constant(0).getText()),
            Integer.parseInt(ctx.simpleType().subrangeType().constant(1).getText()));
        return null;
    }

    // Visita a regra de operadores aditivos
    @Override
    public AST visitSimpleExpression(SimpleExpressionContext ctx) {
        if (ctx.additiveoperator() != null) {
            AST left = visit(ctx.term());
            AST right = visit(ctx.simpleExpression());
            Unified unified = null;

            //
            switch (ctx.additiveoperator().operator.getType()) {
                case PascalParser.PLUS:
                    unified = left.type.unifyPlus(right.type);
                    break;
                case PascalParser.OR:
                    unified = left.type.unifyComp(right.type);
                    break;
                case PascalParser.MINUS:
                    unified = left.type.unifyOtherArith(right.type);
                    break;
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

            switch (ctx.additiveoperator().operator.getType()) {
                case PascalParser.PLUS:
                    return AST.newSubtree(PLUS_NODE, unified.type, left, right);
                case PascalParser.MINUS:
                    return AST.newSubtree(MINUS_NODE, unified.type, left, right);
                case PascalParser.OR:
                    return AST.newSubtree(OR_NODE, unified.type, left, right);
            }
        }

        return super.visitSimpleExpression(ctx);
    }

    // Visita a regra de operadores multiplicativos
    @Override
    public AST visitTerm(TermContext ctx) {
        if (ctx.multiplicativeoperator() != null) {
            AST left = visit(ctx.signedFactor());
            AST right = visit(ctx.term());
            Unified unified = null;

            //
            switch (ctx.multiplicativeoperator().operator.getType()) {
                case PascalParser.STAR:
                case PascalParser.SLASH:
                case PascalParser.DIV:
                    unified = left.type.unifyPlus(right.type);
                    break;
                case PascalParser.AND:
                    unified = left.type.unifyComp(right.type);
                    break;
            }

            //
            if (unified.type == NO_TYPE) {
                typeError(
                    ctx.multiplicativeoperator().operator.getLine(),
                    ctx.multiplicativeoperator().operator.getText(),
                    left.type, right.type);
            }

            left = Conversion.createConversionNode(unified.leftConversion, left);
            right = Conversion.createConversionNode(unified.rightConversion, right);
        
            switch (ctx.multiplicativeoperator().operator.getType()) {
                case PascalParser.STAR:
                    return AST.newSubtree(TIMES_NODE, unified.type, left, right); 
                case PascalParser.AND:
                    return AST.newSubtree(AND_NODE, unified.type, left, right); 
                case PascalParser.DIV:
                case PascalParser.SLASH:
                    return AST.newSubtree(OVER_NODE, unified.type, left, right); 
            }
        }

        return super.visitTerm(ctx);
    }

    //
    @Override
    public AST visitCompoundStatement(CompoundStatementContext ctx) {
        AST node = AST.newSubtree(BLOCK_NODE, NO_TYPE);

        for (StatementContext context : ctx.statements().statement()) {
            node.addChild(visit(context.unlabelledStatement()));
        }

        return node;
    }

    //
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

    //
    @Override
    public AST visitProcedureAndFunctionDeclarationPart(
        ProcedureAndFunctionDeclarationPartContext ctx) {
            return super.visit(
                ctx.procedureOrFunctionDeclaration()
                   .functionDeclaration());
        }

    //
    @Override
    public AST visitFunctionDeclaration(FunctionDeclarationContext ctx) {
        // Atualiza o tipo de retorno
        visit(ctx.resultType());
        Type functionType = lastDeclaredType;
        
        // Cria a função para inserir na tabela
        int idx = this.functionTable.put(new Function(
            ctx.identifier().getText(),
            ctx.identifier().IDENT().getSymbol().getLine(),
            functionType));
            
        //
        this.variableTable = this.functionTable.getVariableTable();

        // cria o nó da função
        AST funcNode = AST.ASTWithFunctionScope(FUNCTION_NODE, idx, functionType);
        AST varsSect = null;

        // Verifica se existe parâmetros
        if (ctx.formalParameterList() != null) {
            // Tem parâmetros, visita a ávore os buscando
            varsSect = visit(ctx.formalParameterList());
        } else {
            // Não tem parâmetros, apenas insere a variável da função
            varsSect = AST.newSubtree(VAR_LIST_NODE, NO_TYPE);
            idx = functionTable.addVarInLastFunction(functionTable.getName(), lastDeclaredType);
            varsSect.addChild(new AST(VAR_DECL_NODE, idx, lastDeclaredType));
        }

        AST stmtSect = visit(ctx.block().compoundStatement());
        funcNode.addChild(varsSect);
        funcNode.addChild(stmtSect);

        //
        this.variableTable = this.ProgramVariableTable;

       return funcNode;
    }

    //
    @Override
    public AST visitFormalParameterList(FormalParameterListContext ctx) {
        AST params = AST.newSubtree(VAR_LIST_NODE, NO_TYPE);

        //
        int idx = functionTable.addVarInLastFunction(functionTable.getName(), lastDeclaredType);
        params.addChild(new AST(VAR_DECL_NODE, idx, lastDeclaredType));

        for (FormalParameterSectionContext context : ctx.formalParameterSection()) {
            // Atualiza o lastDeclaredType
            visit(context.parameterGroup().typeIdentifier());

            // Salva cada declaracao de parâmetro
            IdentifierListContext list = context.parameterGroup().identifierList();

            for (IdentifierContext identifier : list.identifier()) {
                idx = functionTable.addVarInLastFunction(identifier.getText(), lastDeclaredType);
                params.addChild(new AST(VAR_DECL_NODE, idx, lastDeclaredType));
            }
        }

        return params;
    }

    //
    @Override
    public AST visitFactor(FactorContext ctx) {
        if (ctx.variable() != null) {
            return checkVariable(ctx.variable().identifier(0).IDENT().getSymbol());
        }

        if (ctx.LPAREN() != null) {
            return visitExpression(ctx.expression());
        }

        if (ctx.unsignedConstant() != null) {
            return visit(ctx.unsignedConstant());
        }

        return super.visitFactor(ctx);
    }

    @Override // IF expression THEN statement (: ELSE statement)?
    public AST visitIfStatement(IfStatementContext ctx) {
        // Analisa a expressão booleana.
        AST exprNode = visit(ctx.expression());
        AST thenNode = null;
        checkBoolExpr(ctx.IF().getSymbol().getLine(), "if", exprNode.type);

        if (ctx.ELSE() == null) {
            // Constrói o bloco de código do loop.

            if (ctx.statement(0).unlabelledStatement().structuredStatement() == null) {
                thenNode = AST.newSubtree(BLOCK_NODE, NO_TYPE);
                thenNode.addChild(visit(ctx.statement(0).unlabelledStatement()));
            } else {
                thenNode = visit(ctx.statement(0).unlabelledStatement());
            }

            return AST.newSubtree(IF_NODE, NO_TYPE, thenNode, exprNode);
        } 
        else {
            // Faz uma busca pelo token na lista de filhos.
            if (ctx.statement(0).unlabelledStatement().structuredStatement() == null) {
                thenNode = AST.newSubtree(BLOCK_NODE, NO_TYPE);
                thenNode.addChild(visit(ctx.statement(0).unlabelledStatement()));
            } else {
                thenNode = visit(ctx.statement(0).unlabelledStatement());
            }

            // Cria o nó com o bloco de comandos do ELSE.
            AST elseNode = null;
            
            if (ctx.statement(1).unlabelledStatement().structuredStatement() == null) {
                elseNode = AST.newSubtree(BLOCK_NODE, NO_TYPE);
                elseNode.addChild(visit(ctx.statement(1).unlabelledStatement()));
            } else {
                elseNode = visit(ctx.statement(1).unlabelledStatement());
            }

            return AST.newSubtree(IF_NODE, NO_TYPE, exprNode, thenNode, elseNode);
        }
    }

    //
    @Override
    public AST visitWhileStatement(WhileStatementContext ctx){
        // Analisa a expressão booleana.
        AST exprNode = visit(ctx.expression());
        AST blockNode = null;
        checkBoolExpr(ctx.WHILE().getSymbol().getLine(), "while", exprNode.type);

        // Constrói o bloco de código do loop.
        if (ctx.statement().unlabelledStatement().structuredStatement() == null) {
            blockNode = AST.newSubtree(BLOCK_NODE, NO_TYPE);
            blockNode.addChild(visit(ctx.statement().unlabelledStatement()));
        } else {
            blockNode = visit(ctx.statement().unlabelledStatement());
        }

        return AST.newSubtree(REPEAT_NODE, NO_TYPE, blockNode, exprNode);
    }

    //
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

    //
    public void printAST() {
        AST.printDot(root, variableTable, functionTable);
    }

    // Imprime as tabelas no sdout
    public void printTables() {
        System.out.println();
        System.out.println(stringTable);
        System.out.println(variableTable);
        System.out.println(functionTable);
    }
}