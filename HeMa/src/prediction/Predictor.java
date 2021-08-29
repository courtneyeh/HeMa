package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import model.Score;
import model.TokenizedName;
import util.Recorder;
import util.Tokenizer;

public abstract class Predictor {
    final String TYPE;
    int predicted = 0;

    protected Predictor(String type) {
        TYPE = type;
    }

    boolean run(MethodDeclaration method) {
        String predictionString = predict(method);
        if (predictionString == null) return false;

        TokenizedName prediction = new TokenizedName(predictionString);
        TokenizedName reference = new TokenizedName(Tokenizer.tokenize(method.getNameAsString()));

        // Updates counts
        Score score = prediction.score(reference);
        predicted++;

        // Records prediction in CSV
        Recorder.save(reference.toString(), prediction.toString(), TYPE, score);

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
