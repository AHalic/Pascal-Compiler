package array;

public class Range {
    public final int lowerLimit;
    public final int upperLimit;

    public Range(int lowerLimit, int upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    @Override
    public String toString() {
        return lowerLimit + ".." + upperLimit;
    }
}
