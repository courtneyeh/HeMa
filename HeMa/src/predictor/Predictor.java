package predictor;

import com.github.javaparser.ast.body.MethodDeclaration;
import util.Counter;

import java.util.List;

public class Predictor {
    private final List<MethodDeclaration> nodes;

    public Predictor(List<MethodDeclaration> nodes) {
        this.nodes = nodes;
    }

    public void run() {
        for (MethodDeclaration node : this.nodes) {
            Counter.total++;

            if (GetterSetterPredictor.predict(node) > -1)
                continue;
            if (ShortMPredictor.predict(node) > -1)
                continue;
            SignaturePredictor.predict(node);
        }
    }
}
