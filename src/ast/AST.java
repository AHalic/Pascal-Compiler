package ast;

import static typing.Type.*;
import java.util.ArrayList;
import java.util.List;

import array.Array;
import tables.FunctionTable;
import tables.VariableTable;
import tables.StringTable;
import typing.Type;

public class AST {
    public final NodeKind kind;
    public final int intData;
    public final float floatData;
    public final Type type;
    public final boolean functionScope;
    private final List<AST> children;

    private AST(NodeKind kind, int intData, float floatData, Type type, boolean functionScope) {
        this.kind = kind;
        this.intData = intData;
        this.floatData = floatData;
        this.type = type;
        this.functionScope = functionScope;
        this.children = new ArrayList<AST>();
    }

    public AST(NodeKind kind, int intData, Type type) {
        this(kind, intData, 0.0f, type, false);
    }

    public AST(NodeKind kind, float floatData, Type type) {
        this(kind, 0, floatData, type, false);
    }

    public static AST ASTWithFunctionScope(NodeKind kind, int intData, Type type) {
        return new AST(kind, intData, 0.0f, type, true);
    }

    public void addChild(AST child) {
        this.children.add(child);
    }

    public void clear() {
        this.children.clear();
    }

    public int getChildrenQuantity() {
        return this.children.size();
    }

    // Retorna o filho no índice passado.
    // Não há nenhuma verificação de erros!
    public AST getChild(int idx) {
        // Claro que um código em produção precisa testar o índice antes para
        // evitar uma exceção.
        return this.children.get(idx);
    }

    // Cria um nó e pendura todos os filhos passados como argumento.
    public static AST newSubtree(NodeKind kind, Type type, AST... children) {
        AST node = new AST(kind, 0, type);

        for (AST child: children) {
            node.addChild(child);
        }

        return node;
    }

    public static AST newSubtree(NodeKind kind, Type type, int functionId, AST... children) {
        AST node = new AST(kind, functionId, type);

        for (AST child: children) {
            node.addChild(child);
        }

        return node;
    }

    // Variáveis internas usadas para geração da saída em DOT.
    // Estáticas porque só precisamos de uma instância.
    private static int nr;
    private static VariableTable vt;
    private static FunctionTable ft;
    private static StringTable st;

    // Imprime recursivamente a codificação em DOT da subárvore começando no nó atual.
    // Usa stderr como saída para facilitar o redirecionamento, mas isso é só um hack.
    private int printNodeDot(boolean functionScope, int functionID) {
        int myNr = nr++;

        System.err.printf("node%d[label=\"", myNr);

        //
        
        if (this.type == ARRAY_TYPE) {
            if (functionScope) {
                System.err.printf("(%s_%s) ",
                    ft.getVariableTable(functionID).getType(intData),
                    ((Array)ft.getVariableTable(functionID).get(intData)).getComponentType());
            } else {
                System.err.printf("(%s_%s) ", vt.getType(intData), ((Array)vt.get(intData)).getComponentType());
            }
        } else if (this.type != NO_TYPE) {
            System.err.printf("(%s) ", this.type.toString());
        }

        //
        if (!functionScope && (this.kind == NodeKind.VAR_DECL_NODE || this.kind == NodeKind.VAR_USE_NODE | this.kind == NodeKind.VAR_USE_NODE)) {
            System.err.printf("%s", vt.getName(this.intData));

            if ((this.type == ARRAY_TYPE) && (this.kind != NodeKind.VAR_USE_NODE)) {
                System.err.printf("%s@", ((Array)vt.get(this.intData)).getRangeString());
            } else {
                System.err.printf("@");
            }
        } else if ((this.kind == NodeKind.FUNCTION_NODE) || 
                   (functionScope && (this.kind == NodeKind.VAR_DECL_NODE || this.kind == NodeKind.VAR_USE_NODE))) {
            //
            functionScope = true;
            
            //
            if (this.kind == NodeKind.FUNCTION_NODE) {
                functionID = this.intData;
                System.err.printf("%s", ft.getVariableTable(functionID).getName(0));
            } else {
                System.err.printf("%s", ft.getVariableTable(functionID).getName(this.intData));
            }

            if (this.type == ARRAY_TYPE) {
                System.err.printf("%s@", ((Array)ft.getVariableTable(functionID).get(this.intData)).getRangeString());
            } else {
                System.err.printf("@");
            }
        } else if (this.kind == NodeKind.FUNC_USE_NODE) {
            System.err.printf("%s()", ft.get(this.intData).get(0).getName());
        } else {
            System.err.printf("%s", this.kind.toString());
        }

        //
        if (NodeKind.hasData(this.kind)) {
            if (this.kind == NodeKind.REAL_VAL_NODE) {
                System.err.printf("%.2f", this.floatData);
            } else if (this.kind == NodeKind.STR_VAL_NODE) {
                System.err.printf("'%s'", st.get(this.intData));
                System.err.printf("@%d", this.intData);
            } else if (this.kind == NodeKind.FUNC_USE_NODE) {
                System.err.printf("@%d", this.intData);
            } else {
                System.err.printf("%d", this.intData);
            }
        } else if (NodeKind.FUNCTION_NODE == this.kind) {
            System.err.printf("%d", this.intData);
        }

        System.err.printf("\"];\n");

        //
        for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i) == null) break;
            int childNr = this.children.get(i).printNodeDot(functionScope, functionID);
            System.err.printf("node%d -> node%d;\n", myNr, childNr);
        }

        return myNr;
    }

    // Imprime a árvore toda em stderr.
    public static void printDot(
        AST tree, VariableTable variableTable,
        FunctionTable functionTable, StringTable stringTable) {
        //
        nr = 0;
        vt = variableTable;
        ft = functionTable;
        st = stringTable;

        System.err.printf("digraph {\ngraph [ordering=\"out\"];\n");
        tree.printNodeDot(tree.functionScope, -1);
        System.err.printf("}\n");
    }

    @Override
    public String toString() {
        String text = "AST [KIND=" + kind + ", TYPE=" + type + ", VALUE=";
        
        if (type == INT_TYPE) {
            text += intData + "]";
        } else {
            text += floatData + "]";
        }

        return text;
    }
}
