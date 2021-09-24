package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import model.Score;
import model.TokenizedName;
import util.Recorder;
import util.Tokenizer;
import util.score.UpdatedScore;

import java.nio.file.Path;

public class PredictionManager {
    public static int methodCount = 0;
    public static int predictedMethods = 0;

    /* Predictors */
    GetterPredictor getterPredictor = new GetterPredictor();
    SetterPredictor setterPredictor = new SetterPredictor();
    DelegationPredictor delegationPredictor = new DelegationPredictor();
    OverriddenPredictor overriddenPredictor = new OverriddenPredictor();
    SignaturePredictor signaturePredictor = new SignaturePredictor();

    public void predict(MethodDeclaration method, Path path) {
        methodCount++;

        if (getterPredictor.run(method, path)) return;
        if (setterPredictor.run(method, path)) return;
        if (delegationPredictor.run(method, path)) return;
        if (overriddenPredictor.run(method, path)) return;
        if (signaturePredictor.run(method, path)) return;

        // If no predictions were made, record in output CSV
        TokenizedName reference = new TokenizedName(Tokenizer.tokenize(method.getNameAsString()));
        Score score = UpdatedScore.updateScore(reference, null);
        Recorder.save(reference.toString(), "-", "-", score, path);
    }
}
