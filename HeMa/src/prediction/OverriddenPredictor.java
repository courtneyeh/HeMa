package prediction;

import App.HeMa;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import model.Signature;
import util.FileParser;
import util.Tokenizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class OverriddenPredictor extends Predictor {

    protected OverriddenPredictor() {
        super("OVERRIDDEN");
    }

    @Override
    String predict(MethodDeclaration method, Path path) {
        // Check the method is overridden
        if (method.getAnnotationByClass(Override.class).isEmpty()) return null;

        // Get the class signature
        if (method.getParentNode().isEmpty()) return null;
        Node parentNode = method.getParentNode().get();

        // Check the parent node of the method is a class or interface
        if (!(parentNode instanceof ClassOrInterfaceDeclaration)) return null;
        ClassOrInterfaceDeclaration parent = (ClassOrInterfaceDeclaration) parentNode;

        // First consider implemented types, as the methods always need to be implemented
        String interfaceMethod = getOverrideMethodMatch(parent.getImplementedTypes(), method, path, true);
        if (interfaceMethod != null) return interfaceMethod;

        // Next consider classes and their abstract methods
        String abstractMethod = getOverrideMethodMatch(parent.getExtendedTypes(), method, path, true);
        if (abstractMethod != null) return abstractMethod;

        // Next consider classes and their full methods (with bodies)
        String fullMethod = getOverrideMethodMatch(parent.getExtendedTypes(), method, path, false);
        if (fullMethod != null) return fullMethod;

        // Consider generic Object methods that can be overridden (ex. equals, hashCode, toString)
        NodeList<Parameter> params = method.getParameters();

        // equals(Object obj)
        if (method.getType().toString().equals("boolean") && params.size() == 1 && params.getFirst().isPresent()
                && params.getFirst().get().getType().toString().equals("Object")) {
            return Tokenizer.tokenize("equals").toLowerCase();
        }

        // hashCode()
        if (method.getType().toString().equals("int") && params.size() == 0) {
            return Tokenizer.tokenize("hashCode").toLowerCase();
        }

        // toString()
        if (method.getType().toString().equals("String") && params.size() == 0) {
            return Tokenizer.tokenize("toString").toLowerCase();
        }

        return null;
    }

    private String getOverrideMethodMatch(NodeList<ClassOrInterfaceType> types, MethodDeclaration originalMethod,
                                          Path path, boolean noBody) {
        for (ClassOrInterfaceType type : types) {
            String code = getClassOrInterfaceCode(type, path);
            if (code == null) continue;
            String methodName = getFirstSignatureMatch(originalMethod, code, noBody);
            if (methodName == null) continue;
            return Tokenizer.tokenize(methodName).toLowerCase();
        }

        return null;
    }

    private String getClassOrInterfaceCode(ClassOrInterfaceType type, Path path) {
        String fileName = type.getName() + ".java";

        try {
            // First try find file in current directory (same as class)
            Path potentialPath = Path.of(path.getParent().toString() + "/" + fileName);
            return Files.readString(potentialPath, StandardCharsets.US_ASCII);

        } catch (IOException e) {
            // The file could not be found, so search entire project for file
            String[] pathSplit = path.toString().split(HeMa.evaluationDir, 2);
            if (pathSplit.length != 2) return null;
            String projectDir = getProjectDir(pathSplit[1]);

            try (Stream<Path> stream = Files.walk(Paths.get(HeMa.evaluationDir + projectDir))) {
                Optional<Path> result = stream.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(fileName)).findFirst();
                if (result.isPresent()) return new String(Files.readAllBytes(result.get()));

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        return null;
    }

    private String getProjectDir(String pathSuffix) {
        int index = 0;
        if (pathSuffix.charAt(0) == '/') index++; // Start at second position

        while (pathSuffix.charAt(index) != '/') {
            index++;
        }

        return pathSuffix.substring(0, index);
    }

    private String getFirstSignatureMatch(MethodDeclaration original, String code, boolean noBody) {
        CompilationUnit cu;
        try {
            cu = FileParser.parseFileWithRetries(code);
        } catch (IOException ioException) {
            return null; // Parse failed
        }

        if (cu == null) return null;

        for (MethodDeclaration declaration : cu.findAll(MethodDeclaration.class)) {
            if (declaration.getBody().isPresent() == noBody)
                continue; // If there is a body, this is unlikely to be overridden

            if (!new Signature(original).equals(new Signature(declaration))) {
                continue;
            }

            return declaration.getName().asString();
        }

        return null;
    }
}
