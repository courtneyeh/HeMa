package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import model.Score;
import model.TokenizedName;
import util.Recorder;
import util.Tokenizer;
import util.score.UpdatedScore;

public class PredictionManager {
    public static int methodCount = 0;
    public static int predictedMethods = 0;

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
        Score score = UpdatedScore.updateScore(reference, null);
        Recorder.save(reference.toString(), "-", "-", score);
    }
}
