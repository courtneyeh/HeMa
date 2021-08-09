package App;

import model.Task;
import model.TrainSet;
import predictor.PredictionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HeMa {
    private final int numThreads;
    public static PredictionManager predictionManager = new PredictionManager();

    HeMa(String dataDir, int numThreads) {
        TrainSet.initialize(dataDir);
        this.numThreads = numThreads;
    }

    public void start(String evaluationDir) {
        File root = new File(evaluationDir);
        if (!root.exists() || !root.isDirectory()) return;

        File[] files = root.listFiles();
        for (File f : Objects.requireNonNull(files)) {
            extractDir(f.getPath());
        }

        PredictionManager.printResults();
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
