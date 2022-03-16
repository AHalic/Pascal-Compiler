package code;

public enum Structure {
    // Bytecode structures
    space("", "", 0),
    comment("", "", 1),
    programDeclaration(".class", "public", 1),
    methodDeclaration(".method", "public static", 3),
    endMethod(".end method", "", 0),
    limit(".limit", "", 2),
    objectInheritance(".super", "java/lang/Object", 0);

    public final String name;
    public final String body;
    public final int paramsCount;
    
    private Structure(String name, String body, int paramsCount) {
        this.name = name;
        this.body = body;
        this.paramsCount = paramsCount;
    }
    
    public String toString() {
        return this.name + " " + this.body;
    }
    
}