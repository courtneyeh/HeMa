package util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.IOException;
import java.util.ArrayList;

public class FileParser {

    public static ArrayList<MethodDeclaration> extractFeatures(String code) throws ParseException, IOException {
        CompilationUnit m_CompilationUnit = parseFileWithRetries(code);
        if (m_CompilationUnit == null) return new ArrayList<>(); // No method declarations

        FunctionVisitor functionVisitor = new FunctionVisitor();
        functionVisitor.visit(m_CompilationUnit, null);

        return functionVisitor.getMethodDeclarations();
    }

    public static CompilationUnit parseFileWithRetries(String code) throws IOException {
        final String classPrefix = "public class Test {";
        final String classSuffix = "}";
        final String methodPrefix = "SomeUnknownReturnType f() {";
        final String methodSuffix = "return noSuchReturnValue; }";

        JavaParser javaParser = new JavaParser();

        String content = code;
        ParseResult<CompilationUnit> parsed;
        try {
            parsed = javaParser.parse(content);
        } catch (ParseProblemException e1) {
            // Wrap with a class and method
            try {
                content = classPrefix + methodPrefix + code + methodSuffix + classSuffix;
                parsed = javaParser.parse(content);
            } catch (ParseProblemException e2) {
                // Wrap with a class only
                content = classPrefix + code + classSuffix;
                parsed = javaParser.parse(content);
            }
        }

        return parsed.getResult().isPresent() ? parsed.getResult().get() : null;
    }
}
