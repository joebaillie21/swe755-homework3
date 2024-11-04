import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class will read specific chunks of a file
 * It implements Runnable so that it can be passed to a thread
 * It implements Runnable instead of extending Thread because
 * it only needs to implement the run method, rather than overriding
 * multiple thread methods.
 */
public class FileChunkReader implements Runnable {

    private File file;
    private long start;
    private long end;
    private String fileType;
    private String inputType;

    public FileChunkReader(File file, long start, long end, String fileType) {
        this.file = file;
        this.start = start;
        this.end = end;
        this.fileType = fileType;
        inputType = file.getName().substring(file.getName().lastIndexOf(".") + 1); // Extract the file type from the
                                                                                   // file name
    }

    /**
     * This is required method to implement Runnable interface
     * It will be passed to a thread and executed
     * 
     * RandomAccessFile is used to point to move the pointer
     * of the file to a specific location
     * For the purposes of this function, it will start by pointing
     * to the start of the chunk and read until the end of the chunk
     */
    public void run() {
        // Create a file called output.txt
        File outputFile = new File("output." + fileType);
        if (outputFile.exists()) {
            try (RandomAccessFile output = new RandomAccessFile(outputFile, "rw")) {
                output.setLength(0); // Clear the file
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Read the chunk of the file from start to end
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            // Set the buffer equal to the length of the chunk
            byte[] buffer = new byte[(int) (end - start)];
            raf.seek(start);
            raf.read(buffer);
            // Write the buffer to the output file
            try (RandomAccessFile output = new RandomAccessFile(outputFile, "rw")) {
                output.seek(start);
                output.write(buffer);
            }
            System.out.println(new String(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
