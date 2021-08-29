package model;

import util.Tokenizer;

import java.util.HashMap;
import java.util.List;

public class TokenizedName {
    String methodName;
    HashMap<String, Integer> tokenPositions;

    public TokenizedName(String str) {
        str = str.toLowerCase();

        tokenPositions = new HashMap<>();

        String[] tokens = str.split(" ");

        for (int i = 0; i < tokens.length; i++) {
            tokenPositions.put(tokens[i], i);
        }

        methodName = str.replaceAll(" ", "|");
    }

    @Override
    public String toString() {
        return methodName;
    }
}
