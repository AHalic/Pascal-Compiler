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
	protected Void visitINT2REAL(AST node) {
		visit(node.getChild(0));
		stack.pushf((float) stack.popi());
		return null;
	}
}
