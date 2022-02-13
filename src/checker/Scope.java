package checker;

public enum Scope {
    PROGRAM(0),
    IF_SCOPE(1),
    WHILE_SCOPE(2);

    private int level;

    Scope(int level) {
        this.level = level;
    }
}
