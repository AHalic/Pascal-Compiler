package tables;

import java.util.HashMap;
import typing.Type;
import java.util.Formatter;
import array.Array;

public final class VariableTable extends HashMap<Integer, Entry> {
    private HashMap<String, Integer> indexes = new HashMap<String, Integer>();
    private int current = 0;

    public int put(String name, int line, Type type) {
        tables.Entry entry = new tables.Entry(name, line, type);
        this.put(current, entry);
        this.indexes.put(name, current);
        return this.current++;
    }

    public int put(Array array) {
        this.put(current, array);
        this.indexes.put(array.getName(), current);
        return this.current++;
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

    public int getLine(int index) {
        return this.get(index).getLine();
    }

    public Type getType(int index) {
        return this.get(index).getType();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("Variables table:\n");

        for (int i = 0; i < this.size(); i++) {
            formatter.format(
                "Entry %d -- name: %s, line: %d, type: %s",
                i, getName(i), getLine(i), getType(i).toString());

            // Se for um array, imprime o tipo
            if (getType(i) == Type.ARRAY_TYPE) {
                formatter.format("_" + ((Array)get(i)).getComponentType());
            }

            formatter.format("\n");
        }

        formatter.close();

        return stringBuilder.toString();
	}
}
