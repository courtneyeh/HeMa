package model;

import java.util.Arrays;
import java.util.HashSet;

public class TokenizedName {
    public String methodName;
    public HashSet<String> tokens;

    public TokenizedName(String str) {
        str = str.toLowerCase();

        tokens = new HashSet<>();

        String[] tokensStrings = str.split(" ");
        tokens.addAll(Arrays.asList(tokensStrings));

        methodName = str.replaceAll(" ", "|");
    }

    @Override
    public String toString() {
        return methodName;
    }
}
