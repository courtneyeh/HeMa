package model;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenizedName {
    public String methodName;
    public HashSet<String> tokens;

    public TokenizedName(String str) {
        str = str.toLowerCase();

        tokens = new HashSet<>();

        // Get the token strings
        String[] tokensStrings = str.split(" ");
        String[] noNumberTokenStrings = {};

        // Iterate over the tokens
        for (String tokensString : tokensStrings) {
            // Accessing each token
            if (!onlyDigits(tokensString)) {
                noNumberTokenStrings = ArrayUtils.add(noNumberTokenStrings, tokensString);
            }
        }

        // Join tokens together to create method name
        tokens.addAll(Arrays.asList(noNumberTokenStrings));
        methodName = String.join("|", noNumberTokenStrings);
    }

    public static boolean onlyDigits(String str) {
        // Regex to check string contains only digits
        String regex = "[0-9]+";

        // Compile the Regex
        Pattern p = Pattern.compile(regex);

        // If the string is empty return false
        if (str == null) {
            return false;
        }

        // Find match between given string and regular expression using Pattern.matcher()
        Matcher m = p.matcher(str);

        // Return if the string matched the ReGex
        return m.matches();
    }

    @Override
    public String toString() {
        return methodName;
    }
}
