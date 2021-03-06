package checker;

import org.antlr.v4.runtime.Token;
import java.util.List;

import java.util.ArrayList;
import ast.AST;
import parser.PascalParser;
import parser.PascalParserBaseVisitor;
import tables.Function;
import tables.FunctionTable;
import tables.StringEntry;
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
import exception.TypeException;
import exception.NotImplementedException;
import static typing.Conversion.*;

public class SemanticChecker extends PascalParserBaseVisitor<AST> {
    //
    public final FunctionTable functionTable = new FunctionTable(true);

    //
    public final StringTable ProgramStringTable = new StringTable();
    public StringTable stringTable = ProgramStringTable;
    
    /* Cria a tabela de variáveis e o ponteiro para não ser preciso
       diferenciar se a tabela é da função ou do programa. */
    public final VariableTable ProgramVariableTable = new VariableTable();
    private VariableTable variableTable = ProgramVariableTable;

    //
    Type lastDeclaredType;
    Range lastDeclaredRange;
    Array lastDeclaredArray;
    Scope currentScope = Scope.PROGRAM;

    //
    AST root;

    // Cria um nó de utilização de variável caso já tenha sido declarada
    public AST checkVariable(Token token) {
        String text = token.getText().toLowerCase();
        int line = token.getLine();

        // Verifica se é uma chamada de função
        if (this.currentScope == Scope.PROGRAM && functionTable.contains(text)) {
            int idx = functionTable.getIndex(text);
            Function validFunction = null;

            for (var function: functionTable.get(text).getFunctions()) {
                if (function.getVariableTable().size() == 1) {
                    validFunction = function;
                    break;
                }
            }

            // Verifica se achou a função e pode retornar
            if (validFunction != null) {
                return new AST(FUNC_USE_NODE, idx, validFunction.getType());
            }

            // Não achou, verifica se foi informado menos parâmetros do que o necessário
            checkMissingParameters(0, functionTable.get(text).getOverloaded(0), line);
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

    // Retorna a AST construída ao final da análise.
    public AST getAST() {
        return this.root;
    }

    // Cria uma nova variável caso ainda não tenha sido declarada
    public AST newVariable(Token token) {
        String text = token.getText().toLowerCase();
        int line = token.getLine();

        // Verifica se existe uma função com mesmo nome
        checkExistsFunction(token);

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
            lastDeclaredArray.setName(text);
            int idx = variableTable.add(lastDeclaredArray);
            return new AST(VAR_DECL_NODE, idx, lastDeclaredType);
        } else {
            int idx = variableTable.add(text, line, lastDeclaredType);
            return new AST(VAR_DECL_NODE, idx, lastDeclaredType);
        }
    }

    // Visita a chamada de função
    @Override
    public AST visitProcedureStatement(ProcedureStatementContext ctx) {
        String name = ctx.identifier().IDENT().getSymbol().getText().toLowerCase();
        int idx = this.functionTable.getIndex(name);
        int line = ctx.identifier().IDENT().getSymbol().getLine();
        List<ActualParameterContext> list = new ArrayList<ActualParameterContext>();

        // Verifica se a função existe na tabela
        checkNotExistsFunction(ctx.identifier().IDENT().getSymbol());
        
        // Verifica se existe parâmetros
        if ((ctx.parameterList() != null) && 
            (ctx.parameterList().actualParameter() != null)) {
            list = ctx.parameterList().actualParameter();
        }
        
        // Árvore de chamada de função
        AST node = AST.newSubtree(
            FUNC_USE_NODE,
            functionTable.getType(idx),
            idx);
            
        /* Variáveis para buscar se existe alguma função válida que possui
           dentro da lista de funções de nome "name". Na prática, apenas
           funções built-in terão sobrecarga. */
        boolean validFunction = false;
        List<Function> functions = this.functionTable.getOverloadedFunctions(idx);
        List<Type> types = new ArrayList<>();
        Function function = functions.get(0);
        VariableTable table = function.getVariableTable();

        for (int j = 0; j < functions.size(); j++) {
            // Para se ambas não tiverem parâmetros
            if (list.size() == 0 && table.size() == 1) break;

            try {
                for (int i = 0; i < list.size(); i++) {
                        // Insere todos parâmetros no nó de chamada de função
                        AST param = visit(list.get(i));
                        param = createConversionNode(table.getType(i + 1), param.type, param, line);
                        types.add(param.type);
                        node.addChild(param);

                        //
                        if (i == list.size() - 1) validFunction = true;
                    }
                } catch (TypeException error) {
                    /* Função errada, limpa o nó de variáveis. Limpa apenas se tiver
                       mais uma para ler, para manter sempre o último parâmetro salvo. */
                    if (j + 1 != functions.size() - 1) {
                        node.clear();
                        types.clear();
                    } else {
                        throw error;
                    }
                }

                // Para, caso tenha encontrando a declaração correspondente
                if ((validFunction)) break;

                if (j + 1 < functions.size() - 1) {
                    function = functions.get(j + 1);
                    table = function.getVariableTable();
                }
            }

        // Verifica se os parâmetros estão certos
        checkMissingParameters(list.size(), function, line);
        checkExtraParameters(list, table, line);
        checkScope(name, line);

        return node;
    }

    // Verifica o escopo em que o break está sendo chamando
    private void checkScope(String name, int lineNo) {
        if (name.equals("break") && this.currentScope != Scope.WHILE) {
            throw new SemanticException(String.format(
                "line %d: '%s' token out of loop scope.", lineNo, name));
        }
    }

    // Visita o nó de criação de string
    @Override
    public AST visitExprStrVal(ExprStrValContext ctx) {
        int idx = stringTable.add(new StringEntry(
            ctx.string().getText(),
            ctx.string().STRING_LITERAL().getSymbol().getLine()));
        return new AST(STR_VAL_NODE, idx, STR_TYPE);
    }

    // Visita o nó de declaração de variável
    @Override
    public AST visitVariableDeclarationPart(VariableDeclarationPartContext ctx) {
        AST node = AST.newSubtree(VAR_LIST_NODE, NO_TYPE);
        
        for (var varList : ctx.variableDeclaration()) {
            List<IdentifierContext> idList = varList.identifierList().identifier();
            visit(varList.type_());

            for (var idContext : idList) {
                node.addChild(newVariable(idContext.IDENT().getSymbol()));
            }
        }

        return node;
    }

    // Visita o nó de de declaração de variável
    @Override
    public AST visitVariableDeclaration(VariableDeclarationContext ctx) {
        visit(ctx.type_());
        return newVariable(ctx.identifierList().identifier(0).IDENT().getSymbol());
    }

    //
    @Override
    public AST visitProgram(ProgramContext ctx) {
        AST varsSect = AST.newSubtree(VAR_LIST_NODE, NO_TYPE);
        AST funcSect = AST.newSubtree(FUNC_LIST_NODE, NO_TYPE);
        
        //
        stringTable.add(new StringEntry(
            ctx.programHeading().identifier().IDENT().getSymbol().getText(),
            ctx.programHeading().identifier().IDENT().getSymbol().getLine()));

        // Verifica se existe uma declaração de função
        for (var function : ctx.block().procedureAndFunctionDeclarationPart()) {
            if (function.procedureOrFunctionDeclaration() != null)
                funcSect.addChild(visit(function.procedureOrFunctionDeclaration()));
        }

        // Verifica se existe declaração de variáveis
        if (ctx.block().variableDeclarationPart().size() > 0) {
            varsSect = visit(ctx.block().variableDeclarationPart(0));
        }

        AST stmtSect = visit(ctx.block().compoundStatement());
        this.root = AST.newSubtree(PROGRAM_NODE, NO_TYPE, varsSect, funcSect, stmtSect);
        return this.root;
    }

    @Override
    public AST visitVariable(VariableContext ctx) {
        if (ctx.LBRACK(0) != null) {
            Array array = (Array)(variableTable.get(ctx.identifier(0).getText().toLowerCase()));
            AST node = AST.newSubtree(SUBSCRIPT_NODE, array.componentType);
            Token token = ctx.identifier(0).IDENT().getSymbol();

            // Adiciona o array como primeiro elemento do subscript
            node.addChild(
                new AST(
                    VAR_USE_NODE,
                    variableTable.getIndex(array.getName()),
                    ARRAY_TYPE)
                );
            
            // Adiciona os índices como elementos do subscript
            for (var range : ctx.expression()) {
                AST exprNode = visit(range);

                if (exprNode.type != INT_TYPE) {
                    String msg = String.format(
                        "line %d: array index type('%s') is incompatible, index must be an integer.",
                        token.getLine(), exprNode.type);
                    throw new TypeException(msg);
                }

                node.addChild(exprNode);
            }

            checkSubscriptDimension(ctx.expression().size(), array, token);
            
            return node;
        }

        return checkVariable(ctx.identifier(0).IDENT().getSymbol());
    }

    //
    private void checkSubscriptDimension(int subscriptDim, Array array, Token token) {
        if (subscriptDim != array.getDimensionSize()) {
            String message = String.format(
                "line %d: inconsistent number of indice(s) for array '%s'," +
                " informed %d indice(s) being necessary %d.",
                token.getLine(), token.getText().toLowerCase(), subscriptDim, array.getDimensionSize());
            throw new SemanticException(message);
        }
    }

    //
    @Override
    public AST visitAssignmentStatement(AssignmentStatementContext ctx) {
        int line = ctx.variable().identifier(0).IDENT().getSymbol().getLine();
        AST expression  = visit(ctx.expression());
        AST variable = visit(ctx.variable());
        return checkAssign(line, variable, expression);
    }

    public AST visitFunctionDesignator(FunctionDesignatorContext ctx) {
        // Verifica se a função foi declarada para evitar acessos inválidos
        checkNotExistsFunction(ctx.identifier().IDENT().getSymbol());

        String name = ctx.identifier().getText().toLowerCase();
        int line = ctx.identifier().IDENT().getSymbol().getLine();
        int idx = this.functionTable.getIndex(name);
        VariableTable table = functionTable.getVariableTable(idx);
        List<ActualParameterContext> list = ctx.parameterList().actualParameter();
        
        // Árvore de chamada de função
        AST node = AST.newSubtree(
            FUNC_USE_NODE,
            functionTable.getOverloadedFunctions(idx).get(0).getType(),
            idx);
            
        // Verifica se os parâmetros estão certos
        checkMissingParameters(list.size(), functionTable.get(name).getOverloaded(0), line);
        checkExtraParameters(list, table, line);

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
        List<ActualParameterContext> list,
        VariableTable table, int lineNo) {
        //
        if (list.size() > table.size() - 1) {
            int nextParamID = list.size() - 1;

            String message = String.format(
                "line %d: '%s' parameter value not referenced in " +
                "'%s' function declaration.",
                lineNo, list.get(nextParamID).getText().toLowerCase(), table.getName(0));
            throw new SemanticException(message);
        }
    }

    // Verifica se esta faltando parâmetros
    void checkMissingParameters(int quantity, Function function, int lineNo) {
        if (quantity < function.getParameterQuantity()) {
            VariableTable table = function.getVariableTable();
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
        throw new TypeException(message);
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
    private AST checkAssign(int lineNo, AST left, AST right) {
        Type lt = left.type;
        Type rt = right.type;

        //
        if (left.kind == FUNC_USE_NODE) {
            String message = String.format(
                "line %d: the identifier '%s' corresponds to a function," +
                " so it is not possible to assign values.",
                lineNo, this.functionTable.getName(left.intData));

            throw new SemanticException(message);
        }

        right = createConversionNode(lt, rt, right, lineNo);

        return AST.newSubtree(ASSIGN_NODE, NO_TYPE, left, right);
    }

    // TODO: Adicionar char e string.
    @Override
    public AST visitExprIntegerVal(ExprIntegerValContext ctx) {
        int intData = Integer.parseInt(ctx.getText().toLowerCase());
        return new AST(INT_VAL_NODE, intData, INT_TYPE);
    }

    //
    @Override
    public AST visitExprRealVal(ExprRealValContext ctx) {
        float floatData = Float.parseFloat(ctx.getText().toLowerCase());
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
        this.lastDeclaredArray = new Array(
            "",
            ctx.ARRAY().getSymbol().getLine(),
            ARRAY_TYPE,
            lastDeclaredType);

        for (var typeContext : ctx.typeList().indexType()) {
            visit(typeContext);
            checkArrayLimits(ctx.ARRAY().getSymbol().getLine());
            lastDeclaredArray.addRange(lastDeclaredRange);
        }
        
        this.lastDeclaredType = Type.ARRAY_TYPE;
        return null;
    }

    // Verifica se os limites do Array são válidos
    private void checkArrayLimits(int line) {
        if (lastDeclaredRange.upperLimit < lastDeclaredRange.lowerLimit) {
            String message = String.format(
                "line %s: High range limit(%d) < low range limit(%d).",
                line, lastDeclaredRange.upperLimit, lastDeclaredRange.lowerLimit);

            throw new SemanticException(message);
        }
    }

    // Visita o subrange e salva como o último intervalo
    @Override
    public AST visitIndexType(IndexTypeContext ctx) {
        this.lastDeclaredRange = new Range(
            Integer.parseInt(ctx.simpleType().subrangeType().constant(0).getText().toLowerCase()),
            Integer.parseInt(ctx.simpleType().subrangeType().constant(1).getText().toLowerCase()));
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
                    ctx.additiveoperator().operator.getText().toLowerCase(),
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

        return visitTerm(ctx.term());
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
                    ctx.multiplicativeoperator().operator.getText().toLowerCase(),
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

        return visitSignedFactor(ctx.signedFactor());
    }

    //
    @Override
    public AST visitCompoundStatement(CompoundStatementContext ctx) {
        AST node = AST.newSubtree(BLOCK_NODE, NO_TYPE);

        for (var context : ctx.statements().statement()) {
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
                    ctx.relationaloperator().operator.getText().toLowerCase(),
                    leftType, rightType);
            }

            left = Conversion.createConversionNode(unified.leftConversion, left);
            right = Conversion.createConversionNode(unified.rightConversion, right);

            return relationalNode(
                ctx.relationaloperator().operator,
                unified, left, right);
        }

        return visit(ctx.simpleExpression());
    }

    //
    @Override
    public AST visitFunctionDeclaration(FunctionDeclarationContext ctx) {
        // Atualiza o tipo de retorno
        visit(ctx.resultType());
        Type functionType = lastDeclaredType;
        
        checkExistsFunction(ctx.identifier().IDENT().getSymbol());

        // Cria a função para inserir na tabela
        int idx = this.functionTable.add(new Function(
            ctx.identifier().getText().toLowerCase(),
            ctx.identifier().IDENT().getSymbol().getLine(),
            functionType));
            
        //
        Scope lastScope = this.currentScope;
        this.currentScope = Scope.FUNCTION;
        this.variableTable = this.functionTable.getLastVariableTable();
        this.stringTable = this.functionTable.getLastStringTable();

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
            idx = functionTable.addVarInLastFunction(functionTable.getLastName(), lastDeclaredType, false);
            varsSect.addChild(new AST(VAR_DECL_NODE, idx, lastDeclaredType));
        }
        
        // Adiciona as variáveis definidas na função
        if (ctx.block().variableDeclarationPart(0) != null) {
            List<VariableDeclarationContext> list =
                ctx.block().variableDeclarationPart(0).variableDeclaration();
            
            for (var variable : list) {
                varsSect.addChild(visit(variable));
            }
        }
            
        AST stmtSect = visit(ctx.block().compoundStatement());
        funcNode.addChild(varsSect);
        funcNode.addChild(stmtSect);

        //
        this.variableTable = this.ProgramVariableTable;
        this.stringTable = this.ProgramStringTable;
        this.currentScope = lastScope;

       return funcNode;
    }

    // Verifica se existe uma função já declarada com o mesmo nome
    private void checkExistsFunction(Token token) {
        if (this.functionTable.contains(token.getText().toLowerCase())) {
            String message = String.format(
                "line %d: A function declaration already exists for '%s'.",
                token.getLine(), token.getText().toLowerCase());
            throw new SemanticException(message);
        }
    }

    //
    private void checkNotExistsFunction(Token token) {
        if (!this.functionTable.contains(token.getText().toLowerCase())) {
            String message = String.format(
                "line %d: undeclared function '%s'.",
                token.getLine(), token.getText().toLowerCase());
            throw new SemanticException(message);
        }
    }

    //
    @Override
    public AST visitFormalParameterList(FormalParameterListContext ctx) {
        AST params = AST.newSubtree(VAR_LIST_NODE, NO_TYPE);

        //
        int idx = functionTable.addVarInLastFunction(functionTable.getLastName(), lastDeclaredType, false);
        params.addChild(new AST(VAR_DECL_NODE, idx, lastDeclaredType));

        for (var context : ctx.formalParameterSection()) {
            // Atualiza o lastDeclaredType
            visit(context.parameterGroup().typeIdentifier());
            // Salva cada declaracao de parâmetro
            IdentifierListContext list = context.parameterGroup().identifierList();

            for (var identifier : list.identifier()) {
                idx = functionTable.addVarInLastFunction(identifier.getText().toLowerCase(), lastDeclaredType, true);
                params.addChild(new AST(VAR_DECL_NODE, idx, lastDeclaredType));
            }
        }

        return params;
    }

    //
    @Override
    public AST visitFactor(FactorContext ctx) {
        if (ctx.variable() != null) {
            return visit(ctx.variable());
        }
        
        if (ctx.LPAREN() != null) {
            return visit(ctx.expression());
        }

        if (ctx.functionDesignator() != null) {
            return visit(ctx.functionDesignator());
        }

        if (ctx.unsignedConstant() != null) {
            return visit(ctx.unsignedConstant());
        }
            
        if (ctx.set_() != null) {
            int line = ctx.set_().LBRACK().getSymbol().getLine();
            notImplemented("set", line);
        }
            
        if (ctx.NOT() != null) {
            visit(ctx.factor());
            return AST.newSubtree(NOT_NODE, lastDeclaredType, visit(ctx.factor()));
        }

        return super.visitFactor(ctx);
    }

    //
    private void notImplemented(String element, int line) {
        String message = String.format(
            "line %d: '%s' not yet implemented!",
            line, element);
        throw new NotImplementedException(message);
    }

    // Visita a declaração do bloco lógico IF-THEN-ELSE
    @Override
    public AST visitIfStatement(IfStatementContext ctx) {
        // Analisa a expressão booleana.
        AST exprNode = visit(ctx.expression());
        AST thenNode = createStatementBlockNode(ctx.statement(0));
        checkBoolExpr(ctx.IF().getSymbol().getLine(), "if", exprNode.type);

        // Constrói o bloco da condicional.
        if (ctx.ELSE() == null) {
            return AST.newSubtree(IF_NODE, NO_TYPE, exprNode, thenNode);
        } 
        else {
            AST elseNode = createStatementBlockNode(ctx.statement(1));
            return AST.newSubtree(IF_NODE, NO_TYPE, exprNode, thenNode, elseNode);
        }
    }

    //
    public AST createStatementBlockNode(StatementContext ctx) {
        UnlabelledStatementContext unlabelled = ctx.unlabelledStatement();
        if ((unlabelled.structuredStatement() != null) &&
            ((unlabelled.structuredStatement().compoundStatement() != null) ||
             (unlabelled.structuredStatement().conditionalStatement() != null))) {
            //
            return visit(unlabelled.structuredStatement());
        }

        AST blockNode = AST.newSubtree(BLOCK_NODE, NO_TYPE); 
        blockNode.addChild(visitUnlabelledStatement(unlabelled));
        return blockNode;
    }

    //
    @Override
    public AST visitWhileStatement(WhileStatementContext ctx) {
        // Analisa a expressão booleana.
        AST exprNode = visit(ctx.expression());
        checkBoolExpr(ctx.WHILE().getSymbol().getLine(), "while", exprNode.type);
        
        // Troca de escopo
        Scope lastScope = currentScope;
        currentScope = Scope.WHILE;
        
        // Constrói o bloco de código do loop e volta para o escopo anterior
        AST blockNode = createStatementBlockNode(ctx.statement());
        currentScope = lastScope;

        return AST.newSubtree(REPEAT_NODE, NO_TYPE, exprNode, blockNode);
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
                    operator.getLine(), operator.getText().toLowerCase());
                throw new SemanticException(message);
        }
    }

    //
    public void printAST() {
        AST.printDot(root, variableTable, functionTable, stringTable);
    }

    // Imprime as tabelas no sdout
    public void printTables() {
        System.out.println();
        System.out.println(stringTable);
        System.out.println(variableTable);
        System.out.println(functionTable);
    }
}