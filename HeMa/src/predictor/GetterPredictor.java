package predictor;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.VoidType;
import util.Counter;
import util.Tokenizer;

import java.util.List;

public class GetterPredictor {

    public static int predict(MethodDeclaration node) {
        if (!(node.getElementType() instanceof VoidType)) {
            String prediction = predictGetter(node);
            if (prediction != null) {
                Counter.gPredicted++;
                String reference = Tokenizer.tokenize(node.getName()).toLowerCase();

                prediction = Tokenizer.tokenize(prediction).toLowerCase();
                if (node.getElementType() instanceof PrimitiveType &&
                        ((PrimitiveType) node.getElementType()).getType().name().equals("Boolean")) {
                    if (!prediction.startsWith("is "))
                        prediction = "is " + prediction;
                } else if (prediction.startsWith("m ")) {
                    prediction = prediction.replaceFirst("m ", "get ");
                } else {
                    prediction = "get " + prediction;
                }

                int precision = reference.equals(prediction) ? 1 : 0;
                Counter.gCorrect += precision;
                return precision;
            }
        }
        return -1;
    }

    private static String predictGetter(MethodDeclaration node) {
        if (node.getElementType() instanceof VoidType) {
            return null;
        }

        List<ReturnStmt> returnStmts = node.getNodesByType(ReturnStmt.class);
        if (returnStmts.size() == 0) {
            return null;
        }

        if (returnStmts.size() > 1) {
            return null;
        }

        ReturnStmt returnStmt = returnStmts.get(returnStmts.size() - 1);
        Expression expr = returnStmt.getExpr();

        String returnName = "";
        if (expr instanceof FieldAccessExpr) {
            returnName = ((FieldAccessExpr) expr).getField();
        } else if (expr instanceof NameExpr) {
            returnName = ((NameExpr) expr).getName();
        } else {
            return null;
        }

        FieldDeclaration field = null;
        ClassOrInterfaceDeclaration parent = node.getParentNodeOfType(ClassOrInterfaceDeclaration.class);
        if (parent != null) {
            List<FieldDeclaration> fieldDeclarations = parent.getFields();
            for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
                if (fieldDeclaration.getVariables().get(0).getId().getName().equals(returnName)) {
                    if (fieldDeclaration.getElementType().toString().equals(node.getElementType().toString())) {
                        field = fieldDeclaration;
                        break;
                    }
                }
            }
        }

        if (field == null) {
            return null;
        }

        return returnName;
    }
}
