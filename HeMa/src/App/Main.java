package App;

import JavaExtractor.App;
import JavaExtractor.Common.CommandLineValues;
import org.kohsuke.args4j.CmdLineException;

public class Main {
    public static void main(String[] args) {
        CommandLineValues s_CommandLineValues;
        App.test();
        try {
            s_CommandLineValues = new CommandLineValues(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        String evaluationDir = s_CommandLineValues.Dir;
        int numThreads = s_CommandLineValues.NumThreads;
        String dataDir = s_CommandLineValues.Train;

        if (evaluationDir == null || dataDir == null) return;
        new HeMa(dataDir, numThreads, evaluationDir).start();
    }
}
