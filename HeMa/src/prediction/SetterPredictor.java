package prediction;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import util.Recorder;
import util.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class SetterPredictor extends GetterSetterPredictor {

    SetterPredictor() {
        super("SETTER");
    }

    @Override
    public boolean predict(MethodDeclaration method) {
        // Check there is a parameter
        if (method.getParameters().size() == 0) return false;

        // Check there is a single assignment
        List<AssignExpr> assignExprs = new ArrayList<>();
        for (AssignExpr assignExpr : method.getBody().getNodesByType(AssignExpr.class)) {
            if (assignExpr.getParentNodeOfType(MethodDeclaration.class).equals(method)) assignExprs.add(assignExpr);
        }

        if (assignExprs.size() != 1) return false;

        // Get the prediction
        AssignExpr assignExpr = assignExprs.get(0);
        Expression targetExpr = assignExpr.getTarget();
        Expression valueExpr = assignExpr.getValue();

        String prediction = getPrediction(targetExpr);
        if (prediction == null) return false;

        // Check the value is an assigned method parameter
        String valueName;
        if (valueExpr instanceof NameExpr) {
            valueName = ((NameExpr) valueExpr).getName();
        } else {
            return false;
        }

        List<String> parameters = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            parameters.add(parameter.getId().getName());
        }

        if (!parameters.contains(valueName)) return false;

        // Check assignment is assigned to field declared within the class
        if (!returnedDeclaredClass(method, prediction)) return false;

        predicted++;
        String reference = Tokenizer.tokenize(method.getName()).toLowerCase();
        prediction = Tokenizer.tokenize(prediction).toLowerCase();

        if (prediction.startsWith("is ")) {
            prediction = prediction.replaceFirst("is ", "set ");
        } else if (prediction.startsWith("m ")) {
            prediction = prediction.replaceFirst("m ", "set ");
        } else {
            prediction = "set " + prediction;
        }

        correct += reference.equals(prediction) ? 1 : 0;

        Recorder.save(reference, prediction, TYPE);
        return true;
    }

    @Override
    boolean validDeclaration(MethodDeclaration methodDeclaration, FieldDeclaration fieldDeclaration, String prediction) {
        return fieldDeclaration.getVariables().get(0).getId().getName().equals(prediction);
    }
}
