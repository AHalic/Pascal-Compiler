package checker;

public enum Scope {
    PROGRAM(0),
    IF(1),
    WHILE(2),
    FUNCTION(3);

    private int level;

    Scope(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}
