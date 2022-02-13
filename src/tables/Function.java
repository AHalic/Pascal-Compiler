package tables;

import typing.Type;

public class Function extends Entry {
    protected boolean builtIn = false;
    private int parametersQuantity = 0;
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

    public int addVariable(String name, Type type, boolean param) {
        if (param) this.parametersQuantity++;
        return this.variableTable.put(name, this.line, type);
    }

    public VariableTable getVariableTable() {
        return this.variableTable;
    }

    public int getParameterQuantity() {
        return this.parametersQuantity;
    }

    @Override
    public String toString() {
        return "Function [name=" + name + ", type=" + type +
               ", line=" + line + ", Params=" + parametersQuantity + "]";
    }
}
