package code;

public enum OpCode {
    // Bytecode Opcodes
    create("new", 1),
    invokespecial("invokespecial", 1),
    invokevirtual("invokevirtual", 1),
    dup("dup", 0),
    ldc("ldc", 1),
    astore("astore", 1),
    aload("aload", 1),
    istore("istore", 1),
    iload("iload", 1),
    fstore("fstore", 1),
    fload("fload", 1),
    returnProgram("return", 0),
    iadd("iadd", 0),
    i2f("i2f", 0),
    label("", 1),
    isub("isub", 0);
    
    
    public final String name;
    public final int opCount;
    
    private OpCode(String name, int opCount) {
        this.name = name;
        this.opCount = opCount;
    }
    
    public String toString() {
        return this.name;
    }
    
}