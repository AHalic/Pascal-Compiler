package tables;

import java.util.HashMap;
import java.util.Formatter;

public class StringTable extends HashMap<Integer, String> {
    private HashMap<String, Integer> indexes = new HashMap<String, Integer>();
    private int current = 0;

    public int add(String string) {
        if (!this.indexes.containsKey(string)) {
            int idx = this.current;
            this.put(idx, string);
            this.indexes.put(string, idx);
            return this.current++;
        }

        return this.indexes.get(string);
    }

    public int getIndex(String string) {
        return this.indexes.get(string);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("Strings table:\n");

        for (int i = 0; i < this.size(); i++) {
            formatter.format("Entry %d -- %s\n", i, this.get(i));
        }

        formatter.close();

        return stringBuilder.toString();
	}
}
