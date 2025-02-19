package prediction;

import JavaExtractor.MethodAST;
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
    TestPredictor testPredictor = new TestPredictor();                      // New Predictor
    OverriddenPredictor overriddenPredictor = new OverriddenPredictor();    // New Predictor
    GetterPredictor getterPredictor = new GetterPredictor();
    SetterPredictor setterPredictor = new SetterPredictor();
    DelegationPredictor delegationPredictor = new DelegationPredictor();
    ReturnNamePredictor returnNamePredictor = new ReturnNamePredictor();
    SignaturePredictor signaturePredictor = new SignaturePredictor();

    public void predict(MethodDeclaration method, Path path, MethodAST ast) {
        methodCount++;

        if (testPredictor.run(method, path, ast)) return;                   // New Predictor
        if (overriddenPredictor.run(method, path, ast)) return;             // New Predictor
        if (getterPredictor.run(method, path, ast)) return;
        if (setterPredictor.run(method, path, ast)) return;
        if (delegationPredictor.run(method, path, ast)) return;
//        if (returnNamePredictor.run(method, path, ast)) return;     // New Predictor (not sure which position
                                                                      // is most effective; before/after signature
                                                                      // predictor, or instead of it)
        if (signaturePredictor.run(method, path, ast)) return;

        // If no predictions were made, record in output CSV
        TokenizedName reference = new TokenizedName(Tokenizer.tokenize(method.getNameAsString()));
        Score score = UpdatedScore.updateScore(reference, null);
        Recorder.save(reference.toString(), "-", "-", score, path, ast);
    }
}
