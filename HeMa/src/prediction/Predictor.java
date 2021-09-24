package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import model.Score;
import model.TokenizedName;
import util.Recorder;
import util.Tokenizer;
import util.score.OriginalScore;
import util.score.UpdatedScore;

import java.nio.file.Path;

public abstract class Predictor {
    final String TYPE;

    protected Predictor(String type) {
        TYPE = type;
    }

    boolean run(MethodDeclaration method, Path path) {
        String predictionString = predict(method);
        if (predictionString == null) return false;

        String referenceString = Tokenizer.tokenize(method.getNameAsString());

        TokenizedName prediction = new TokenizedName(predictionString);
        TokenizedName reference = new TokenizedName(referenceString);

        // Updates scores
        Score score = UpdatedScore.updateScore(reference, prediction);
        OriginalScore.updateScore(referenceString.toLowerCase(), predictionString);
        PredictionManager.predictedMethods++;

        // Records prediction in CSV
        Recorder.save(reference.toString(), prediction.toString(), TYPE, score, path);

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
