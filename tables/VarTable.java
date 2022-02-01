package tables;

import typing.Type;

import java.util.HashMap;
import java.util.Formatter;

public class VarTable {
    private HashMap<Integer, TableLine> table = new HashMap<Integer, TableLine>();
    private int counter = 0;

    public int lookupVar(String s) {
        for (int key: table.keySet()) {
            if (table.get(key).name.equals(s)) {
                return key;
            }
        }

        return -1;
    }

    public int addVar(String s, int line, Type type) {
        TableLine tl = new TableLine(s, line, type);
        table.put(counter, tl);
        counter++;
        return counter - 1;
    }

    public String getName(int i) {
		return table.get(i).name;
	}
	
	public int getLine(int i) {
		return table.get(i).line;
	}
	
	public Type getType(int i) {
		return table.get(i).type;
	}

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);

        f.format("Variables table:\n");

        for (int key: table.keySet()) {
            f.format("Entry -- name: %s, line: %d, type: %s\n",
            getName(key), getLine(key), getType(key).toString());
        }

		f.close();

		return sb.toString();
    }

    private final class TableLine {
        String name;
        int line;
        Type type;

        TableLine(String name, int line, Type type) {
            this.name = name;
            this.line = line;
            this.type = type;
        }
    }
}
