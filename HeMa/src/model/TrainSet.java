package model;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.MethodDeclaration;
import util.FileParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainSet {
    private static final Map<Signature, Map<String, Integer>> data = new HashMap<>();

    public static void initialize(String dataLocation) {
        System.out.println("Initializing TrainSet... " + new Timestamp(System.currentTimeMillis()));
        if (dataLocation == null) {
            throw new RuntimeException("Provide a directory for training, or a train set CSV");
        }

        if (dataLocation.endsWith(".csv")) {
            load(dataLocation);
        } else {
            create(dataLocation);
        }
        System.out.println("Finished initializing TrainSet, " + new Timestamp(System.currentTimeMillis()));
    }

    private static void create(String dataDirectory) {
        StringBuilder sb = new StringBuilder();

        // Initialize Directory
        File root = new File(dataDirectory);

        if (root.exists() && root.isDirectory()) {
            try {
                Files.walk(Paths.get(dataDirectory)).filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".java")).forEach(path -> exploreClass(path, sb));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (PrintWriter writer = new PrintWriter(new File("trainSet.csv"))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to write to trainSet.csv");
        }
    }

    private static void exploreClass(Path path, StringBuilder sb) {
        // Initialize the code from class
        String code;
        try {
            code = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
            code = "";
        }

        try {
            ArrayList<MethodDeclaration> nodes = FileParser.extractFeatures(code);

            // Update data with new methods
            for (MethodDeclaration m : nodes) {
                Signature signature = new Signature(m);
                Map<String, Integer> signatureMap = data.getOrDefault(signature, new HashMap<>());

                String methodName = m.getNameAsString();
                int times = signatureMap.getOrDefault(methodName, 0) + 1;
                signatureMap.put(methodName, times);

                data.put(signature, signatureMap);

                sb.append('"').append(methodName).append('"').append(',').append('"').append(signature).append('"').append('\n');
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void load(String dataDirectory) {
        int total = 0;
        List<String> lines = read(dataDirectory);

        for (String line : lines) {
            total++;
            String[] strs = line.split(",");
            String method_name = strs[0].substring(1, strs[0].length() - 1);
            Signature signature = new Signature(strs[1].substring(1, strs[1].length() - 1));

            Map<String, Integer> counter = data.getOrDefault(signature, new HashMap<>());
            int count = counter.getOrDefault(method_name, 0) + 1;
            counter.put(method_name, count);
            data.put(signature, counter);
        }
        System.out.println(total + " train samples loaded.");
    }

    private static List<String> read(String filePath) {
        List<String> list = new ArrayList<>();
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    list.add(line);
                }
                bufferedReader.close();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static Map<Signature, Map<String, Integer>> getData() {
        return data;
    }
}
