package typing;

import static ast.NodeKind.BOOL2INT_NODE;
import static ast.NodeKind.BOOL2REAL_NODE;
import static ast.NodeKind.BOOL2STRING_NODE;
import static ast.NodeKind.INT2REAL_NODE;
import static ast.NodeKind.INT2STRING_NODE;
import static ast.NodeKind.REAL2STRING_NODE;

import ast.AST;
import static typing.Type.*;
import exception.SemanticException;

public enum Conversion {
    BOOL2INT,
    BOOL2REAL,
    BOOL2STRING,
    INT2REAL,
    INT2STRING,
    REAL2STRING,
    NONE;

    public static AST createConversionNode(Conversion conversion, AST n) {
        switch(conversion) {
            case BOOL2INT:  return AST.newSubtree(BOOL2INT_NODE, INT_TYPE, n);
            case BOOL2REAL:  return AST.newSubtree(BOOL2REAL_NODE, REAL_TYPE, n);
            case BOOL2STRING:  return AST.newSubtree(BOOL2STRING_NODE, STR_TYPE, n);
            case INT2REAL:  return AST.newSubtree(INT2REAL_NODE, REAL_TYPE, n);
            case INT2STRING:  return AST.newSubtree(INT2STRING_NODE, STR_TYPE, n);
            case REAL2STRING:  return AST.newSubtree(REAL2STRING_NODE, STR_TYPE, n);
            case NONE: return n;
            default:
                String message = "INTERNAL ERROR: invalid conversion of types!";
                throw new SemanticException(message);
        }
    }

    public static final class Unified {
        public final Type type;
        public final Conversion leftConversion;
        public final Conversion rightConversion;
        
        public Unified(Type type, Conversion lc, Conversion rc) {
            this.type = type;
            this.leftConversion = lc;
            this.rightConversion = rc;
        }

        // public Unified(Type type, Conversion lc, Conversion rc) {
        //     this.type = type;
        //     this.leftConversion = lc;
        //     this.rightConversion = rc;
        // }
    }
}


