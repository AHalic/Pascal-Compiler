package tables;

import java.util.HashMap;
import typing.Type;
import java.util.Formatter;

public final class VariableTable extends HashMap<Integer, Entry> {
    private HashMap<String, Integer> indexes = new HashMap<String, Integer>();
    private int current = 0;

    public int put(String name, int line, Type type) {
        tables.Entry entry = new tables.Entry(name, line, type);
        this.put(current, entry);
        this.indexes.put(name, current);
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
                "Entry %d -- name: %s, line: %d, type: %s\n",
                i, getName(i), getLine(i), getType(i).toString());
        }

        formatter.close();

        return stringBuilder.toString();
	}
}
