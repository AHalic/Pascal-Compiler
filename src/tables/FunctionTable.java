package tables;

import java.util.HashMap;
import typing.Type;
import java.util.Formatter;

public final class FunctionTable extends HashMap<Integer, Function> {
    private HashMap<String, Integer> indexes = new HashMap<String, Integer>();
    private int current = 0;

    public FunctionTable() {
        // Cria as funções built-in
        // write, writeln
        int idx = this.addBuiltIn("write", Type.NO_TYPE);
        this.get(idx).addVariable("message", Type.STR_TYPE);
        idx = this.addBuiltIn("writeln", Type.NO_TYPE);
        this.get(idx).addVariable("message", Type.STR_TYPE);
        // read, readln
        idx = this.addBuiltIn("read", Type.NO_TYPE);
        idx = this.addBuiltIn("readln", Type.NO_TYPE);
    }

    public int addBuiltIn(String name, Type type) {
        Function function = new Function(name, -1, type, true);
        function.addVariable(name, type);
        return this.put(function);
    }

    public int put(Function function) {
        this.put(current, function);
        this.indexes.put(function.getName(), current);
        return this.current++;
    }

    public VariableTable getVariableTable(int index) {
        return this.get(index).getVariableTable();
    }

    public VariableTable getVariableTable(String name) {
        int idx = this.indexes.get(name);
        return this.getVariableTable(idx);
    }

    public VariableTable getVariableTable() {
        return this.getVariableTable(this.current - 1);
    }

    public int addVarInLastFunction(String name, Type type) {
        return this.get(this.current - 1).addVariable(name, type);
    }

    public int getIndex(String string) {
        return this.indexes.get(string);
    }

    public boolean contains(String string) {
        return this.indexes.containsKey(string);
    }

    public String getName(int index) {
        return this.get(index).getName();
    }

    public String getName() {
        return this.getName(this.current - 1);
    }

    public int getLine(int index) {
        return this.get(index).getLine();
    }

    public Type getType(int index) {
        return this.get(index).getType();
    }

    public Type getType(String name) {
        int idx = this.indexes.get(name);
        return this.get(idx).getType();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("Function table:\n");

        for (int i = 0; i < this.size(); i++) {
            // Não imprime as funções built-in
            if (!get(i).builtIn) {
                formatter.format(
                "Function %d -- name: %s, line: %d, type: %s\n",
                i, getName(i), getLine(i), getType(i).toString());
            }
        }

        formatter.close();

        return stringBuilder.toString();
    }
}
