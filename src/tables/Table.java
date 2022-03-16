package tables;

import java.util.HashMap;
import java.util.Formatter;

import typing.Type;

abstract public class Table<T extends tables.Entry> extends HashMap<Integer, T> {
    protected int currentID = 0;
    protected HashMap<String, Integer> ID_ = new HashMap<String, Integer>();
    
    //
    public Integer add(T key) {
        if (this.ID_.containsKey(key.name)) {
            return this.ID_.get(key.name);
        } else {
            this.put(currentID, key);
            this.ID_.put(key.name, currentID);
            return currentID++;
        }
    }

    //
    public int size() {
        return this.ID_.size();
    }

    //
    public T get(String name) {
        int idx = this.ID_.get(name);
        return this.get(idx);
    }

    //
    public boolean contains(String name) {
        return this.ID_.containsKey(name);
    }

    //
    public int getIndex(String name) {
        return this.ID_.get(name);
    }

    //
    public Type getType(int position) {
        return this.get(position).getType();
    }

    //
    public int getLine(int position) {
        return this.get(position).getLine();
    }

    //
    public String getName(int position) {
        return this.get(position).getName();
    }

    //
    abstract protected void getString(Formatter formatter, int position);

    //
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);
        formatter.format(this.getClass().getSimpleName() + ":\n");

        for (int i = 0; i < this.size(); i++) {
            getString(formatter, i);
        }

        formatter.close();
        return stringBuilder.toString();
    }
}
