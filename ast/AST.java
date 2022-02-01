package ast;

import static typing.Type.NO_TYPE;

import java.util.ArrayList;
import java.util.List;

// import tables.VarTable;
import typing.Type;

public class AST {

	// public  final NodeKind kind;
	public  final int intData;
	public  final float floatData;
	public  final Type type;
	private final List<AST> children;

    private AST(/*NodeKind kind,*/ int intData, float floatData, Type type) {
		// this.kind = kind;
		this.intData = intData;
		this.floatData = floatData;
		this.type = type;
		this.children = new ArrayList<AST>();
	}
}
