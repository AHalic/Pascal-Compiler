package tables;

import typing.Type;

public class StringEntry extends Entry {
    public StringEntry(String name, int line) {
        super(name, line, Type.STR_TYPE);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
