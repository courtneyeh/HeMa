package util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import predictor.Predictor;

import java.io.IOException;
import java.util.ArrayList;

public class FileParser {
    private final String code;
    private CompilationUnit m_CompilationUnit;

    public FileParser(String code) {
        this.code = code;
    }

    public static CompilationUnit parseFileWithRetries(String code) throws IOException {
        final String classPrefix = "public class Test {";
        final String classSuffix = "}";
        final String methodPrefix = "SomeUnknownReturnType f() {";
        final String methodSuffix = "return noSuchReturnValue; }";

        String content = code;
        CompilationUnit parsed;
        try {
            parsed = JavaParser.parse(content);
        } catch (ParseProblemException e1) {
            // Wrap with a class and method
            try {
                content = classPrefix + methodPrefix + code + methodSuffix + classSuffix;
                parsed = JavaParser.parse(content);
            } catch (ParseProblemException e2) {
                // Wrap with a class only
                content = classPrefix + code + classSuffix;
                parsed = JavaParser.parse(content);
            }
        }

        return parsed;
    }

    public CompilationUnit getParsedFile() {
        return m_CompilationUnit;
    }

    public void extractFeatures() throws ParseException, IOException {
        m_CompilationUnit = parseFileWithRetries(code);
        FunctionVisitor functionVisitor = new FunctionVisitor();

        functionVisitor.visit(m_CompilationUnit, null);
        ArrayList<MethodDeclaration> nodes = functionVisitor.getMethodDeclarations();

        Predictor.predict(nodes);
    }
}
