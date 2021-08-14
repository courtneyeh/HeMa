package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import model.Signature;
import model.TrainSet;
import util.Tokenizer;

import java.util.Map;
import java.util.Map.Entry;

public class SignaturePredictor extends Predictor {

    public SignaturePredictor() {
        super("SIGNATURE");
    }

    @Override
    public String predict(MethodDeclaration node) {
        Signature signature = new Signature(node);

        // Predict based on signature of training set
        if (TrainSet.getData().containsKey(signature)) {
            Map<String, Integer> counter = TrainSet.getData().get(signature);
            int max = 0;
            String prediction = "";
            for (Entry<String, Integer> entry : counter.entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    prediction = entry.getKey();
                }
            }
            if (max > 0) {
                prediction = Tokenizer.tokenize(prediction).toLowerCase();
                return prediction;
            }
        }

        return null;
    }
}
