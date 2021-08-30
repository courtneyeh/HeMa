package util.score;

import model.Score;
import model.TokenizedName;
import prediction.PredictionManager;

import java.util.HashSet;

public class UpdatedScore {
    public static double truePositive = 0;
    public static double falsePositive = 0;
    public static double falseNegative = 0;

    public static double normalizedTruePositive = 0;
    public static double normalizedFalsePositive = 0;
    public static double normalizedFalseNegative = 0;

    public static Score updateScore(TokenizedName reference, TokenizedName prediction) {
        if (prediction == null) {
            int fn = reference.tokens.size();
            falseNegative += fn;
            return new Score(0, 0, fn);
        }

        int tp = 0;
        int fp = 0;
        int fn = 0;

        HashSet<String> refTokens = reference.tokens;

        for (String predToken : prediction.tokens) {
            if (refTokens.contains(predToken)) tp++;
            else fp++;
        }

        for (String refToken : refTokens) {
            if (!prediction.tokens.contains(refToken)) fn++;
        }

        truePositive += tp;
        falsePositive += fp;
        falseNegative += fn;

        normalizedTruePositive += (double) tp / prediction.tokens.size();
        normalizedFalsePositive += (double) fp / prediction.tokens.size();
        normalizedFalseNegative += (double) fn / reference.tokens.size();

        return new Score(tp, fp, fn);
    }

    public static void printResults() {
        // Calculate precision, recall and f1 scores, checking for a zero division error
        double precision = 0, recall = 0, f1 = 0;
        if (truePositive + falsePositive != 0) precision = truePositive / (truePositive + falsePositive);
        if (truePositive + falseNegative != 0) recall = truePositive / (truePositive + falseNegative);
        if (precision + recall != 0) f1 = 2 * precision * recall / (precision + recall);

        System.out.println("\n---------- Updated Score ----------");
        System.out.println("precision = " + precision);
        System.out.println("recall = " + recall);
        System.out.println("F1 = " + f1);

        // Calculate normalized precision, recall and f1 scores, checking for a zero division error
        double normalizedPrecision = 0, normalizedRecall = 0, normalizedF1 = 0;
        if (normalizedTruePositive + normalizedFalsePositive != 0)
            normalizedPrecision = normalizedTruePositive / (normalizedTruePositive + normalizedFalsePositive);
        if (normalizedTruePositive + normalizedFalseNegative != 0)
            normalizedRecall = normalizedTruePositive / (normalizedTruePositive + normalizedFalseNegative);
        if (normalizedPrecision + normalizedRecall != 0)
            normalizedF1 = 2 * normalizedPrecision * normalizedRecall / (normalizedPrecision + normalizedRecall);

        System.out.println("\n---------- Normalized Score ----------");
        System.out.println("precision = " + normalizedPrecision);
        System.out.println("recall = " + normalizedRecall);
        System.out.println("F1 = " + normalizedF1);
    }
}
