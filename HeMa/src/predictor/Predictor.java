package predictor;

import com.github.javaparser.ast.body.MethodDeclaration;

public class Predictor {
    public static int methodCount = 0;
    public static int predictedMethods = 0;
    public static float correctMethods = 0;

    public static void predict(MethodDeclaration method) {
        methodCount++;

        if (GetterPredictor.predict(method) > -1)
            return;
        if (SetterPredictor.predict(method) > -1)
            return;
        if (DelegationPredictor.predict(method) > -1)
            return;
        SignaturePredictor.predict(method);
    }

    public static void printResults() {
        predictedMethods = GetterPredictor.predicted + SetterPredictor.predicted + DelegationPredictor.predicted
                + SignaturePredictor.predicted;
        correctMethods = GetterPredictor.correct + SetterPredictor.correct + DelegationPredictor.correct
                + SignaturePredictor.correct;

        System.out.println("total = " + methodCount);
        System.out.println("predicted = " + predictedMethods);
        System.out.println("correct = " + correctMethods);
        System.out.println("precision = " + correctMethods * 1.0 / predictedMethods);
        System.out.println("recall = " + correctMethods * 1.0 / methodCount);
    }
}
