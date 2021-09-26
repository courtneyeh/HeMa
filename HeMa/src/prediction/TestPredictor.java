package prediction;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import util.Tokenizer;

import java.util.*;

public class TestPredictor extends Predictor {

    protected TestPredictor() {
        super("TEST");
    }

    @Override
    String predict(MethodDeclaration method) {
        // Check the method is overridden
        if (method.getAnnotationByName("Test").isEmpty()) return null;

        // Assert method has body
        if (method.getBody().isEmpty()) return null;

        // Create list of tokens, based on variables and their types
        List<String> tokens = new ArrayList<>();
        method.findAll(VariableDeclarator.class).forEach(variableDeclarator -> {
            updateTokenList(tokens, variableDeclarator.getTypeAsString()); // Class type
            updateTokenList(tokens, variableDeclarator.getNameAsString()); // Variable name
        });

        // Create frequency HashMap, from list of tokens
        HashMap<String, Integer> frequency = new HashMap<>();
        for (String t : tokens) frequency.put(t, frequency.getOrDefault(t, 0) + 1);

        // Create priority queue based on frequency of tokens
        Queue<String> queue = new PriorityQueue<>((String a, String b) -> frequency.get(b) - frequency.get(a));
        queue.addAll(frequency.keySet());

        String prediction = getPrediction(queue);
        return Tokenizer.tokenize("test " + prediction).toLowerCase();
    }

    private void updateTokenList(List<String> tokens, String str) {
        for (String s : Tokenizer.tokenize(str).toLowerCase().split(" ")) {
            if ("test".equals(s) || s.length() == 1 && Character.isDigit(s.charAt(0))) continue;
            tokens.add(s);
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
