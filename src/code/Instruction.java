package code;

import java.util.Formatter;

// Instruction quadruple.
public abstract class Instruction {
    public String o1;
    public String o2;
    public String o3;

    public Instruction() {
    }

    abstract public String getString(int addr);

    // Constantes
    
    // Memory is split between data and instruction memory.
    // This is called the Harvard architecture, in contrast to the von Neumann
    // (stored program) architecture.
    public static final int INSTR_MEM_SIZE = 1024;	// instr_mem[]
    public static final int DATA_MEM_SIZE  = 1024;  // data_mem[]
    // The machine also has a string table str_tab[] for storing strings with
    // the command SSTR. Maximum size for each string is 128 chars.
}
