package tables;

import java.util.List;
import java.util.ArrayList;

import typing.Type;

public class FunctionWrapper extends Entry {
    protected boolean builtIn;
    private List<Function> overloaded = new ArrayList<>();

    public FunctionWrapper(String name, int line, Type type) {
        super(name, line, type);
        this.builtIn = false;
    }

    public FunctionWrapper(String name, int line, Type type, boolean isbuiltIn) {
        super(name, line, type);
        this.builtIn = isbuiltIn;
    }

    public int addVariableInLastFunction(String name, Type type, boolean param) {
        int idx = this.overloaded.size() - 1;
        return this.overloaded.get(idx).addVariable(name, line, type, param);
    }

    public VariableTable getVariableTable(int position) {
        return this.overloaded.get(position).getVariableTable();
    }

    public List<Function> getFunctions() {
        return this.overloaded;
    }

    public Function getOverloaded(int position) {
        return this.overloaded.get(position);
    }

    public VariableTable getLastVariableTable() {
        return this.overloaded.get(this.overloaded.size() - 1).getVariableTable();
    }

    public StringTable getLastStringTable() {
        return this.overloaded.get(this.overloaded.size() - 1).getStringTable();
    }

    public int addOverloaded(Function function) {
        int idx = this.overloaded.size();
        this.overloaded.add(function);
        return idx;
    }
}
