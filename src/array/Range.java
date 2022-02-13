package array;

public class Range {
    private int lowerLimit;
    private int upperLimit;

    public Range(int lowerLimit, int upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    @Override
    public String toString() {
        return lowerLimit + ".." + upperLimit;
    }
}
