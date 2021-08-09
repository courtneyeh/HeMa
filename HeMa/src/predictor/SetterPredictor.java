package predictor;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import util.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class SetterPredictor {
    public static int predicted = 0;
    public static int correct = 0;

    public static int predict(MethodDeclaration node) {
        if (node.getParameters().size() > 0) {
            String prediction = predictSetter(node);
            if (prediction != null) {
                predicted++;
                String reference = Tokenizer.tokenize(node.getName()).toLowerCase();
                prediction = Tokenizer.tokenize(prediction).toLowerCase();

                if (prediction.startsWith("is ")) {
                    prediction = prediction.replaceFirst("is ", "set ");
                } else if (prediction.startsWith("m ")) {
                    prediction = prediction.replaceFirst("m ", "set ");
                } else {
                    prediction = "set " + prediction;
                }

                int precision = reference.equals(prediction) ? 1 : 0;
                correct += precision;
                return precision;
            }
        }

        return -1;
    }

    private static String predictSetter(MethodDeclaration node) {
        // Check there is a parameter
        if (node.getParameters().size() == 0) {
            return null;
        }

        List<AssignExpr> assignExprs = new ArrayList<>();
        for (AssignExpr assignExpr : node.getBody().getNodesByType(AssignExpr.class)) {
            if (assignExpr.getParentNodeOfType(MethodDeclaration.class).equals(node)) {
                assignExprs.add(assignExpr);
            }
        }

        if (assignExprs.size() == 0) {
            return null;
        }

        if (assignExprs.size() > 1) {
            return null;
        }

        AssignExpr assignExpr = assignExprs.get(0);
        Expression targetExpr = assignExpr.getTarget();
        Expression valueExpr = assignExpr.getValue();

        String rightName = "";
        if (valueExpr instanceof NameExpr) {
            rightName = ((NameExpr) valueExpr).getName();
        } else {
            return null;
        }

        List<String> parameters = new ArrayList<>();
        for (Parameter parameter : node.getParameters()) {
            parameters.add(parameter.getId().getName());
        }
        if (!parameters.contains(rightName)) {
            return null;
        }

        String prediction = "";
        if (targetExpr instanceof FieldAccessExpr) {
            prediction = ((FieldAccessExpr) targetExpr).getField();
        } else if (targetExpr instanceof NameExpr) {
            prediction = ((NameExpr) targetExpr).getName();
        } else {
            return null;
        }

        FieldDeclaration field = null;
        ClassOrInterfaceDeclaration parent = node.getParentNodeOfType(ClassOrInterfaceDeclaration.class);
        if (parent != null) {
            List<FieldDeclaration> fieldDeclarations = parent.getFields();
            for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
                if (fieldDeclaration.getVariables().get(0).getId().getName().equals(prediction)) {
                    field = fieldDeclaration;
                    break;
                }
            }
        }

        return field != null ? prediction : null;
    }
}
