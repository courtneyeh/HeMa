package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import model.Score;
import model.TokenizedName;
import util.Recorder;
import util.Tokenizer;

public class PredictionManager {
    public static int methodCount = 0;
    public static int predictedMethods = 0;

    public static double truePositive = 0;
    public static double falsePositive = 0;
    public static double falseNegative = 0;

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
        TokenizedName reference = new TokenizedName(Tokenizer.tokenize(method.getNameAsString()));
        int fn = reference.tokens.size();
        falseNegative += fn;

        Score score = new Score(0, 0, fn);
        Recorder.save(reference.toString(), "-", "-", score);
    }

    public void printResults() {
        predictedMethods = getterPredictor.predicted + setterPredictor.predicted + delegationPredictor.predicted
                + signaturePredictor.predicted;

        double precision = 0, recall = 0, f1 = 0;

        // Calculate precision, recall and f1 scores, checking for a zero division error
        if (truePositive + falsePositive != 0) precision = truePositive / (truePositive + falsePositive);
        if (truePositive + falseNegative != 0) recall = truePositive / (truePositive + falseNegative);
        if (precision + recall != 0) f1 = 2 * precision * recall / (precision + recall);

        System.out.println("\n---------- Results ----------");
        System.out.println("total = " + methodCount);
        System.out.println("predicted = " + predictedMethods);
        System.out.println("precision = " + precision);
        System.out.println("recall = " + recall);
        System.out.println("F1 = " + f1);
    }
}
