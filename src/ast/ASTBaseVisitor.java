package ast;

import org.antlr.v4.parse.ANTLRParser.range_return;

public abstract class ASTBaseVisitor<T> {
    public void execute(AST root) {
        visit(root);
    }

    protected T visit(AST node) {
        switch(node.kind) {
            case ASSIGN_NODE:       return visitAssign(node);
            case EQ_NODE:           return visitEq(node);
	        case BLOCK_NODE:        return visitBlock(node);
	        case BOOL_VAL_NODE:     return visitBoolVal(node);
            case IF_NODE:           return visitIf(node);
	        case INT_VAL_NODE:      return visitIntVal(node);
            case NIL_VAL_NODE:      return visitNilVal(node);
            case ARRAY_NODE:        return visitArray(node);
            case LT_NODE:           return visitLt(node);
            case GT_NODE:           return visitGt(node);
            case LE_NODE:           return visitLe(node);
            case GE_NODE:           return visitGe(node);
            case NOT_EQUAL_NODE:    return visitNotEqual(node);
            case MINUS_NODE:        return visitMinus(node);
	        case OVER_NODE:         return visitOver(node);
	        case PLUS_NODE:         return visitPlus(node);
	        case PROGRAM_NODE:      return visitProgram(node);
            case FUNC_LIST_NODE:    return visitFuncList(node);
            case FUNCTION_NODE:     return visitFunction(node);
            case REAL_VAL_NODE:     return visitRealVal(node);
            case REPEAT_NODE:       return visitRepeat(node);
	        case STR_VAL_NODE:      return visitStrVal(node);
            case TIMES_NODE:        return visitTimes(node);
            case AND_NODE:          return visitAnd(node);
            case OR_NODE:           return visitOr(node);
            case NOT_NODE:          return visitNot(node);
            case VAR_DECL_NODE:     return visitVarDecl(node);
            case VAR_LIST_NODE:     return visitVarList(node);
            case VAR_USE_NODE:      return visitVarUse(node);
            case SUBSCRIPT_NODE:    return visitSubscript(node);
            case FUNC_USE_NODE:     return visitFuncUse(node);

            case BOOL2INT_NODE:     return visitBOOL2INT(node);
            case BOOL2REAL_NODE:    return visitBOOL2REAL(node);
            case BOOL2STRING_NODE:  return visitBOOL2STRING(node);
            case INT2REAL_NODE:     return visitINT2REAL(node);
            case INT2STRING_NODE:   return visitINT2STRING(node);
            case REAL2STRING_NODE:  return visitREAL2STRING(node);

            default:
	            System.err.printf("Invalid kind: %s!\n", node.kind.toString());
	            System.exit(1);
	            return null;
        }   
    }

    protected abstract T visitAssign(AST node);
    
    protected abstract T visitEq(AST node);
            
    protected abstract T visitBlock(AST node);
            
    protected abstract T visitBoolVal(AST node);
    
    protected abstract T visitIf(AST node);
            
    protected abstract T visitIntVal(AST node);
        
    protected abstract T visitNilVal(AST node);
        
    protected abstract T visitArray(AST node);
        
    protected abstract T visitLt(AST node);
            
    protected abstract T visitGt(AST node);
            
    protected abstract T visitLe(AST node);
            
    protected abstract T visitGe(AST node);
            
    protected abstract T visitNotEqual(AST node);
    
    protected abstract T visitMinus(AST node);
            
    protected abstract T visitOver(AST node);
            
    protected abstract T visitPlus(AST node);
            
    protected abstract T visitProgram(AST node);
    
    protected abstract T visitFuncList(AST node);
    
    protected abstract T visitFunction(AST node);
    
    protected abstract T visitRealVal(AST node);
    
    protected abstract T visitRepeat(AST node);
        
    protected abstract T visitStrVal(AST node);
        
    protected abstract T visitTimes(AST node);
        
    protected abstract T visitAnd(AST node);
        
    protected abstract T visitOr(AST node);
            
    protected abstract T visitNot(AST node);
        
    protected abstract T visitVarDecl(AST node);
    
    protected abstract T visitVarList(AST node);
    
    protected abstract T visitVarUse(AST node);
        
    protected abstract T visitSubscript(AST node);
    
    protected abstract T visitFuncUse(AST node);

    protected abstract T visitBOOL2INT(AST node);
    
    protected abstract T visitBOOL2REAL(AST node);
    
    protected abstract T visitBOOL2STRING(AST node);

    protected abstract T visitINT2REAL(AST node);
    
    protected abstract T visitINT2STRING(AST node);

    protected abstract T visitREAL2STRING(AST node);
}
