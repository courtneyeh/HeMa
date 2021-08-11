package prediction;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;

import java.util.List;

public abstract class GetterSetterPredictor extends Predictor {

    public GetterSetterPredictor(String type) {
        super(type);
    }

    String getPrediction(Expression expression) {
        if (expression instanceof FieldAccessExpr) {
            return ((FieldAccessExpr) expression).getField();
        } else if (expression instanceof NameExpr) {
            return ((NameExpr) expression).getName();
        }

        return null;
    }

    Boolean returnedDeclaredClass(MethodDeclaration method, String prediction) {
        // Check returned value declared in enclosing class
        FieldDeclaration field = null;
        ClassOrInterfaceDeclaration parent = method.getParentNodeOfType(ClassOrInterfaceDeclaration.class);
        if (parent != null) {
            List<FieldDeclaration> fieldDeclarations = parent.getFields();
            for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
                if (validDeclaration(method, fieldDeclaration, prediction)) {
                    field = fieldDeclaration;
                    break;
                }
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
