package util;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;

public class FunctionVisitor extends VoidVisitorAdapter<Object> {
    private final ArrayList<MethodDeclaration> m_Nodes = new ArrayList<>();

    @Override
    public void visit(MethodDeclaration node, Object arg) {
        AnnotationExpr annotation = node.getAnnotationByClass(Override.class).orElse(null);

        if (annotation == null && node.getBody().isPresent())
            m_Nodes.add(node);

        super.visit(node, arg);
    }

    public ArrayList<MethodDeclaration> getMethodDeclarations() {
        return m_Nodes;
    }
}
