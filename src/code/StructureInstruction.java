package code;

import java.util.Formatter;

// Instruction for bytecode structures.
public final class StructureInstruction extends Instruction {
    public final Structure op;

    public StructureInstruction(Structure op, String o1, String o2, String o3) {
        this.op = op;
        this.o1 = o1;
        this.o2 = o2;
        this.o3 = o3;
    }
    
    public String getString(int addr) {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        
        switch (this.op.name) {
            case ".class":
                f.format(".class %s %s", this.op.body, this.o1);
                break;

            case ".super":
                f.format(".super %s", this.op.body);
                break;
        
            case ".method":
                f.format(".method %s %s", this.op.body, this.o1);
                break;

            case ".limit":
                f.format("    .limit %s %s", this.o1, this.o2);
                break;
            
            case ".label":
                f.format("%s:", this.o1);
                break;

            default:
                if (this.o1.equals("")) {
                    f.format("%s", this.op.name);
                } else if (this.o2.equals("")) {
                    f.format("%s %s", this.op.name, this.o1);
                } else if (this.o3.equals("")) {
                    f.format("%s %s %s", this.op.name, this.o1, this.o2);
                } else {
                    f.format("%s %s %s %s", this.op.name, this.o1, this.o2, this.o3);
                }
        }

        f.close();
        return sb.toString();
    }

    // Constantes
    
    // Memory is split between data and instruction memory.
    // This is called the Harvard architecture, in contrast to the von Neumann
    // (stored program) architecture.
    public static final int INSTR_MEM_SIZE = 1024;  // instr_mem[]
    public static final int DATA_MEM_SIZE  = 1024;  // data_mem[]
    // The machine also has a string table str_tab[] for storing strings with
    // the command SSTR. Maximum size for each string is 128 chars.
}
