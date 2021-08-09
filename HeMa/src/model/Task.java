package model;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.MethodDeclaration;
import predictor.Predictor;
import util.FileParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Task implements Callable<Void> {
    String code;

    public Task(Path path) {
        try {
            this.code = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
            this.code = "";
        }
    }

    @Override
    public Void call() throws Exception {
        try {
            ArrayList<MethodDeclaration> nodes = FileParser.extractFeatures(code);
            for (MethodDeclaration m : nodes) Predictor.predict(m);

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
