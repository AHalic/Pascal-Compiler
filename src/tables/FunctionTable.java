package tables;

import java.util.List;
import typing.Type;
import java.util.Formatter;

public class FunctionTable extends Table<FunctionWrapper> {
    //
    public Integer add(Function function) {
        if (this.ID_.containsKey(function.name)) {
            int idx = this.ID_.get(function.name);
            this.get(idx).addOverloaded(function);
            return idx;
        } else {
            FunctionWrapper wrapper = new FunctionWrapper(
                function.name,
                function.line,
                Type.NO_TYPE);
            wrapper.addOverloaded(function);
            this.put(currentID, wrapper);
            this.ID_.put(function.name, currentID);
            return currentID++;
        }
    }

    public FunctionTable(boolean builtIn) {
        super();
        
        // Cria as funções built-in
        if (builtIn) {
           this.addBuiltInFunctionOverloaded("write");
           this.addBuiltInFunctionOverloaded("writeln");
           this.addBuiltInFunctionOverloaded("read");
           this.addBuiltInFunctionOverloaded("readln");
           this.addBuiltIn("break", Type.NO_TYPE);
        }
    }

    private void addBuiltInFunctionOverloaded(String name) {
        int idx = this.addBuiltIn(name, Type.NO_TYPE);
        this.get(idx).getOverloaded(0).addVariable("message", Type.STR_TYPE);
        //
        this.addBuiltIn(name, Type.NO_TYPE);
        this.get(idx).getOverloaded(1).addVariable("int_value", Type.INT_TYPE);
        //
        this.addBuiltIn(name, Type.NO_TYPE);
        this.get(idx).getOverloaded(2).addVariable("real_value", Type.REAL_TYPE);
        //
        this.addBuiltIn(name, Type.NO_TYPE);
        this.get(idx).getOverloaded(3).addVariable("boolean_value", Type.BOOL_TYPE);
        //
        this.addBuiltIn(name, Type.NO_TYPE);
    }

    public int addBuiltIn(String name, Type type) {
        int idx = -1;
        FunctionWrapper wrapper = null;

        if (!this.contains(name)) {
            wrapper = new FunctionWrapper(name, -1, Type.NO_TYPE, true);
            idx = this.add(wrapper);
        } else {
            wrapper = this.get(name);
            idx = this.getIndex(name);
        }

        Function function = new Function(name, -1, type);
        function.addVariable(name, -1, type, false);
        wrapper.addOverloaded(function);
        return idx;
    }

    protected void getString(Formatter formatter, int position) {
        if (!this.get(position).builtIn) {
            formatter.format(
                "Function[%d] -- name: %s, line: %d, type: %s\n",
                position, getName(position), getLine(position), getType(position));
        }
    }

    //
    public List<Function> getOverloadedFunctions(int position) {
        return this.get(position).getFunctions();
    }

    //
    public VariableTable getVariableTable(int position) {
        return this.get(position).getLastVariableTable();
    }
    
    public StringTable getStringTable(int position) {
        return this.get(position).getLastStringTable();
    }

    //
    public VariableTable getLastVariableTable() {
        return this.get(this.currentID - 1).getLastVariableTable();
    }

    public StringTable getLastStringTable(int position) {
        return this.get(position).getLastStringTable();
    }

    public StringTable getLastStringTable() {
        return this.get(this.currentID - 1).getLastStringTable();
    }

    //
    public String getLastName() {
        return this.getName(this.currentID - 1);
    }

    //
    public int addVarInLastFunction(String name, Type type, boolean parameter) {
        return this.get(this.currentID - 1).addVariableInLastFunction(name, type, parameter);
    }
}
