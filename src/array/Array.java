package array;

import java.util.ArrayList;
import java.util.List;

import tables.Entry;

import typing.Type;
// import static typing.Type.*;

public class Array extends Entry {
    private List<Range> ranges = new ArrayList<Range>();
    private Type componentType;

    public Array(String name, int line, Type type, Type componentType) {
        super(name, line, type);
        this.componentType = componentType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getComponentType() {
        return this.componentType;
    }

    public void addRange(Range range) {
        this.ranges.add(range);
    }

    public String getRangeString() {
        String text = "(";

        for (int i = 0; i < ranges.size(); i++) {
            text += ranges.get(i).toString();
            if (i != ranges.size() - 1) text += ", ";
        }

        return text + ")";
    }

    @Override
    public String toString() {
        String text = "Array[ TYPE=" + this.type + ", RANGE=";
        return text + this.getRangeString() + "]";
    }
}
