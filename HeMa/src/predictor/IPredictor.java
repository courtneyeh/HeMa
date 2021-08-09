package predictor;

import com.github.javaparser.ast.body.MethodDeclaration;

public interface IPredictor {
    /**
     * Predicts the name of the method
     * @param method declaration
     * @return boolean representation of whether a prediction was successfully made
     */
    boolean predict(MethodDeclaration method);
}
