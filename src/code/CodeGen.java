package code;

import static code.Instruction.INSTR_MEM_SIZE;

import static typing.Type.INT_TYPE;
import static typing.Type.REAL_TYPE;

import java.io.BufferedWriter;
import java.io.IOException;

import ast.AST;
import ast.ASTBaseVisitor;
import ast.NodeKind;
import checker.Scope;
import tables.StringTable;
import tables.VariableTable;
import tables.Function;
import tables.FunctionTable;
import typing.Type;

public final class CodeGen extends ASTBaseVisitor<Void> {
    private final DataStack stack;
    private final Memory memory;
    private final StringTable st;
    private final VariableTable vt;
    private final FunctionTable ft;
    private final BufferedWriter output;
    private Scope scope;

    private VariableTable currentVars;
    private StringTable currentStrings;

    // Próxima posição na memória de código para emit.
    private static int nextInstr;
    private final Instruction code[]; // Code memory

    public CodeGen(
        StringTable st, VariableTable vt,
        FunctionTable ft, BufferedWriter output
    ) {
        this.code = new Instruction[INSTR_MEM_SIZE];
        this.stack = new DataStack();
        this.memory = new Memory(vt);
        this.st = st;
        this.vt = vt;
        this.ft = ft;
        this.output = output;
        this.currentVars = this.vt;
        this.currentStrings = this.st;
        this.scope = Scope.PROGRAM;
    }

    // Função principal para geração de código.
    @Override
    public void execute(AST root) throws IOException {
        nextInstr = 0;
        visit(root);
        // emit(HALT);
        dumpProgram();
    }

    void dumpStrTable() {
        // Emite um espaço para melhor legibilidade
        emit(Structure.space);
        emit(Structure.comment, "   ; STR TABLE");

        int start = 0;
        
        // Começa em 1 porque o 0 é o nome do programa
        if (scope == Scope.PROGRAM) start += 1;

        for (int i = start; i < currentStrings.size(); i++) {
            emit(OpCode.ldc, currentStrings.get(i).getName());
            emit(OpCode.astore, Integer.toString(i - start));
        }

        // Emite um espaço para melhor legibilidade
        emit(Structure.comment, "   ; END STR TABLE");
        emit(Structure.space);
    }

    void dumpProgram() throws IOException {
        int addrCounter = 0;
        int diff;

        for (int addr = 0; addr < nextInstr; addr++) {
            System.out.println("adrr e count: " + addr + "  " + addrCounter);
            diff = addr - addrCounter;
            this.output.write(String.format("%s\n", code[addr].getString(addrCounter, diff)));

            // Zera a contagem das labels quando entrar em uma nova função
            if (code[addr] instanceof StructureInstruction) {
                if (((StructureInstruction)code[addr]).op.name.equals(".method"))
                    addrCounter = 0;
            } else {
                addrCounter++;
            }
        }

        this.output.flush();
    }

    @Override
    protected Void visitProgram(AST node) {
        // Emite as instruções de declaração de programa
        emit(Structure.programDeclaration, st.getName(0));
        emit(Structure.objectInheritance);
        emit(Structure.space);

        // VarList node
        if (node.getChild(0).getChildCount() > 0) visit(node.getChild(0));

        // FunctionList node
        if (node.getChild(1).getChildCount() > 0) visit(node.getChild(1));

        // Emite declaração da main e o tamanho da stack e locals
        emit(Structure.methodDeclaration, "main([Ljava/lang/String;)V");
        emit(Structure.limit, "stack", "65535");
        emit(Structure.limit, "locals", Integer.toString(this.vt.size() + this.st.size()));
        dumpStrTable();

        // Emite os códigos do block node
        if (node.getChild(2).getChildCount() > 0) visit(node.getChild(2));

        // Emite o códigos de return e fim do método da main
        emit(OpCode.returnNULL);
        emit(Structure.endMethod); 

        return null; 
    }

    @Override
    protected Void visitBlock(AST node) {

        for (int i = 0; i < node.getChildCount(); i++) {
            if (node.getChild(i) != null) visit(node.getChild(i));
        }
        return null;
    }

    @Override
    protected Void visitVarUse(AST node) {
        System.out.println("VAR USE");

        int varIdx = node.intData + st.size();

        //
        if (scope == Scope.PROGRAM) varIdx--;

        //
        if (node.type == INT_TYPE || node.type == Type.BOOL_TYPE) {
            emit(OpCode.iload, Integer.toString(varIdx));
        } else if (node.type == REAL_TYPE) {
            emit(OpCode.fload, Integer.toString(varIdx));
        } else if (node.type == Type.STR_TYPE) {
            emit(OpCode.aload, Integer.toString(varIdx));
        } else {
            System.out.println("FALTA IMPLEMENTAR AQUI!!!!");
        }

        return null;
    }

    @Override
    protected Void visitTimes(AST node) {
        System.out.println("TIMES");
        visit(node.getChild(0));
        visit(node.getChild(1));

        switch (node.type) {
            case INT_TYPE:
                emit(OpCode.imul);
                break;
            case REAL_TYPE:
                emit(OpCode.fmul);
                break;
            default:
                System.out.println("Não implementado!");
                break;
        }
        return null;
    }

    @Override
    protected Void visitMinus(AST node) {
        System.out.println("MINUS");
        visit(node.getChild(0));
        visit(node.getChild(1));
        
        switch (node.type) {
            case INT_TYPE:
                emit(OpCode.isub);
                break;
            case REAL_TYPE:
                emit(OpCode.fsub);
                break;
            default:
                System.out.println("Não implementado!");
                break;
        }

        return null; 
    }

    @Override
    protected Void visitOver(AST node) {
        System.out.println("OVER");
        visit(node.getChild(0));
        visit(node.getChild(1));

        switch (node.type) {
            case INT_TYPE:
                emit(OpCode.idiv);
                break;
            case REAL_TYPE:
                emit(OpCode.fdiv);
                break;
            default:
                System.out.println("Não implementado!");
                break;
        }
        

        return null;
    }

    @Override
    protected Void visitPlus(AST node) {
        switch(node.type) {
            case INT_TYPE:  plusInt(node);    break;
            case REAL_TYPE: plusReal(node);   break;
            case STR_TYPE:  plusStr(node);    break;
            // Acho que isso era para o interpretador, no nosso caso acho
            // que pode remover, mas por enquanto resolvi deixei
            case NO_TYPE:
            default:
                System.err.printf("Invalid type: %s!\n",node.type.toString());
                System.exit(1);
        }
        return null; 
    }

    private Void plusInt(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        emit(OpCode.iadd);
        System.out.println("PLUS INT");

        return null; 
    }

    private Void plusReal(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        emit(OpCode.fadd);
        System.out.println("PLUS REAL");

        return null; 
    }

    private Void plusStr(AST node) {
        // Emite a criação de um stringBuilder
        emit(OpCode.create, "java/lang/StringBuilder");
        emit(OpCode.dup);
        emit(OpCode.invokespecial, "java/lang/StringBuilder.<init>()V");
        visit(node.getChild(0));
        emit(OpCode.invokevirtual, "java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        
        // Se ao lado direito for outra concatenação, percorre por questão de eficiência
        AST plusNode = node.getChild(1);

        while (plusNode.kind == NodeKind.PLUS_NODE) {
            visit(plusNode.getChild(0));
            emit(OpCode.invokevirtual, "java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder;");

            if (plusNode.getChild(1).getChildCount() > 0) {
                plusNode = plusNode.getChild(1);
            } else {
                break;
            }
        }

        // Emite o valor do lado direito
        visit(plusNode.getChild(1));
        emit(OpCode.invokevirtual, "java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        
        // Transforma o valor do builder em string e joga na pilha
        emit(OpCode.invokevirtual, "java/lang/StringBuilder.toString()Ljava/lang/String;");

        return null;
    }

    @Override
    protected Void visitVarDecl(AST node) {
        System.out.println("VAR_DECL");
        return null; 
    }

    @Override
    protected Void visitVarList(AST node) {
        System.out.println("VAR_LIST");
        visit(node.getChild(0));
        return null; 
    }

    @Override
    protected Void visitFuncList(AST node) {
        System.out.println("FUNC LIST");

        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
        }

        return null;
    }

    static String getStringType(Type type) {
        switch (type) {
            case INT_TYPE:
                return "I";
            case REAL_TYPE:
                return "F";
            case STR_TYPE:
                return "Ljava/lang/String;";
            case BOOL_TYPE:
                return "Z";
            default:
                return null;
        }
    }

    protected String getFunctionDeclaration(Function function) {
        // Type type = this.ft.getType(idx);
        String declaration = function.getName() + "(";
        VariableTable fvt = function.getVariableTable();

        // Primeira posição é a própria função
        for (int i = 1; i < fvt.size() - 1; i++) {
            declaration += getStringType(fvt.getType(i));
        }
        
        declaration += ")" + getStringType(function.getType());

        return declaration;
    }

    protected void emitFunctionReturnType(Function function) {
        int returnPos = function.getStringTable().size();

        switch (function.getType()) {
            case INT_TYPE:
                emit(OpCode.iload, Integer.toString(returnPos));
                emit(OpCode.returnINT);
                break;
            case REAL_TYPE:
                emit(OpCode.fload, Integer.toString(returnPos));
                emit(OpCode.returnFLOAT);
                break;
            case STR_TYPE:
                emit(OpCode.aload, Integer.toString(returnPos));
                emit(OpCode.returnREFERENCE);
                break;
            default:
                emit(OpCode.returnNULL);
                break;
        }
    }

    @Override
    protected Void visitFunction(AST node) {
        System.out.println("FUNCTION");
        // Pegamos o envelope da função e pegamos a primeira declaração,
        // apenas funçõs built-in terão mais de uma definição.
        Function function = this.ft.get(node.intData).getFunctions().get(0);
        String declaration = getFunctionDeclaration(function);

        scope = Scope.FUNCTION;
        VariableTable lastVars = currentVars;
        StringTable lastStrs = currentStrings;
        currentVars = function.getVariableTable();
        currentStrings = function.getStringTable();

        // TODO: Colocar a tabela de strings separadas por função
        int localsNumber = currentVars.size() + function.getStringTable().size() + 1;
        emit(Structure.methodDeclaration, declaration);
        emit(Structure.limit, "stack", "65535");
        emit(Structure.limit, "locals", Integer.toString(localsNumber));

        //
        dumpStrTable();

        //
        visit(node.getChild(0));
        visit(node.getChild(1));

        //
        emitFunctionReturnType(function);

        emit(Structure.endMethod);
        emit(Structure.space);

        //
        currentVars = lastVars;
        currentStrings = lastStrs;
        scope = Scope.PROGRAM;

        return null;
    }

    protected Boolean emitBuiltInFunction(AST node) {
        Function builtin = null;

        // Busca a função built-in com os parâmetros correto
        for (var function: ft.get(node.intData).getFunctions()) {
            VariableTable vt = function.getVariableTable();
            Boolean finded = true;

            for (int i = 1; i < vt.size(); i++) {
                if (vt.getType(i) != node.getChild(i - 1).type) {
                    finded = false;
                    break;
                }
            }

            if (finded) {
                builtin = function;
                break;
            }
        }

        // Emite a função
        switch (builtin.getName()) {
            case "read":
                System.out.println("BUILT-IN READ");
                emitRead(node);
                break;
            case "write":
                System.out.println("BUILT-IN WRITE");
                emitWrite(node);
                break;
        }

        return true;
    }

    @Override
    protected Void visitFuncUse(AST node) {
        System.out.println("FUNC USE");

        /* Caso seja uma função built-in irá retornar false, caso contrário
           o código da função é emitido */
        if (!emitBuiltInFunction(node)) {
            // Verifica se não é uma função sem parâmetros
            if (node.getChildCount() > 0) {
                // Caso tenha parâmetros temos que empilha-los na pilha
                for (int i = 0; i < node.getChildCount(); i++) {
                    visit(node.getChild(i));
                }
            }
    
            // Emite a chamada da função
            String program = st.getName(0);
            String header = getFunctionDeclaration(ft.get(node.intData).getFunctions().get(0));
            emit(OpCode.invokestatic, String.format("%s.%s", program, header));
        }


        return null;
    }

    @Override
    protected Void visitRealVal(AST node) {
        emit(OpCode.ldc, Float.toString(node.floatData));
        
        return null; 
    }

    @Override
    protected Void visitIntVal(AST node) {
        System.out.println("INTVAL");
        emit(OpCode.ldc, Integer.toString(node.intData));
        return null; 
    }

    @Override
    protected Void visitBoolVal(AST node) {
        System.out.println("BOOLVAL");
        emit(OpCode.ldc, Integer.toString(node.intData));
        return null; 
    }

    @Override
    protected Void visitStrVal(AST node) {
        // Pega a referência, como é uma string devemos subtrair 1,
        // pois o nome do código é registrado como a string 0.
        int varIdx = node.intData;

        // Se o escopo for do programa principal subtrai-se 1,
        // devido ao nome do programa ser a string 0.
        if (scope == Scope.PROGRAM) varIdx--;

        emit(OpCode.aload, Integer.toString(varIdx));
        return null;
    }

    @Override
    protected Void visitAssign(AST node) {
        System.out.println("ASSIGN");
        AST rexpr = node.getChild(1);
        visit(rexpr);

        int varIdx = node.getChild(0).intData + currentStrings.size();

        if (scope == Scope.PROGRAM) varIdx--;

        Type varType = currentVars.getType(node.getChild(0).intData);

        emitStore(varType, varIdx);

        return null;
    }

    protected Void emitStore(Type varType, int varIdx) {
        if (varType == INT_TYPE || varType == Type.BOOL_TYPE) {
            emit(OpCode.istore, Integer.toString(varIdx));
        } else if (varType == REAL_TYPE) {
            emit(OpCode.fstore, Integer.toString(varIdx));
        } else if (varType == Type.STR_TYPE) {
            emit(OpCode.astore, Integer.toString(varIdx));
        }

        return null;
    }

    @Override
    protected Void visitRepeat(AST node) {
        int first_addr = nextInstr;
        visit(node.getChild(0));
        int eq_addr = nextInstr;
        emit(OpCode.ifeq, "");
        
        // visita as instruções
        visit(node.getChild(1));
        
        emit(OpCode.gotoProgram, "");
        
        // modifica o label do operador goto
        this.code[nextInstr-1].o1 = Integer.toString(first_addr);
        // modifica o label do operador ifeq
        this.code[eq_addr].o1 = Integer.toString(nextInstr);
        
        return null;
    }

    @Override
    protected Void visitLt(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));

        emit(OpCode.if_icmpge, Integer.toString(nextInstr + 3));
        emit(OpCode.ldc, "1");
        emit(OpCode.gotoProgram, Integer.toString(nextInstr + 2));
        emit(OpCode.ldc, "0");
        
        return null;
    }

    @Override
    protected Void visitLe(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));

        emit(OpCode.if_icmpgt, Integer.toString(nextInstr + 3));
        emit(OpCode.ldc, "1");
        emit(OpCode.gotoProgram, Integer.toString(nextInstr + 2));
        emit(OpCode.ldc, "0");
        
        return null;
    }

    @Override
    protected Void visitGt(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));

        emit(OpCode.if_icmple, Integer.toString(nextInstr + 3));
        emit(OpCode.ldc, "1");
        emit(OpCode.gotoProgram, Integer.toString(nextInstr + 2));
        emit(OpCode.ldc, "0");
        
        return null;
    }

    @Override
    protected Void visitGe(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));

        emit(OpCode.if_icmplt, Integer.toString(nextInstr + 3));
        emit(OpCode.ldc, "1");
        emit(OpCode.gotoProgram, Integer.toString(nextInstr + 2));
        emit(OpCode.ldc, "0");
        
        return null;
    }

    @Override
    protected Void visitEq(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));

        emit(OpCode.if_icmpne, Integer.toString(nextInstr + 3));
        emit(OpCode.ldc, "1");
        emit(OpCode.gotoProgram, Integer.toString(nextInstr + 2));
        emit(OpCode.ldc, "0");

        return null;
    }

    @Override
    protected Void visitNotEqual(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));

        emit(OpCode.if_icmpeq, Integer.toString(nextInstr + 3));
        emit(OpCode.ldc, "1");
        emit(OpCode.gotoProgram, Integer.toString(nextInstr + 2));
        emit(OpCode.ldc, "0");

        return null;
    }

    @Override
    protected Void visitAnd(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));

        emit(OpCode.iand);

        return null;
    }

    @Override
    protected Void visitOr(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));

        emit(OpCode.ior);

        return null;
    }

    @Override
    protected Void visitNot(AST node) {
        visit(node.getChild(0));

        emit(OpCode.ineg);

        return null;
    }

    @Override
    protected Void visitIf(AST node) {
        visit(node.getChild(0));
        int last_addr = nextInstr;
        emit(OpCode.ifeq, "");
        
        int childs = node.getChildCount();

        if (childs >= 2) { // if com instrucoes
            visit(node.getChild(1));
        }
        
        this.code[last_addr].o1 = Integer.toString(nextInstr + 1);

        if (childs == 3) { // if - else
            int last_addr_else = nextInstr;
            emit(OpCode.gotoProgram, "");
            visit(node.getChild(2));
            this.code[last_addr_else].o1 = Integer.toString(nextInstr);
        }

        return null;
    }


    @Override
    protected Void visitINT2REAL(AST node) {
        visit(node.getChild(0));
        emit(OpCode.i2f);
        return null;
    }

    // Emits

    // Emite uma estrutura do bytecode
    private void emit(Structure struct, String o1, String o2, String o3) {
        StructureInstruction instr = new StructureInstruction(struct, o1, o2, o3);
        code[nextInstr] = instr;
        nextInstr++;
    }

    private void emit(Structure op) {
        emit(op, "", "", "");
    }
    
    private void emit(Structure op, String o1) {
        emit(op, o1, "", "");
    }
    
    private void emit(Structure op, String o1, String o2) {
        emit(op, o1, o2, "");
    }

    private void emit(OpCode op, String o1, String o2, String o3) {
        Instruction instr = new OpInstruction(op, o1, o2, o3);
        code[nextInstr] = instr;
        nextInstr++;
    }

    private void emit(OpCode op) {
        emit(op, null, null, null);
    }
    
    private void emit(OpCode op, String o1) {
        emit(op, o1, null, null);
    }
    
    private void emit(OpCode op, String o1, String o2) {
        emit(op, o1, o2, null);
    }

    // Built-In functions
    private void emitRead(AST node) {
        Type type = node.getChild(0).type;
        String typeName = "";
        int varIdx = node.getChild(0).intData + currentStrings.size() - 1;

        // Verifica o pos-append no nome do método do scanner
        if (type == INT_TYPE) {
            typeName = "Int";
        } else if (type == REAL_TYPE) {
            typeName = "Float";
        } else if (type == Type.STR_TYPE) {
            typeName = "Line";
        } else if (type == Type.BOOL_TYPE) {
            typeName = "Boolean";
        }

        // Cria um objeto de scanner novo
        emit(OpCode.create, "java/util/Scanner");
        emit(OpCode.dup);
        emit(OpCode.getstatic, "java/lang/System/in", "Ljava/io/InputStream;");
        emit(OpCode.invokespecial, "java/util/Scanner/<init>(Ljava/io/InputStream;)V");

        // Emite a chamada do scanner
        String callString = String.format(
            "java/util/Scanner/next%s()%s",
            typeName,
            getStringType(type));
        
        emit(OpCode.invokevirtual, callString);

        emitStore(type, varIdx);
    }

    private void emitWrite(AST node) {
        Type type = node.getChild(0).type;
        int varIdx = node.getChild(0).intData + currentStrings.size() - 1;

        emit(OpCode.getstatic, "java/lang/System/out", "Ljava/io/PrintStream;");
        visit(node.getChild(0));

        //
        String callString = String.format(
            "java/io/PrintStream/print(%s)V",
            getStringType(node.getChild(0).type));
        
        emit(OpCode.invokevirtual, callString);
    }

    // private void backpatchJump(int instrAddr, int jumpAddr) {
    //     ((OpInstruction)code[instrAddr]).o1 = jumpAddr;
    // }

    // private void backpatchBranch(int instrAddr, int offset) {
    //     ((OpInstruction)code[instrAddr]).o2 = offset;
    // }
}
