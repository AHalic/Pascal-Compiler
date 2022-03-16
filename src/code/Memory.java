package code;

import java.util.Vector;

import tables.VariableTable;

@SuppressWarnings("serial")
public final class Memory extends Vector<Word> {

    public Memory(VariableTable vt) {
        for (int i = 0; i < vt.size(); i++) {
            this.add(Word.fromInt(0));
        }
    }
    
    public void storei(int addr, int value) {
        this.set(addr, Word.fromInt(value));
    }
    
    public int loadi(int addr) {
        return this.get(addr).toInt();
    }
    
    public void storef(int addr, float value) {
        this.set(addr, Word.fromFloat(value));
    }
    
    public float loadf(int addr) {
        return this.get(addr).toFloat();
    }
    
}
