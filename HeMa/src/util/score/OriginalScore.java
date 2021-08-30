package util.score;

import prediction.PredictionManager;

public class OriginalScore {
    private static int correctMethods = 0;

    public static void updateScore(String reference, String prediction) {
        correctMethods += reference.equals(prediction) ? 1 : 0;
    }

    public static void printResults() {
        System.out.println("\n---------- Original Score ----------");
        System.out.println("total = " + PredictionManager.methodCount);
        System.out.println("predicted = " + PredictionManager.predictedMethods);
        System.out.println("correct = " + correctMethods);
        System.out.println("precision = " + correctMethods * 1.0 / PredictionManager.predictedMethods);
        System.out.println("recall = " + correctMethods * 1.0 / PredictionManager.methodCount);
    }
}
