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
    private final int numThreads;
    public static PredictionManager predictionManager = new PredictionManager();

    HeMa(String dataDir, int numThreads) {
        // Set up prediction folder and add header
        Recorder.initialize();
        // Load trainset csv
        TrainSet.initialize(dataDir);
        this.numThreads = numThreads;
    }

    public void start(String evaluationDir) {

        System.out.println("Starting HeMa... " + new Timestamp(System.currentTimeMillis()));
        File root = new File(evaluationDir);
        if (!root.exists() || !root.isDirectory()) return;

        File[] files = root.listFiles();
        // iterate over top level files
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
            // Iterate over directories
            Files.walk(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".java"))
                    .forEach(f -> {
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
