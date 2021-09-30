package App;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        String evaluationDir = args[0];
        int numThreads = Integer.parseInt(args[1]);
        String dataDir = args[2];

        if (evaluationDir == null || dataDir == null) return;

        new HeMa(dataDir, numThreads).start(evaluationDir);
    }
}
