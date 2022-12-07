package CS4488.Capstone.Library.Tools;

import CS4488.Capstone.Library.BackEndSystemInterfaces.FileManagerInterface;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



/**
 * The File Manager
 *
 * Just the ability to check a file and read it in, and save it for now.
 * Extensible features later.
 *
 * @version 1.0
 * @author Traae
 */
public class FileManager {
    private static String defaultMessage = "No Current Error";
    private String lastErrorMessage;



    // SINGLETON instance, private constructor, and getInstance.
    private static FileManager instance = null;
    private FileManager(){
        lastErrorMessage = defaultMessage;
    }
    public static FileManager getInstance(){
        if (instance == null){
            instance = new FileManager();

        }
        return instance;
    }

    /**
     * Pass in a file path, return a boolean for  is readable or not.
     * @param pathString
     * @return is/is not readable;
     */
    public boolean checkFile(String pathString) {
        boolean result = Files.isReadable(Paths.get(pathString));
        lastErrorMessage = defaultMessage;
        return result;
    }

    /**
     * Returns the file contents in a string, automatically opens and closes the stream.
     * @param pathString
     * @return string of file contents;
     */
    public String fileToString(String pathString) {
        String readIn = "";
        try {
            readIn = Files.readString(Paths.get(pathString));
            lastErrorMessage = defaultMessage;
        }
        catch (IOException e){
            lastErrorMessage = ("FileManager.fileToString() Failure:\n" +
                    e.toString() + "\n" + e.getMessage());
        }
        return readIn;
    }

    public String readFile(String file) {
        StringBuilder text = new StringBuilder();

        try{

            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                text.append(reader.readLine());
            }
            lastErrorMessage = defaultMessage;

        }catch (IOException e){
            lastErrorMessage = "File Manager: in readFile()\n" + e.getMessage();
        }

        return text.toString().toLowerCase();
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    private BufferedWriter writer;

    public boolean saveFile(String path, String Content){
        boolean result = false;
        result = setWriteFile(path);

        if (result){
            write(Content);
        }

        closeWrite();

        return result;

    }

    private boolean setWriteFile(String path){
        boolean result = false;
        try {
            Files.deleteIfExists(Path.of(path));
            Files.createFile(Path.of(path));
            writer = new BufferedWriter(new FileWriter(path));
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    private void write(String toWrite) {
        try {
            writer.write(toWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void closeWrite(){
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
