package tables;

import java.util.HashMap;
import typing.Type;
import java.util.Formatter;

public final class FunctionTable extends HashMap<Integer, Function> {
    private HashMap<String, Integer> indexes = new HashMap<String, Integer>();
    private int current = 0;

    public int put(Function function) {
        this.put(current, function);
        this.indexes.put(function.getName(), current);
        return this.current++;
    }

    public VariableTable getVariableTable(int index) {
        return this.get(index).getVariableTable();
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

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("Function table:\n");

        for (int i = 0; i < this.size(); i++) {
            formatter.format(
                "Function %d -- name: %s, line: %d, type: %s\n",
                i, getName(i), getLine(i), getType(i).toString());
        }

        formatter.close();

        return stringBuilder.toString();
    }
}
