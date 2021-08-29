package model;

import prediction.PredictionManager;

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

    public void score(TokenizedName reference) {
        int tp = 0;
        int fp = 0;
        int fn = 0;

        HashSet<String> refTokens = reference.tokens;

        for (String predToken : tokens) {
            if (refTokens.contains(predToken)) tp++;
            else fp++;
        }

        for (String refToken : refTokens) {
            if (!tokens.contains(refToken)) fn++;
        }

        PredictionManager.truePositive += tp;
        PredictionManager.falsePositive += fp;
        PredictionManager.falseNegative += fn;
    }

    @Override
    public String toString() {
        return methodName;
    }
}
