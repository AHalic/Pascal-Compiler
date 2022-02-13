package tables;

import typing.Type;

public class Function extends Entry {
    protected boolean builtIn = false;
    private VariableTable variableTable = new VariableTable();

    public Function(String name, int line, Type type) {
        super(name, line, type);
    }

    public Function(String name, int line, Type type, boolean builtIn) {
        super(name, line, type);
        this.builtIn = builtIn;
    }

    public int addVariable(String name, Type type) {
        return this.variableTable.put(name, this.line, type);
    }

    public VariableTable getVariableTable() {
        return this.variableTable;
    }

    @Override
    public String toString() {
        return "Function [name=" + name + ", type=" + type +
               ", line=" + line + "]";
    }
}
