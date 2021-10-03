package App;

import JavaExtractor.Common.CommandLineValues;
import JavaExtractor.ExtractFeaturesTask;
import JavaExtractor.FeatureExtractor;
import JavaExtractor.FeaturesEntities.ProgramFeatures;
import JavaExtractor.MethodAST;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.MethodDeclaration;
import me.tongfei.progressbar.ProgressBar;
import model.Task;
import model.TrainSet;
import prediction.PredictionManager;
import util.FileParser;
import util.Recorder;
import util.score.OriginalScore;
import util.score.UpdatedScore;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HeMa {
    private final int numThreads;
    public static PredictionManager predictionManager = new PredictionManager();
    private final CommandLineValues cmdLineValues;

    public HeMa(String dataDir, int numThreads, CommandLineValues cmdLineValues) {
        // Set up prediction folder and add header
        Recorder.initialize();
        // Load trainset csv
        TrainSet.initialize(dataDir);
        this.numThreads = numThreads;
        this.cmdLineValues = cmdLineValues;
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

    public static ArrayList<MethodAST> executeBashCommand(String file) {
        ArrayList<MethodAST> filesMethods = new ArrayList<>();

        Runtime r = Runtime.getRuntime();
        String[] commands = {"/home/glow250/p4p/jdk-15.0.2/bin/java","-Dfile.encoding=UTF-8",
                "-classpath","/home/glow250/JPredict/TEIuv3gOef:/home/glow250/JPredict/ZQAjJi9LEo/javaparser-core-3.0.0-alpha.4.jar:/home/glow250/JPredict/VXy3kJ4MLC/commons-io-1.3.2.jar:/home/glow250/JPredict/uH876y9BWi/jackson-databind-2.9.10.4.jar:/home/glow250/JPredict/bXgM41le7D/jackson-annotations-2.9.10.jar:/home/glow250/JPredict/yzVqIV0y0x/jackson-core-2.9.10.jar:/home/glow250/JPredict/P5fTBvkCYc/args4j-2.33.jar:/home/glow250/JPredict/ke0wETmXAm/commons-lang3-3.5.jar",
                "JavaExtractor.App","--max_path_length","8","--max_path_width","2",
                "--dir",file,"--num_threads","1"};
        try {
            Process p = r.exec(commands);

            p.waitFor();

            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";

            while ((line = b.readLine()) != null) {
                String name = line.split(",")[0];
                int length = new Integer(line.split(",")[1]);
                filesMethods.add(new MethodAST(name, length));
            }

            b.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filesMethods;
    }

    private ArrayList<String> socketConnect(String file) throws IOException, ClassNotFoundException {
        ServerSocket ss = new ServerSocket(7777);
        Runtime r = Runtime.getRuntime();
        // Use bash -c so we can handle things like multi commands separated by ; and
        // things like quotes, $, |, and \. My tests show that command comes as
        // one argument to bash, so we do not need to quote it to make it one thing.
        // Also, exec may object if it does not have an executable file as the first thing,
        // so having bash here makes it happy provided bash is installed and in path.
        String[] commands = {"/home/glow250/p4p/jdk-15.0.2/bin/java","-Dfile.encoding=UTF-8",
                "-classpath","/home/glow250/JPredict/HZeySTXFs5:/home/glow250/JPredict/ZQAjJi9LEo/javaparser-core-3.0.0-alpha.4.jar:/home/glow250/JPredict/VXy3kJ4MLC/commons-io-1.3.2.jar:/home/glow250/JPredict/uH876y9BWi/jackson-databind-2.9.10.4.jar:/home/glow250/JPredict/bXgM41le7D/jackson-annotations-2.9.10.jar:/home/glow250/JPredict/yzVqIV0y0x/jackson-core-2.9.10.jar:/home/glow250/JPredict/P5fTBvkCYc/args4j-2.33.jar:/home/glow250/JPredict/ke0wETmXAm/commons-lang3-3.5.jar",
                "JavaExtractor.App","--max_path_length","8","--max_path_width","2",
                "--dir",file,"--num_threads","1"};
        r.exec(commands);

        Socket socket = ss.accept(); // blocking call, this will wait until a connection is attempted on this port.

        // get the input stream from the connected socket
        InputStream inputStream = socket.getInputStream();
        // create a DataInputStream so we can read data from it.
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        // read the list of messages from the socket
        ArrayList<String> listOfMessages = (ArrayList<String>) objectInputStream.readObject();
        ss.close();
        socket.close();
        return listOfMessages;
    }

    private void runMethod(CommandLineValues commandLineValues, Path path, FeatureExtractor fe) {
        try {
            String code = new String(Files.readAllBytes(path));
            ArrayList<MethodDeclaration> nodes = FileParser.extractFeatures(code);
//            ExtractFeaturesTask eft = new ExtractFeaturesTask(commandLineValues, path);
//            ArrayList<MethodAST> c2v_methods = eft.getMethodsAndAsts();
//            ArrayList<ProgramFeatures> features = fe.extractFeatures(code);
//            ArrayList<String> c2v_methods = null;
//            try {
//                c2v_methods = socketConnect(path.toString());
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
            ArrayList<MethodAST> c2v_methods = executeBashCommand(path.toString());
            if(c2v_methods.size() != nodes.size()){
                for (MethodDeclaration m : nodes) HeMa.predictionManager.predict(m, path, new MethodAST("No Match", -1));
            }else{
                for(int i = 0; i < nodes.size(); i++){
                    HeMa.predictionManager.predict(nodes.get(i), path, c2v_methods.get(i));
                }
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    private void extractDir(String dir) {
//        FeatureExtractor featureExtractor = new FeatureExtractor(cmdLineValues);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        LinkedList<Task> tasks = new LinkedList<>();
        try {
            // Iterate over directories

            Files.walk(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".java"))
                    .forEach(f -> {
                        Task task = new Task(f, cmdLineValues);
                        tasks.add(task);
//                            runMethod(cmdLineValues, f, featureExtractor);
//                            pb.step();
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
