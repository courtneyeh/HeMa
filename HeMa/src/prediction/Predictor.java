package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import util.Recorder;
import util.Tokenizer;

public abstract class Predictor {
    final String TYPE;
    int predicted = 0;
    int correct = 0;

    protected Predictor(String type) {
        TYPE = type;
    }

    boolean run(MethodDeclaration method) {
        String prediction = predict(method);
        if (prediction == null) return false;

        String reference = Tokenizer.tokenize(method.getNameAsString()).toLowerCase();

        // Updates counts
        predicted++;
        correct += reference.equals(prediction) ? 1 : 0;

        // Records prediction in CSV
        Recorder.save(reference, prediction, TYPE);

        return true;
    }

    /**
     * Predicts the name of the method
     *
     * @param method declaration
     * @return String representation of the prediction, null if no prediction was made
     */
    abstract String predict(MethodDeclaration method);
}
