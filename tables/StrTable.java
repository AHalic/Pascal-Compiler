package tables;

import java.util.HashMap;
import java.util.Formatter;

public class StrTable extends HashMap<String, String> {
    
    @Override
    public String put(String s1, String s2) {
        return super.put(s1, s2);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);

        f.format("Strings table:\n");

        for (String str: this.keySet()) {
            String value = this.get(str);
            f.format("Entry -- %s\n", value);
        }

		f.close();

		return sb.toString();
    }
}