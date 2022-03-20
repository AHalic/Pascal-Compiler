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
    aastore("aastore", 0),
    astore("astore", 1),
    aload("aload", 1),
    aaload("aaload", 0),
    baload("baload", 0),
    iaload("iaload", 0),
    faload("faload", 0),
    bastore("bastore", 0),
    //
    istore("istore", 1),
    iastore("iastore", 0),
    iload("iload", 1),
    idiv("idiv", 0),
    isub("isub", 0),
    iadd("iadd", 0),
    i2f("i2f", 0),
    imul("imul", 0),
    iand("iand", 0),
    ior("ior", 0),
    ineg("ineg", 0),
    //
    fstore("fstore", 1),
    fastore("fastore", 0),
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
    iflt("iflt", 1),
    ifgt("ifgt", 1),
    ifge("ifge", 1),
    ifle("ifle", 1),
    ifne("ifne", 1),
    ifnull("ifnull", 1),
    fcmpg("fcmpg", 0),
    fcmpl("fcmpl", 0),
    if_icmpeq("if_icmpeq", 1),
    if_icmple("if_icmple", 1),
    if_icmplt("if_icmplt", 1),
    if_icmpge("if_icmpge", 1),
    if_icmpgt("if_icmpgt", 1),
    if_icmpne("if_icmpne", 1),
    gotoProgram("goto", 1),
    //
    newarray("newarray", 1),
    multianewarray("multianewarray", 2);
    
    
    public final String name;
    public final int opCount;
    
    private OpCode(String name, int opCount) {
        this.name = name;
        this.opCount = opCount;
    }
    
    public String toString() {
        return this.name;
    }

    public Boolean isJump() {
        switch (this.name) {
            case "ifeq":
            case "ifgt":
            case "ifle":
            case "ifne":
            case "ifge":
            case "iflt":
            case "fcmpl":
            case "if_icmpeq":
            case "if_icmple":
            case "if_icmplt":
            case "if_icmpge":
            case "if_icmpgt":
            case "if_icmpne":
            case "ifnull":
            case "goto":
                return true;
        }
        return false;
    }
    
}