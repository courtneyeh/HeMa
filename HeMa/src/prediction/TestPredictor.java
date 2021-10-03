package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import util.Tokenizer;

import java.nio.file.Path;
import java.util.*;

public class TestPredictor extends Predictor {

    protected TestPredictor() {
        super("TEST");
    }

    @Override
    String predict(MethodDeclaration method, Path path) {
        // Check the method is overridden
        if (method.getAnnotationByName("Test").isEmpty()) return null;

        // Assert method has body
        if (method.getBody().isEmpty()) return null;

        // Create HashMap of tokens frequencies, based on variables and their types
        HashMap<String, Integer> frequency = new HashMap<>();
        method.findAll(VariableDeclarator.class).forEach(variableDeclarator -> {
            updateFrequenciesList(frequency, variableDeclarator.getTypeAsString()); // Class type
            updateFrequenciesList(frequency, variableDeclarator.getNameAsString()); // Variable name
        });

        // Create priority queue based on frequency of tokens
        Queue<String> queue = new PriorityQueue<>((String a, String b) -> frequency.get(b) - frequency.get(a));
        queue.addAll(frequency.keySet());

        String prediction = getPrediction(queue);
        return Tokenizer.tokenize("test " + prediction).toLowerCase();
    }

    private void updateFrequenciesList(HashMap<String, Integer> frequency, String str) {
        for (String token : Tokenizer.tokenize(str).toLowerCase().split(" ")) {
            if ("test".equals(token) || token.length() == 1 && Character.isDigit(token.charAt(0))) continue;
            frequency.put(token, frequency.getOrDefault(token, 0) + 1);
        }
    }

    private String getPrediction(Queue<String> queue) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (queue.peek() != null) sb.append(queue.poll()).append(" ");
        }

        return sb.toString().strip();
    }
}
