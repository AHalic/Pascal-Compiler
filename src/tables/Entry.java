package tables;

import typing.Type;

public class Entry {
    protected String name;
    protected int line;
    protected Type type;

    public Entry(String name, int line, Type type) {
        this.name = name;
        this.line = line;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public int getLine() {
        return this.line;
    }

    @Override
    public String toString() {
        return "Entry [name=" + name + ", type=" + type +
               ", line=" + line + "]";
    }
}
