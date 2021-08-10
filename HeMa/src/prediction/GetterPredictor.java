package prediction;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.VoidType;
import util.Tokenizer;

import java.util.List;

public class GetterPredictor extends GetterSetterPredictor {

    GetterPredictor() {
        super("GETTER");
    }

    @Override
    public String predict(MethodDeclaration method) {
        // Check method does not return void
        if (method.getElementType() instanceof VoidType) return null;

        // Check method returns a single value
        List<ReturnStmt> returnStmts = method.getNodesByType(ReturnStmt.class);
        if (returnStmts.size() != 1) return null;

        // Check the returned value is declared in the enclosing class
        ReturnStmt returnStmt = returnStmts.get(0);
        Expression expr = returnStmt.getExpr();

        String prediction = getPrediction(expr);
        if (prediction == null) return null;

        // Check assignment is assigned to field declared within the class
        if (!returnedDeclaredClass(method, prediction)) return null;

        prediction = Tokenizer.tokenize(prediction).toLowerCase();

        if (method.getElementType() instanceof PrimitiveType &&
                ((PrimitiveType) method.getElementType()).getType().name().equals("Boolean")) {
            if (!prediction.startsWith("is "))
                prediction = "is " + prediction;
        } else if (prediction.startsWith("m ")) {
            prediction = prediction.replaceFirst("m ", "get ");
        } else {
            prediction = "get " + prediction;
        }

        return prediction;
    }

    @Override
    boolean validDeclaration(MethodDeclaration methodDeclaration, FieldDeclaration fieldDeclaration, String prediction) {
        return fieldDeclaration.getVariables().get(0).getId().getName().equals(prediction)
                && fieldDeclaration.getElementType().toString().equals(methodDeclaration.getElementType().toString());
    }
}
