package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import util.Recorder;
import util.Tokenizer;

import java.util.List;

public class DelegationPredictor extends Predictor {

    public DelegationPredictor() {
        super("DELEGATION");
    }

    @Override
    public boolean predict(MethodDeclaration node) {
        // Check there is a single statement
        List<Statement> stmts = node.getBody().getStmts();
        if (stmts.size() != 1) return false;

        // Check the single statement is an expression or return statement
        Statement stmt = stmts.get(0);
        Expression expr;
        if (stmt instanceof ExpressionStmt) {
            expr = ((ExpressionStmt) stmt).getExpression();
        } else if (stmt instanceof ReturnStmt) {
            expr = ((ReturnStmt) stmt).getExpr();
        } else {
            return false;
        }

        // Check single method is a method call
        if (!(expr instanceof MethodCallExpr)) return false;

        predicted++;
        String reference = Tokenizer.tokenize(node.getName()).toLowerCase();

        MethodCallExpr method = (MethodCallExpr) expr;
        String prediction = Tokenizer.tokenize(method.getName()).toLowerCase();

        correct += reference.equals(prediction) ? 1 : 0;

        Recorder.save(reference, prediction, TYPE);
        return true;
    }
}
