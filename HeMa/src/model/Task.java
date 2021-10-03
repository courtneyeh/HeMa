package model;

import App.HeMa;
import JavaExtractor.Common.CommandLineValues;
import JavaExtractor.FeatureExtractor;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.MethodDeclaration;
import me.tongfei.progressbar.ProgressBar;
import util.FileParser;
import JavaExtractor.ExtractFeaturesTask;
import JavaExtractor.MethodAST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Task implements Callable<Void> {
    /**
     * Edit Commands Based on Your System
     **/
    private static final String javaPath = "java";
    private static final String javaExtractorJarPath = "./HeMa/JavaExtractor.jar";

    String code;
    Path path;

    public Task(Path path) {
        this.path = path;
        try {
            this.code = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
            this.code = "";
        }
    }

    public ArrayList<MethodAST> executeBashCommand(String file) {
        ArrayList<MethodAST> filesMethods = new ArrayList<>();

        Runtime r = Runtime.getRuntime();
        String[] commands = {javaPath, "-jar", javaExtractorJarPath, "--max_path_length", "8", "--max_path_width", "2",
                "--dir", file, "--num_threads", "1"};

        try {
            Process p = r.exec(commands);

            if (!p.waitFor(100, TimeUnit.SECONDS)) {
                return null;
            }

            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = b.readLine()) != null) {
                String name = line.split(",")[0];
                int length = Integer.parseInt(line.split(",")[1]);
                filesMethods.add(new MethodAST(name, length));
            }

            b.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filesMethods;
    }

    private void runMethod() {
        try {
            ArrayList<MethodDeclaration> nodes = FileParser.extractFeatures(code);
            ArrayList<MethodAST> c2v_methods = executeBashCommand(path.toString());
            if (c2v_methods == null) {
                System.out.println(path + ": Timed Out");
                for (MethodDeclaration m : nodes)
                    HeMa.predictionManager.predict(m, path, new MethodAST("Timed Out", -2));
            } else if (c2v_methods.size() != nodes.size()) {
                System.out.println(path + ": Not Equal");
                for (MethodDeclaration m : nodes)
                    HeMa.predictionManager.predict(m, path, new MethodAST("No Match", -1));
            } else {
                for (int i = 0; i < nodes.size(); i++) {
                    HeMa.predictionManager.predict(nodes.get(i), path, c2v_methods.get(i));
                }
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Void call() throws Exception {
        runMethod();
        return null;
    }
}
