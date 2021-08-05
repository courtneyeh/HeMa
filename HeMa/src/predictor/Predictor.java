package predictor;

import com.github.javaparser.ast.body.MethodDeclaration;
import predictor.DelegationPredictor;
import predictor.GetterPredictor;
import predictor.SetterPredictor;
import predictor.SignaturePredictor;
import util.Counter;

import java.util.List;

public class Predictor {
    public static void predict(List<MethodDeclaration> nodes) {
        for (MethodDeclaration node : nodes) {
            Counter.total++;

            if (GetterPredictor.predict(node) > -1)
                continue;
            if (SetterPredictor.predict(node) > -1)
                continue;
            if (DelegationPredictor.predict(node) > -1)
                continue;
            SignaturePredictor.predict(node);
        }
    }
}
