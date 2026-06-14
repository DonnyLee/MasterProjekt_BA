package fitness.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Parser {
    public Parser() {
    }

    public static Dataset read(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        boolean isReadingNodes = false;
        Scanner scanner = new Scanner(path);
        
        // Read Name (e.g. NAME : a280)
        String nameLine = scanner.nextLine();
        String name = nameLine.split(":")[1].trim();
        
        String nextLine = scanner.nextLine();
        String type = "";
        if (nextLine.contains("TYPE")) {
            type = nextLine.split(":")[1].trim();
            scanner.nextLine(); // skip the next line (often COMMENT)
        } else {
            type = scanner.nextLine().split(":")[1].trim();
        }
        
        // Read Dimension/Size
        String sizeLine = scanner.nextLine();
        int size = Integer.parseInt(sizeLine.split(":")[1].trim());
        
        Dataset dataset = new Dataset(type, size, name);
        
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (!isReadingNodes) {
                if ("TSP".equals(type)) {
                    if (line.contains("NODE_COORD_SECTION")) {
                        isReadingNodes = true;
                    }
                } else {
                    if (line.contains(String.valueOf(size))) {
                        isReadingNodes = true;
                    }
                }
            } else {
                if (line.contains("EOF")) {
                    continue;
                }
                dataset.addNode(line);
            }
        }
        scanner.close();
        return dataset;
    }
}
