package App;

public class Main {
    public static void main(String[] args) {
        String evaluationDir = args[0];
        int numThreads = Integer.parseInt(args[1]);
        String dataDir = args[2];

        if (evaluationDir == null || dataDir == null) return;

        new HeMa(dataDir, numThreads).start(evaluationDir);
    }
}
