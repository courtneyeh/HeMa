package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;

public abstract class Predictor {
    int predicted = 0;
    int correct = 0;

    /**
     * Predicts the name of the method
     * @param method declaration
     * @return boolean representation of whether a prediction was successfully made
     */
    abstract boolean predict(MethodDeclaration method);
}
