package App;

import model.Task;
import model.TrainSet;
import prediction.PredictionManager;
import util.Recorder;
import util.score.OriginalScore;
import util.score.UpdatedScore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HeMa {
    public static String evaluationDir;
    public static PredictionManager predictionManager = new PredictionManager();
    private final int numThreads;

    HeMa(String dataDir, int numThreads, String evaluationDir) {
        Recorder.initialize();
        TrainSet.initialize(dataDir);
        this.numThreads = numThreads;
        HeMa.evaluationDir = evaluationDir;
    }

    public void start() {
        System.out.println("Starting HeMa... " + new Timestamp(System.currentTimeMillis()));
        File root = new File(evaluationDir);
        if (!root.exists() || !root.isDirectory()) return;

        File[] files = root.listFiles();
        for (File f : Objects.requireNonNull(files)) {
            extractDir(f.getPath());
        }

        System.out.println("Finished HeMa, " + new Timestamp(System.currentTimeMillis()));

        printResults();
    }

    private void printResults() {
        System.out.println("\n---------- Results ----------");
        System.out.println("total = " + PredictionManager.methodCount);
        System.out.println("predicted = " + PredictionManager.predictedMethods);
        OriginalScore.printResults();
        UpdatedScore.printResults();
    }

    private void extractDir(String dir) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        LinkedList<Task> tasks = new LinkedList<>();

        try {
            Files.walk(Paths.get(dir)).filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".java")).forEach(f -> {
                Task task = new Task(f);
                tasks.add(task);
            });
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
