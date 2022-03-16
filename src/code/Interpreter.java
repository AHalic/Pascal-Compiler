package code;

import static typing.Type.INT_TYPE;
import static typing.Type.REAL_TYPE;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Scanner;

import ast.AST;
import ast.ASTBaseVisitor;
import tables.StringTable;
import tables.VariableTable;
import typing.Type;

public final class Interpreter extends ASTBaseVisitor<Void> {
    private final DataStack stack;
    private final Memory memory;
    private final StringTable st;
    private final VariableTable vt;
    private final Scanner in;

    public Interpreter(StringTable st, VariableTable vt) {
        this.stack = new DataStack();
        this.memory = new Memory(vt);
        this.st = st;
        this.vt = vt;
        this.in = new Scanner(System.in);
    }

    @Override
    protected Void visitProgram(AST node) {
        // deixar igual do block?
        visit(node.getChild(0));

        if (node.getChildCount() > 1) visit(node.getChild(1));
        if (node.getChildCount() > 2) visit(node.getChild(2)); 
        in.close(); 

        return null; 
    }

    @Override
    protected Void visitBlock(AST node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
        }
        return null;
    }

    @Override
    protected Void visitVarUse(AST node) {
        int varIdx = node.intData;
        if (node.type == REAL_TYPE) {
            stack.pushf(memory.loadf(varIdx));
        } else {
            stack.pushi(memory.loadi(varIdx));
        }
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
        return null; 
    }

    @Override
    protected Void visitVarList(AST node) {
        return null; 
    }

    @Override
    protected Void visitFuncList(AST node) {
        return null;
    }

    @Override
    protected Void visitRealVal(AST node) {
        stack.pushf(node.floatData);
        return null; 
    }

    @Override
    protected Void visitIntVal(AST node) {
        stack.pushi(node.intData);
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
        AST rexpr = node.getChild(1);
        visit(rexpr);

        int varIdx = node.getChild(0).intData;
        Type varType = vt.getType(varIdx);
        if (varType == REAL_TYPE) {
            memory.storef(varIdx, stack.popf());
        } else {
            memory.storei(varIdx, stack.popi());
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
}
