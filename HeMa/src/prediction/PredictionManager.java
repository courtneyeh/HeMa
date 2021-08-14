package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import util.Recorder;
import util.Tokenizer;

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

        if (getterPredictor.run(method)) return;
        if (setterPredictor.run(method)) return;
        if (delegationPredictor.run(method)) return;
        if (signaturePredictor.run(method)) return;

        // If no predictions were made, record in output CSV
        Recorder.save(Tokenizer.tokenize(method.getNameAsString()).toLowerCase(), "-", "-");
    }

    public void printResults() {
        predictedMethods = getterPredictor.predicted + setterPredictor.predicted + delegationPredictor.predicted
                + signaturePredictor.predicted;
        correctMethods = getterPredictor.correct + setterPredictor.correct + delegationPredictor.correct
                + signaturePredictor.correct;

        System.out.println("total = " + methodCount);
        System.out.println("predicted = " + predictedMethods);
        System.out.println("correct = " + correctMethods);
        System.out.println("precision = " + correctMethods * 1.0 / predictedMethods);
        System.out.println("recall = " + correctMethods * 1.0 / methodCount);
    }
}
