package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.VoidType;
import util.Tokenizer;

import java.nio.file.Path;
import java.util.List;

public class ReturnNamePredictor extends Predictor {

    protected ReturnNamePredictor() {
        super("RETURN NAME");
    }

    @Override
    String predict(MethodDeclaration method, Path path) {
        // Check method does not return void
        if (method.getType() instanceof VoidType) return null;

        // Check method returns a single value
        List<ReturnStmt> returnStmts = method.getNodesByType(ReturnStmt.class);
        if (returnStmts.size() != 1) return null;

        // Check the returned value is declared in the enclosing class
        ReturnStmt returnStmt = returnStmts.get(0);
        if (returnStmt.getExpression().isEmpty()) return null;
        Expression expr = returnStmt.getExpression().get();

        if (expr.isNameExpr()) {
            System.out.println(method.getName() + ", predict: " + expr.asNameExpr().getNameAsString());
            return Tokenizer.tokenize(expr.asNameExpr().getNameAsString()).toLowerCase();
        }

        return null;
    }
}
