package tables;

import java.util.HashMap;
import java.util.Formatter;

public class StrTable extends HashMap<Integer, String> {
    private int counter = 0;
    
    public int putStr(String value) {
        if(this.containsValue(value)) {
            for (int key: this.keySet()) {
                if (this.get(key).equals(value)) {
                    return key;
                }
            }
            return -1;
        } else {
            super.put(counter, value);
            counter++;
            return counter - 1;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);

        f.format("Strings table:\n");

        for (Integer key: this.keySet()) {
            String value = this.get(key);
            f.format("Entry %d -- %s\n", key, value);
        }

		f.close();

		return sb.toString();
    }
}