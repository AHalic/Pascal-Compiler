package code;

import java.util.Formatter;

// Instruction for bytecode structures.
public final class StructureInstruction extends Instruction {
    public final Structure op;
    public String o1;
    public String o2;
    public String o3;

    public StructureInstruction(Structure op, String o1, String o2, String o3) {
        this.op = op;
        this.o1 = o1;
        this.o2 = o2;
        this.o3 = o3;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("%s", this.op.toString());
        
        // if (op.paramsCount == 1)
        //     f.format(" %s", this.o1);
        // else if (op.paramsCount == 2)
        //     f.format(" %s", this.o1, this.o2);
        // else if (op.paramsCount == 3)
        //     f.format(" %s", this.o1, this.o2, this.o3);

        switch (this.op.name) {
            case ".class":
                f.format(" %s", this.o1);
                break;
        
            case ".method":
                f.format(" %s(%s)%s", this.o1, this.o2, this.o3);
                break;

            case ".limit":
                f.format(" %s %s", this.o1, this.o2);
                break;
        }

        f.close();
        return sb.toString();
    }
    
    // Constantes
    
    // Memory is split between data and instruction memory.
    // This is called the Harvard architecture, in contrast to the von Neumann
    // (stored program) architecture.
    public static final int INSTR_MEM_SIZE = 1024;	// instr_mem[]
    public static final int DATA_MEM_SIZE  = 1024;  // data_mem[]
    // The machine also has a string table str_tab[] for storing strings with
    // the command SSTR. Maximum size for each string is 128 chars.

}
