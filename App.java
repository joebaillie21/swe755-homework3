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

        /**
         * Split the file into chunks based on the size of the file and the number of
         * threads
         * Include remaining size to ensure all data is accounted for
         */
        long fileSize = file.length();
        long chunkSize = fileSize / numThreads;
        long remainingSize = fileSize % numThreads;

        // Create the thread pool with the inputted amount of threads
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        long start = 0;
        long end = chunkSize;

        long startTime = System.currentTimeMillis();
        // Create a new FileChunkReader for each chunk of the file
        for (int i = 0; i < numThreads; i++) {
            // Check if at the second the last chunk, if so, add the remaining size
            if (i == numThreads - 1) {
                end += remainingSize;
            }
            executor.submit(new FileChunkReader(file, start, end));
            start = end;
            end += chunkSize;
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");

        sc.close();
    }
}