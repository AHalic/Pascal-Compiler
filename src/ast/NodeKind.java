package ast;

public enum NodeKind {
    ASSIGN_NODE {
        public String toString() {
            return ":=";
        }
    },
    EQ_NODE {
        public String toString() {
            return "=";
        }
    },
    BLOCK_NODE {
        public String toString() {
            return "block";
        }
    },
    BOOL_VAL_NODE {
        public String toString() {
            return "";
        }
    },
    IF_NODE {
        public String toString() {
            return "if";
        }
    },
    INT_VAL_NODE {
        public String toString() {
            return "";
        }
    },
    LT_NODE {
        public String toString() {
            return "<";
        }
    },
    GT_NODE {
        public String toString() {
            return ">";
        }
    },
    LE_NODE {
        public String toString() {
            return "<=";
        }
    },
    GE_NODE {
        public String toString() {
            return ">=";
        }
    },
    NOT_EQUAL_NODE {
        public String toString() {
            return "<>";
        }
    },
    MINUS_NODE {
        public String toString() {
            return "-";
        }
    },
    OVER_NODE {
        public String toString() {
            return "/";
        }
    },
    PLUS_NODE {
        public String toString() {
            return "+";
        }
    },
    PROGRAM_NODE {
        public String toString() {
            return "program";
        }
    },
    FUNC_LIST_NODE {
        public String toString() {
            return "func_list";
        }
    },
    FUNCTION_NODE {
        public String toString() {
            return "function";
        }
    },
    READ_NODE {
        public String toString() {
            return "read";
        }
    },
    REAL_VAL_NODE {
        public String toString() {
            return "";
        }
    },
    REPEAT_NODE {
        public String toString() {
            return "repeat";
        }
    },
    STR_VAL_NODE {
        public String toString() {
            return "";
        }
    },
    TIMES_NODE {
        public String toString() {
            return "*";
        }
    },
    AND_NODE {
        public String toString() {
            return "AND";
        }
    },
    OR_NODE {
        public String toString() {
            return "OR";
        }
    },
    VAR_DECL_NODE {
        public String toString() {
            return "var_decl";
        }
    },
    VAR_LIST_NODE {
        public String toString() {
            return "var_list";
        }
    },
    VAR_USE_NODE {
        public String toString() {
            return "var_use";
        }
    },
    FUNC_USE_NODE {
        public String toString() {
            return "func_use";
        }
    },
    WRITE_NODE {
        public String toString() {
            return "write";
        }
    },

    // Type conversion.
    BOOL2INT_NODE { 
        public String toString() {
            return "BOOL2INT";
        }
    },
    BOOL2REAL_NODE {
        public String toString() {
            return "BOOL2REAL";
        }
    },
    BOOL2STRING_NODE {
        public String toString() {
            return "BOOL2STRING";
        }
    },
    INT2REAL_NODE {
        public String toString() {
            return "INT2REAL";
        }
    },
    INT2STRING_NODE {
        public String toString() {
            return "INT2STRING";
        }
    },
    REAL2STRING_NODE {
        public String toString() {
            return "REAL2STRING";
        }
    };
    
    public static boolean hasData(NodeKind kind) {
        switch(kind) {
            case BOOL_VAL_NODE:
            case INT_VAL_NODE:
            case REAL_VAL_NODE:
            case STR_VAL_NODE:
            case VAR_DECL_NODE:
            case VAR_USE_NODE:
            case FUNC_USE_NODE:
                return true;
            default:
                return false;
        }
    }
}
