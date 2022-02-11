package ast;

import static typing.Type.*;
import java.util.ArrayList;
import java.util.List;
import tables.VariableTable;
import typing.Type;

public class AST {
    public final NodeKind kind;
    public final int intData;
    public final float floatData;
    public final Type type;
    private final List<AST> children;

    private AST(NodeKind kind, int intData, float floatData, Type type) {
        this.kind = kind;
        this.intData = intData;
        this.floatData = floatData;
        this.type = type;
        this.children = new ArrayList<AST>();
    }

    public AST(NodeKind kind, int intData, Type type) {
        this(kind, intData, 0.0f, type);
    }

    public AST(NodeKind kind, float floatData, Type type) {
        this(kind, 0, floatData, type);
    }

    public void addChild(AST child) {
        this.children.add(child);
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

    // Variáveis internas usadas para geração da saída em DOT.
    // Estáticas porque só precisamos de uma instância.
    private static int nr;
    private static VariableTable vt;

    // Imprime recursivamente a codificação em DOT da subárvore começando no nó atual.
    // Usa stderr como saída para facilitar o redirecionamento, mas isso é só um hack.
    private int printNodeDot() {
        int myNr = nr++;

        System.err.printf("node%d[label=\"", myNr);
        if (this.type != NO_TYPE) {
            System.err.printf("(%s) ", this.type.toString());
        }
        if (this.kind == NodeKind.VAR_DECL_NODE || this.kind == NodeKind.VAR_USE_NODE) {
            System.err.printf("%s@", vt.getName(this.intData));
        } else {
            System.err.printf("%s", this.kind.toString());
        }
        if (NodeKind.hasData(this.kind)) {
            if (this.kind == NodeKind.REAL_VAL_NODE) {
                System.err.printf("%.2f", this.floatData);
            } else if (this.kind == NodeKind.STR_VAL_NODE) {
                System.err.printf("@%d", this.intData);
            } else {
                System.err.printf("%d", this.intData);
            }
        }
        System.err.printf("\"];\n");

        for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i) == null) {
                break;
            }
            int childNr = this.children.get(i).printNodeDot();
            System.err.printf("node%d -> node%d;\n", myNr, childNr);
        }
        return myNr;
    }

    // Imprime a árvore toda em stderr.
    public static void printDot(AST tree, VariableTable table) {
        nr = 0;
        vt = table;
        System.err.printf("digraph {\ngraph [ordering=\"out\"];\n");
        tree.printNodeDot();
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
