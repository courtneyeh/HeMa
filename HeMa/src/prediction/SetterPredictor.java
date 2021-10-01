package prediction;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import util.Tokenizer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SetterPredictor extends GetterSetterPredictor {

    SetterPredictor() {
        super("SETTER");
    }

    @Override
    public String predict(MethodDeclaration method, Path path) {
        // Check there is a parameter
        if (method.getParameters().size() == 0) return null;

        // Check there is a single assignment
        if (!method.getBody().isPresent()) return null;
        List<AssignExpr> assignExprs = new ArrayList<>(method.getBody().get().getNodesByType(AssignExpr.class));
        if (assignExprs.size() != 1) return null;

        // Get the prediction
        AssignExpr assignExpr = assignExprs.get(0);
        Expression targetExpr = assignExpr.getTarget();
        Expression valueExpr = assignExpr.getValue();

        String prediction = getPrediction(targetExpr);
        if (prediction == null) return null;

        // Check the value is an assigned method parameter
        String valueName;
        if (valueExpr.isNameExpr()) {
            valueName = valueExpr.asNameExpr().getNameAsString();
        } else {
            return null;
        }

        List<String> parameters = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            parameters.add(parameter.getNameAsString());
        }

        if (!parameters.contains(valueName)) return null;

        // Check assignment is assigned to field declared within the class
        if (!returnedDeclaredClass(method, prediction)) return null;

        prediction = Tokenizer.tokenize(prediction).toLowerCase();

        if (prediction.startsWith("is ")) {
            prediction = prediction.replaceFirst("is ", "set ");
        } else if (prediction.startsWith("m ")) {
            prediction = prediction.replaceFirst("m ", "set ");
        } else {
            prediction = "set " + prediction;
        }

        return prediction;
    }

    @Override
    boolean validDeclaration(MethodDeclaration methodDeclaration, FieldDeclaration fieldDeclaration, String prediction) {
        return fieldDeclaration.getVariables().get(0).getNameAsString().equals(prediction);
    }
}
