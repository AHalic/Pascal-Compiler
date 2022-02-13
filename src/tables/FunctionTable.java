package tables;

import java.util.HashMap;
import java.util.List;
import typing.Type;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Formatter;

public final class FunctionTable extends HashMap<Integer, List<Function>> {
    private HashMap<String, Integer> indexes = new HashMap<String, Integer>();
    private int current = 0;

    public FunctionTable() {
        // Cria as funções built-in
       this.addBuiltInFunctionOverloaded("write");
       this.addBuiltInFunctionOverloaded("writeln");
       this.addBuiltInFunctionOverloaded("read");
       this.addBuiltInFunctionOverloaded("readln");
       this.addBuiltIn("break", Type.NO_TYPE);
    }

    private void addBuiltInFunctionOverloaded(String name) {
        int idx = this.addBuiltIn(name, Type.NO_TYPE);
        this.get(idx).get(0).addVariable("message", Type.STR_TYPE);
        //
        idx = this.addBuiltIn(name, Type.NO_TYPE);
        this.get(idx).get(1).addVariable("int_value", Type.INT_TYPE);
        //
        idx = this.addBuiltIn(name, Type.NO_TYPE);
        this.get(idx).get(1).addVariable("real_value", Type.REAL_TYPE);
        //
        idx = this.addBuiltIn(name, Type.NO_TYPE);
        this.get(idx).get(2).addVariable("boolean_value", Type.BOOL_TYPE);
        //
        idx = this.addBuiltIn(name, Type.NO_TYPE);
    }

    public Function get(String name) {
        int idx = this.indexes.get(name);
        return this.get(idx).get(0); 
    }

    public int addBuiltIn(String name, Type type) {
        Function function = new Function(name, -1, type, true);
        function.addVariable(name, type, false);
        return this.put(function);
    }

    public int getParameterQuantity(String name) {
        int idx = this.indexes.get(name);
        return this.get(idx).get(0).getParameterQuantity();
    }

    public int put(Function function) {
        if (this.indexes.containsKey(function.getName())) {
            int idx = this.indexes.get(function.getName());
            this.get(idx).add(function);
            return idx;
        } else {
            List<Function> list = new ArrayList<>();
            list.add(function);
            this.put(current, list);
            this.indexes.put(function.getName(), current);
            return this.current++;
        }
    }

    public VariableTable getVariableTable(int index) {
        return this.get(index).get(0).getVariableTable();
    }

    public VariableTable getVariableTable(String name) {
        int idx = this.indexes.get(name);
        return this.getVariableTable(idx);
    }

    public VariableTable getVariableTable() {
        return this.getVariableTable(this.current - 1);
    }

    public int addVarInLastFunction(String name, Type type) {
        return this.get(this.current - 1).get(0).addVariable(name, type);
    }

    public int addVarInLastFunction(String name, Type type, boolean param) {
        return this.get(this.current - 1).get(0).addVariable(name, type, param);
    }

    public int getIndex(String string) {
        return this.indexes.get(string);
    }

    public boolean contains(String string) {
        return this.indexes.containsKey(string);
    }

    public String getName(int index) {
        return this.get(index).get(0).getName();
    }

    public String getName() {
        return this.getName(this.current - 1);
    }

    public int getLine(int index) {
        return this.get(index).get(0).getLine();
    }

    public Type getType(int index) {
        return this.get(index).get(0).getType();
    }

    public Type getType(String name) {
        int idx = this.indexes.get(name);
        return this.get(idx).get(0).getType();
    }


    public List<Function> getOverloadedFunctions(String name) {
        int idx = this.indexes.get(name);
        return this.get(idx);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("Function table:\n");

        for (int i = 0; i < this.size(); i++) {
            // Não imprime as funções built-in
            if (!get(i).get(0).builtIn) {
                formatter.format(
                "Function %d -- name: %s, line: %d, type: %s\n",
                i, getName(i), getLine(i), getType(i).toString());
            }
        }

        formatter.close();

        return stringBuilder.toString();
    }
}
