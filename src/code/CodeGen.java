package code;

import static code.Instruction.INSTR_MEM_SIZE;

import static typing.Type.INT_TYPE;
import static typing.Type.REAL_TYPE;

import java.io.BufferedWriter;
import java.io.IOException;

import ast.AST;
import ast.ASTBaseVisitor;
import tables.StringTable;
import tables.VariableTable;
import typing.Type;

public final class CodeGen extends ASTBaseVisitor<Void> {
    private final DataStack stack;
    private final Memory memory;
    private final StringTable st;
    private final VariableTable vt;
    private final BufferedWriter output;

    // Próxima posição na memória de código para emit.
    private static int nextInstr;
    private final Instruction code[]; // Code memory

    public CodeGen(StringTable st, VariableTable vt, BufferedWriter output) {
        this.code = new Instruction[INSTR_MEM_SIZE];
        this.stack = new DataStack();
        this.memory = new Memory(vt);
        this.st = st;
        this.vt = vt;
        this.output = output;
    }

    // Função principal para geração de código.
    @Override
    public void execute(AST root) throws IOException {
        nextInstr = 0;
        // dumpStrTable();
        visit(root);
        // emit(HALT);
        dumpProgram();
    }

    void dumpProgram() throws IOException {
        for (int addr = 0; addr < nextInstr; addr++) {
            this.output.write(String.format("%s\n", code[addr].toString()));
        }
        this.output.flush();
    }

    @Override
    protected Void visitProgram(AST node) {
        // VarList node
        // if (node.getChild(0).getChildCount() > 0) visit(node.getChild(0));
        // FunctionList node
        // if (node.getChild(1).getChildCount() > 0) visit(node.getChild(1));
        // Block node
        if (node.getChild(2).getChildCount() > 0) visit(node.getChild(2)); 

        return null; 
    }

    @Override
    protected Void visitBlock(AST node) {
        for (int i = 0; i < node.getChildCount() - 1; i++) {
            visit(node.getChild(i));
        }
        return null;
    }

    @Override
    protected Void visitVarUse(AST node) {
        System.out.println("VAR USE");
        // System.out.println(node);

        return null;
    }

    @Override
    protected Void visitTimes(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        if (node.type == INT_TYPE) {
            int r = stack.popi();
            int l = stack.popi();
            stack.pushi(l * r);
        } else { 
            float r = stack.popf();
            float l = stack.popf();
            stack.pushf(l * r);
        }
        return null;
    }

    @Override
    protected Void visitMinus(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        if (node.type == INT_TYPE) {
            int r = stack.popi();
            int l = stack.popi();
            stack.pushi(l - r);
        } else { 
            float r = stack.popf();
            float l = stack.popf();
            stack.pushf(l - r);
        }
        return null; 
    }

    @Override
    protected Void visitOver(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        if (node.type == INT_TYPE) {
            int r = stack.popi();
            int l = stack.popi();
            stack.pushi(l / r);
        } else {
            float r = stack.popf();
            float l = stack.popf();
            stack.pushf(l / r);
        }
        return null;
    }

    @Override
    protected Void visitPlus(AST node) {
        switch(node.type) {
            case INT_TYPE:  plusInt(node);    break;
            case REAL_TYPE: plusReal(node);   break;
            // case STR_TYPE:  plusStr(node);    break;
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
        int r = stack.popi();
        int l = stack.popi();
        stack.pushi(l + r);
        return null; 
    }

    private Void plusReal(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        float r = stack.popf();
        float l = stack.popf();
        stack.pushf(l + r);
        return null; 
    }

    // TODO Verificar utilização da tabela
    // private Void plusStr(AST node) {
    // 	visit(node.getChild(0));
    // 	visit(node.getChild(1));
    //     int r = stack.popi();
    //     int l = stack.popi();
    //     String ls = st.get(l).getName();
    //     String rs = st.get(r).getName();
    //     StringBuilder sb = new StringBuilder();

    //     sb.append(ls.substring(0, ls.length() - 1));
    //     sb.append(rs.substring(1));

    //     int newStrIdx = st.addStr(sb.toString());
    //     stack.pushi(newStrIdx);
    //     return null;
    // }

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
        visit(node.getChild(0));
        return null;
    }

    @Override
    protected Void visitFunction(AST node) {
        System.out.println("FUNCTION");
        visit(node.getChild(0));
        return null;
    }

    @Override
    protected Void visitFuncUse(AST node) {
        System.out.println("FUNC USE");

        // Verifica se não é uma função sem parâmetros
        if (node.getChildCount() > 0)
            visit(node.getChild(0));

        return null;
    }

    @Override
    protected Void visitRealVal(AST node) {
        stack.pushf(node.floatData);
        return null; 
    }

    @Override
    protected Void visitIntVal(AST node) {
        // stack.pushi(node.intData);
        System.out.println("INTVAL");
        emit(OpCode.ldc, node.intData);
        return null; 
    }

    @Override
    protected Void visitBoolVal(AST node) {
        stack.pushi(node.intData);
        return null; 
    }

    @Override
    protected Void visitStrVal(AST node) {
        stack.pushi(node.intData);
        return null;
    }

    @Override
    protected Void visitAssign(AST node) {
        System.out.println("ASSIGN");
        AST rexpr = node.getChild(1);
        visit(rexpr);

        int varIdx = node.getChild(0).intData;
        Type varType = vt.getType(varIdx);
        
        if (varType == INT_TYPE) {
            emit(OpCode.istore, varIdx);
        }

        return null;
    }

    @Override
    protected Void visitRepeat(AST node) {
        int again = 1;
        while (again == 1) {
            visit(node.getChild(0)); 
            visit(node.getChild(1)); 
            again = (stack.popi() == 0? 1 : 0); 
        }
        return null;
    }

    @Override
    protected Void visitINT2REAL(AST node) {
        visit(node.getChild(0));
        stack.pushf((float) stack.popi());
        return null;
    }

    // Emits
    
    private void emit(OpCode op, int o1, int o2, int o3) {
        Instruction instr = new Instruction(op, o1, o2, o3);
        code[nextInstr] = instr;
        nextInstr++;
    }
    
    private void emit(OpCode op) {
        emit(op, 0, 0, 0);
    }
    
    private void emit(OpCode op, int o1) {
        emit(op, o1, 0, 0);
    }
    
    private void emit(OpCode op, int o1, int o2) {
        emit(op, o1, o2, 0);
    }

    private void backpatchJump(int instrAddr, int jumpAddr) {
        code[instrAddr].o1 = jumpAddr;
    }

    private void backpatchBranch(int instrAddr, int offset) {
        code[instrAddr].o2 = offset;
    }
}
