package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import util.Tokenizer;

import java.util.List;

public class DelegationPredictor extends Predictor {

    public DelegationPredictor() {
        super("DELEGATION");
    }

    @Override
    public String predict(MethodDeclaration node) {
        // Check there is a single statement
        if (!node.getBody().isPresent()) return null;

        List<Statement> stmts = node.getBody().get().getStatements();
        if (stmts.size() != 1) return null;

        // Check the single statement is an expression or return statement
        Statement stmt = stmts.get(0);
        Expression expr;
        if (stmt instanceof ExpressionStmt) {
            expr = ((ExpressionStmt) stmt).getExpression();
        } else if (stmt instanceof ReturnStmt) {
            if (!((ReturnStmt) stmt).getExpression().isPresent()) return null;
            expr = ((ReturnStmt) stmt).getExpression().get();
        } else {
            return null;
        }

        // Check single method is a method call
        if (!(expr instanceof MethodCallExpr)) return null;

        MethodCallExpr method = (MethodCallExpr) expr;
        return Tokenizer.tokenize(method.getNameAsString()).toLowerCase();
    }
}
