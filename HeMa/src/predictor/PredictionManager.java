package predictor;

import com.github.javaparser.ast.body.MethodDeclaration;

public class PredictionManager {
    public static int methodCount = 0;
    public static int predictedMethods = 0;
    public static float correctMethods = 0;

    /* Predictors */
    GetterPredictor getterPredictor = new GetterPredictor();
    SetterPredictor setterPredictor = new SetterPredictor();
    DelegationPredictor delegationPredictor = new DelegationPredictor();
    SignaturePredictor signaturePredictor = new SignaturePredictor();

    public void predict(MethodDeclaration method) {
        methodCount++;

        if (getterPredictor.predict(method)) return;
        if (setterPredictor.predict(method)) return;
        if (delegationPredictor.predict(method)) return;
        signaturePredictor.predict(method);
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
