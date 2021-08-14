package prediction;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;

import java.util.List;
import java.util.Optional;

public abstract class GetterSetterPredictor extends Predictor {

    public GetterSetterPredictor(String type) {
        super(type);
    }

    String getPrediction(Expression expression) {
        if (expression.isFieldAccessExpr()) {
            return expression.asFieldAccessExpr().getNameAsString();
        } else if (expression.isNameExpr()) {
            return expression.asNameExpr().getNameAsString();
        }

        return null;
    }

    Boolean returnedDeclaredClass(MethodDeclaration method, String prediction) {
        // Check returned value declared in enclosing class
        FieldDeclaration field = null;

        if (!method.getParentNode().isPresent()) return null;
        Node parentNode = method.getParentNode().get();

        if (!(parentNode instanceof ClassOrInterfaceDeclaration)) return null;

        ClassOrInterfaceDeclaration parent = (ClassOrInterfaceDeclaration) parentNode;
        List<FieldDeclaration> fieldDeclarations = parent.getFields();
        for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
            if (validDeclaration(method, fieldDeclaration, prediction)) {
                field = fieldDeclaration;
                break;
            }
        }

        // Return whether the field found in the class
        return field != null;
    }

    /**
     * @return the boolean representation of a check for a valid field declaration
     */
    abstract boolean validDeclaration(MethodDeclaration methodDeclaration, FieldDeclaration fieldDeclaration,
                                      String prediction);
}
