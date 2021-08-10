package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import model.Signature;
import model.TrainSet;
import util.Tokenizer;

import java.util.Map;
import java.util.Map.Entry;

public class SignaturePredictor extends Predictor {

    @Override
    public boolean predict(MethodDeclaration node) {
        String method_name = node.getName();
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
                predicted++;
                String reference = Tokenizer.tokenize(method_name).toLowerCase();
                prediction = Tokenizer.tokenize(prediction).toLowerCase();
                correct += reference.equals(prediction) ? 1 : 0;
                return true;
            }
        }

        return false;
    }
}
