package model;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenizedName {
    public String methodName;
    public HashSet<String> tokens;

    public static boolean onlyDigits(String str)
    {
        // Regex to check string
        // contains only digits
        String regex = "[0-9]+";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (str == null) {
            return false;
        }

        // Find match between given string
        // and regular expression
        // using Pattern.matcher()
        Matcher m = p.matcher(str);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    public TokenizedName(String str) {
        str = str.toLowerCase();

        tokens = new HashSet<>();

        String[] tokensStrings = str.split(" ");

        String[] noNumberTokenStrings = {};
        int index = 0;
        // iterating over an array
        for (int i = 0; i < tokensStrings.length; i++) {
            // accessing each element of array
            if(onlyDigits(tokensStrings[i]) == false){
                noNumberTokenStrings = ArrayUtils.add(noNumberTokenStrings, tokensStrings[i]);
                index++;
            }
        }

        tokens.addAll(Arrays.asList(noNumberTokenStrings));

        methodName = String.join("|", noNumberTokenStrings);
    }

    @Override
    public String toString() {
        return methodName;
    }
}
