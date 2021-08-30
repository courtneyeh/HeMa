package util;

import model.Score;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class Recorder {
    private static String SAVE_FILE;
    private static final String SEPARATOR = ", ";

    public static void initialize() {
        SAVE_FILE = "HeMa_predictions_" + new Timestamp(System.currentTimeMillis()) + ".csv";

        try {
            FileWriter fw = new FileWriter(SAVE_FILE, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Method Name" + SEPARATOR + "Prediction" + SEPARATOR + "Successful Prediction" + SEPARATOR
                    + "Predictor Type" + SEPARATOR + "True Positive" + SEPARATOR + "False Positive" + SEPARATOR
                    + "False Negative");
            bw.newLine();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(String methodName, String prediction, String predictorType, Score score) {
        String match = String.valueOf(methodName.equals(prediction));

        try {
            FileWriter fw = new FileWriter(SAVE_FILE, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(methodName + SEPARATOR + prediction + SEPARATOR + match + SEPARATOR + predictorType + SEPARATOR
                    + score.truePositive + SEPARATOR + score.falsePositive + SEPARATOR + score.falseNegative);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
