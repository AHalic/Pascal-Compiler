package tables;

import typing.Type;
import array.Array;
import java.util.Formatter;

public class VariableTable extends Table<Entry> {

    public int add(String name, int line, Type type) {
        tables.Entry entry = new tables.Entry(name, line, type);
        return this.add(entry);
    }

    protected void getString(Formatter formatter, int position) {
        formatter.format(
                "Entry[%d] -- name: %s, line: %d, type: %s",
                position, getName(position), getLine(position), getType(position));

            // Se for um array, imprime o tipo
            if (getType(position) == Type.ARRAY_TYPE) {
                formatter.format("_" + ((Array)get(position)).getComponentType());
            }

            formatter.format("\n");
    }
}
