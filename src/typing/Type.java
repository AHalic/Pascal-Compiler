package typing;

import static typing.Conversion.INT2REAL;
import static typing.Conversion.NONE;

import typing.Conversion.Unified;

public enum Type {
    INT_TYPE {
        public String toString() {
            return "int";
        }
    },
    REAL_TYPE {
        public String toString() {
            return "real";
        }
    },
    BOOL_TYPE {
        public String toString() {
            return "bool";
        }
    },
    STR_TYPE {
        public String toString() {
            return "string";
        }
    },
    CHAR_TYPE {
        public String toString() {
            return "char";
        }
    },
    ARRAY_TYPE {
        public String toString() {
            return "array";
        }
    },
    NO_TYPE { // Indica um erro de tipos.
        public String toString() {
            return "no_type";
        }
    };

    //
    private static Unified plus[][] ={
        { new Unified(INT_TYPE, NONE, NONE), new Unified(REAL_TYPE, INT2REAL, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE) },
        { new Unified(REAL_TYPE, NONE, INT2REAL), new Unified(REAL_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE) },
        { new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE) },
        { new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(STR_TYPE, NONE, NONE) }
    };

    //
    private static Unified other[][] = {
        { new Unified(INT_TYPE, NONE, NONE), new Unified(REAL_TYPE, INT2REAL, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE) },
        { new Unified(REAL_TYPE, NONE, INT2REAL), new Unified(REAL_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE) },
        { new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE) },
        { new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE) }
    };

    //
    private static Unified comp[][] = {
        { new Unified(BOOL_TYPE, NONE, NONE), new Unified(BOOL_TYPE, INT2REAL, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE) },
        { new Unified(BOOL_TYPE, NONE, INT2REAL), new Unified(BOOL_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE) },
        { new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(BOOL_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE)   },
        { new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(NO_TYPE, NONE, NONE), new Unified(BOOL_TYPE, NONE, NONE) }
    };

    public Unified unifyPlus(Type that) {
        return plus[this.ordinal()][that.ordinal()];
    }

    public Unified unifyOtherArith(Type that) {
        return other[this.ordinal()][that.ordinal()];
    }

    public Unified unifyComp(Type that) {
        return comp[this.ordinal()][that.ordinal()];
    }
}
