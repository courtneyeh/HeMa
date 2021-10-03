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
    private final CommandLineValues commandLineValues;
    String code;
    Path path;

    public Task(Path path, CommandLineValues commandLineValues) {
        this.path = path;
        this.commandLineValues = commandLineValues;
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
//        String[] commands = {"/home/glow250/p4p/jdk-15.0.2/bin/java","-Dfile.encoding=UTF-8",
//                "-classpath","/home/glow250/JPredict/wLWPRwYIyv:/home/glow250/JPredict/ZQAjJi9LEo/javaparser-core-3.0.0-alpha.4.jar:/home/glow250/JPredict/VXy3kJ4MLC/commons-io-1.3.2.jar:/home/glow250/JPredict/uH876y9BWi/jackson-databind-2.9.10.4.jar:/home/glow250/JPredict/bXgM41le7D/jackson-annotations-2.9.10.jar:/home/glow250/JPredict/yzVqIV0y0x/jackson-core-2.9.10.jar:/home/glow250/JPredict/P5fTBvkCYc/args4j-2.33.jar:/home/glow250/JPredict/ke0wETmXAm/commons-lang3-3.5.jar",
//                "JavaExtractor.App","--max_path_length","8","--max_path_width","2",
//                "--dir",file,"--num_threads","1"};
        String[] commands = {"java","-jar",
                "JavaExtractor.jar","--max_path_length","8","--max_path_width","2",
                "--dir",file,"--num_threads","1"};
        try {
            Process p = r.exec(commands);

            if(!p.waitFor(100, TimeUnit.SECONDS)){
                return null;
            }

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

    private void runMethod() {
        try {
            ArrayList<MethodDeclaration> nodes = FileParser.extractFeatures(code);
            ArrayList<MethodAST> c2v_methods = executeBashCommand(path.toString());
            if (c2v_methods == null){
                System.out.println(path + ": Timed Out");
                for (MethodDeclaration m : nodes) HeMa.predictionManager.predict(m, path, new MethodAST("Timed Out", -2));
            }else if(c2v_methods.size() != nodes.size()){
                System.out.println(path + ": Not Equal");
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

    @Override
    public Void call() throws Exception {
        runMethod();
        return null;
    }


}
