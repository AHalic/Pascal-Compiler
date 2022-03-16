package code;

import java.util.Formatter;

// Instruction quadruple.
public final class OpInstruction extends Instruction {

    // Público para não precisar de getter/setter.
    public final OpCode op;
    // Estes campos não podem ser final por causa do backpatching...
    public String o1;	// Operands, which can be int or float registers,
    public String o2;	// int addresses or offsets, or
    public String o3;	// integer or float constants (must be in an integer repr.)

    public OpInstruction(OpCode op, String o1, String o2, String o3) {
        this.op = op;
        this.o1 = o1;
        this.o2 = o2;
        this.o3 = o3;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("    %s", this.op.toString());
        if (this.op.opCount == 1) {
            f.format(" %s", this.o1);
        } else if (this.op.opCount == 2) {
            f.format(" %s, %s", this.o1, this.o2);
        } else if (this.op.opCount == 3) {
            f.format(" %s, %s, %s", this.o1, this.o2, this.o3);
        }
        f.close();
        return sb.toString();
    }
    
}
