package predictor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import util.Tokenizer;

import java.util.List;

public class DelegationPredictor {
    public static int predicted = 0;
    public static int correct = 0;

    public static int predict(MethodDeclaration node) {
        List<Statement> stmts = node.getBody().getStmts();
        if (stmts.size() != 1) {
            return -1;
        }

        Statement stmt = stmts.get(0);

        Expression expr;
        if (stmt instanceof ExpressionStmt) {
            expr = ((ExpressionStmt) stmt).getExpression();
        } else if (stmt instanceof ReturnStmt) {
            expr = ((ReturnStmt) stmt).getExpr();
        } else {
            return -1;
        }

        if (expr instanceof MethodCallExpr) {
            predicted++;
            String reference = Tokenizer.tokenize(node.getName()).toLowerCase();

            MethodCallExpr method = (MethodCallExpr) expr;
            String prediction = Tokenizer.tokenize(method.getName()).toLowerCase();

            int precision = reference.equals(prediction) ? 1 : 0;
            correct += precision;
            return precision;
        }

        return -1;
    }
}
