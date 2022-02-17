package tables;

import java.util.Formatter;

public class StringTable extends Table<StringEntry> {
    protected void getString(Formatter formatter, int position) {
        formatter.format(
            "Entry[%d] -- %s\n",
            position, this.get(position));
    }

    
}
