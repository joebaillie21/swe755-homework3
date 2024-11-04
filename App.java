import java.io.*;
import java.util.concurrent.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the file path: ");
        String filePath = sc.nextLine().trim();

        // Verify the user input is a valid file path and isn't attempting traversal
        if (filePath.contains("..") || filePath.contains(":") || filePath.contains("/") || filePath.contains("\\")) {
            System.out.println("Invalid file path.");
            sc.close();
            return;
        }
        File file = new File(filePath);
        // Check if the file exists and is a file
        if (!file.exists() || !file.isFile()) {
            System.out.println("File does not exist.");
            sc.close();
            return;
        }
        System.out.println("Enter the number of threads: ");
        int numThreads = sc.nextInt();
        if (numThreads <= 0) {
            System.out.println("Invalid number of threads.");
            sc.close();
            return;
        }

        System.out.println("What file type would you like to output? (txt, csv, json)");
        String fileType = sc.next().trim();
        if (!fileType.equals("txt") && !fileType.equals("csv") && !fileType.equals("json")) {
            System.out.println("Invalid file type.");
            sc.close();
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int linesPerThread = 1000000;  // Adjust based on your needs
            BlockingQueue<String> lineQueue = new LinkedBlockingQueue<>();

            // Producer to read file and add lines to queue
            Thread producer = new Thread(() -> {
                try {
                    while ((line = reader.readLine()) != null) {
                        lineQueue.put(line);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            producer.start();

            // Consumers to process lines from queue
            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    try {
                        String data;
                        while ((data = lineQueue.poll(1, TimeUnit.SECONDS)) != null) {
                            // Process data here
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            producer.join();
        } catch (IOException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");

        sc.close();
    }
}