package code;

public enum OpCode {
    // Bytecode Opcodes
    create("new", 1),
    dup("dup", 0),
    ldc("ldc", 1),
    //
    getstatic("getstatic", 2),
    //
    invokespecial("invokespecial", 1),
    invokevirtual("invokevirtual", 1),
    invokestatic("invokestatic", 1),
    //
    astore("astore", 1),
    aload("aload", 1),
    baload("baload", 1),
    //
    istore("istore", 1),
    iload("iload", 1),
    idiv("idiv", 0),
    isub("isub", 0),
    iadd("iadd", 0),
    i2f("i2f", 0),
    imul("imul", 0),
    //
    fstore("fstore", 1),
    fload("fload", 1),
    fdiv("fdiv", 0),
    fadd("fadd", 0),
    fsub("fsub", 0),
    fmul("fmul", 0),
    //
    returnNULL("return", 0),
    returnFLOAT("freturn", 0),
    returnINT("ireturn", 0),
    returnREFERENCE("areturn", 0),
    label("", 1),
    //
    ifeq("ifeq", 1),
    ifgt("ifgt", 1),
    ifle("ifle", 1),
    ifne("ifne", 1),
    if_icmpeq("if_icmpeq", 1),
    if_icmple("if_icmple", 1),
    if_icmplt("if_icmplt", 1),
    if_icmpge("if_icmpge", 1),
    if_icmpgt("if_icmpgt", 1),
    if_icmpne("if_icmpne", 1),
    gotoProgram("goto", 1);

    
    
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