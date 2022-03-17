package tables;

import typing.Type;

public class Function extends Entry {
    private int parametersQuantity = 0;
    private VariableTable variableTable = new VariableTable();
    private StringTable stringTable = new StringTable();

    public Function(String name, int line, Type type) {
        super(name, line, type);
    }

    public int addVariable(String name, Type type) {
        return this.variableTable.add(name, this.line, type);
    }

    public int addString(String name, int line) {
        return this.stringTable.add(new StringEntry(name, line));
    }

    public int addVariable(String name, int line, Type type, boolean param) {
        if (param) this.parametersQuantity++;
        return this.variableTable.add(name, this.line, type);
    }

    public VariableTable getVariableTable() {
        return this.variableTable;
    }

    public StringTable getStringTable() {
        return this.stringTable;
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
