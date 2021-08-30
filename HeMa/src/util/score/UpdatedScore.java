package util.score;

import model.Score;
import model.TokenizedName;
import prediction.PredictionManager;

import java.util.HashSet;

public class UpdatedScore {
    public static double truePositive = 0;
    public static double falsePositive = 0;
    public static double falseNegative = 0;

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

        return new Score(tp, fp, fn);
    }

    public static void printResults() {
        double precision = 0, recall = 0, f1 = 0;

        // Calculate precision, recall and f1 scores, checking for a zero division error
        if (truePositive + falsePositive != 0) precision = truePositive / (truePositive + falsePositive);
        if (truePositive + falseNegative != 0) recall = truePositive / (truePositive + falseNegative);
        if (precision + recall != 0) f1 = 2 * precision * recall / (precision + recall);

        System.out.println("\n---------- Updated Score ----------");
        System.out.println("total = " + PredictionManager.methodCount);
        System.out.println("predicted = " + PredictionManager.predictedMethods);
        System.out.println("precision = " + precision);
        System.out.println("recall = " + recall);
        System.out.println("F1 = " + f1);
    }
}
