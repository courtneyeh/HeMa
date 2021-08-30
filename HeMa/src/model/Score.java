package model;

public class Score {
    public int truePositive;
    public int falsePositive;
    public int falseNegative;

    public Score(int truePositive, int falsePositive, int falseNegative) {
        this.truePositive = truePositive;
        this.falsePositive = falsePositive;
        this.falseNegative = falseNegative;
    }
}
