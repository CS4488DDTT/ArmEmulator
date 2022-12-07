
package CS4488.Capstone.Translator;


import CS4488.Capstone.Library.Tools.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * It reads a file, parses it, converts it to hexadecimal, and sets the translated code to the hexadecimal code.
 * <p>
 * * @version 0.0.9
 * * @author Daniel Igbokwe
 */
public class Translator {
    // Instance variables
    private String armFile;
    private boolean loaded;
    private ArrayList<Hex4digit> translatedCode = null;
    private final FileManager fileMan = FileManager.getInstance();
    private static Translator singleton;
    private String exceptionMessage = "No Error";
    private boolean isTranslatable = false;


    private Translator(String armFile) {
       setTranslatable(this.translate(armFile));
    }


    /**
     * It reads the file, parses it, converts it to hexadecimal, and sets the translated code to the hexadecimal code
     *
     * @param armFile The file to be translated.
     * @return A boolean value.
     */
    private boolean translate(String armFile) {
        this.setArmFile(this.readFile(armFile));
        // Check and set if file is read successfully
        this.setLoaded(!(this.getArmFile().isEmpty()));

        // Translate if loaded
        if (this.isLoaded()) {
            // Parse Arm-file for hex conversion
            String[] parsedFile = this.parseFile(this.getArmFile());
            // convert to hex code
            ArrayList<Hex4digit> hex4dCode = null;
            if(parsedFile != null){
                hex4dCode = this.convertToHex(parsedFile);
            }


            // if lines don't exceed memory space
            if (hex4dCode != null && hex4dCode.size() <= 256 ) {
                this.setTranslatedCode(hex4dCode);
                return this.getTranslatedCode() != null;

            } else {
                this.setExceptionMessage("System Memory overflow, Lines exceed 256.");
                //System.out.println(this.getExceptionMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * This function returns a boolean value that indicates whether the current object is translatable or not
     */
    public boolean isTranslatable() {
        return this.isTranslatable;
    }

    /**
     * This function sets the translatable property of the current object to the value of the parameter.
     *
     * @param translatable If true, the text will be translated.
     */

    public void setTranslatable(boolean translatable) {
        this.isTranslatable = translatable;
    }

    /**
     * If the singleton is null, create a new Translator object and assign it to the singleton. Otherwise, return the
     * singleton
     *
     * @param armFile The name of the ARM file to be translated.
     * @return The singleton instance of the Translator class.
     */

    public static Translator getInstance(String armFile) {
        if (singleton == null) {
            singleton = new Translator(armFile); // initialize translator
        } else {
            singleton.translate(armFile); // if translate new arm file
        }
        return singleton;
    }


    /**
     * > This function sets the value of the loaded variable to the value of the loaded parameter
     *
     * @param loaded This is a boolean value that tells us whether the data has been loaded or not.
     */

    private void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * This function sets the armFile variable to the value of the armFile parameter.
     *
     * @param armFile The path to the ARM template file.
     */
    public void setArmFile(String armFile) {
        this.armFile = armFile;
    }

    /**
     * This function sets the translated code of the object to the given translated code
     *
     * @param translatedCode This is the arraylist that will hold the translated code.
     */

     public void setTranslatedCode(ArrayList<Hex4digit> translatedCode) {
        this.translatedCode = translatedCode;
    }

    /**
     * This function returns the armFile variable
     *
     * @return The armFile variable is being returned.
     */

     public String getArmFile() {
        return this.armFile;
    }

    /**
     * Returns true if the object is loaded, false otherwise.
     *
     * @return The boolean value of the loaded variable.
     */

     public boolean isLoaded() {
        return this.loaded;
    }

    /**
     * This function returns the translated code of the current object
     *
     * @return The translated code.
     */

     public ArrayList<Hex4digit> getTranslatedCode() {
        return this.translatedCode;
    }


    /**
     * This function clears the file name, sets the loaded flag to false, and clears the translated code
     */
    public void clearFile() {
        setArmFile("");
        setLoaded(false);
        setTranslatedCode(null);
        //System.out.println("Translator: Cleared all files.");
    }


    /**
     * It creates a new array of size one less than the original array, copies all the elements except the element at the
     * index to the new array, and returns the new array
     *
     * @param arr   The array from which you want to remove an element.
     * @param index The index of the element to be removed.
     * @return The array with the element removed.
     */
     private String[] removeTheElement(String[] arr, int index) {

        // If the array is empty
        // or the index is not in array range
        // return the original array
        if (arr == null || index < 0
                || index >= arr.length) {

            return arr;
        }

        // Create another array of size one less
        String[] anotherArray = new String[arr.length - 1];

        // Copy the elements except the index
        // from original array to the other array
        for (int i = 0, k = 0; i < arr.length; i++) {

            // if the index is
            // the removal element index
            if (i == index) {
                continue;
            }

            // if the index is not
            // the removal element index
            anotherArray[k++] = arr[i];
        }

        // return the resultant array
        return anotherArray;
    }


    /**
     * This function returns the exception message
     *
     * @return The exception message.
     */
    public String getExceptionMessage() {
        return this.exceptionMessage;
    }

    /**
     * This function sets the exception message
     *
     * @param exceptionMessage The message that will be displayed to the user.
     */
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * This function takes a string and removes all comments from it
     *
     * @param line The line of code that is being read in.
     * @return The line without the comments.
     */
    private String removeComments(String line) {
        //(?<=@).*?(?=@)

        //System.out.println("Parsing out Comments (@ comment @).");

        String lineCopy = line;
        while (lineCopy.contains("@")) {

            int firstIndex = lineCopy.indexOf('@');


            if(firstIndex == lineCopy.lastIndexOf('@')){
                this.setExceptionMessage("Error occurred parsing comments. Please check your comments.");
               // System.out.println(this.getExceptionMessage());
                return "";
            }
            if (firstIndex + 1 < lineCopy.length()) {
                int endIndex = lineCopy.indexOf('@', firstIndex + 1);

                if (endIndex + 1 <= lineCopy.length()) {
                    lineCopy = lineCopy.replace(lineCopy.substring(firstIndex, endIndex + 1), "");
                }

            }
        }
        //System.out.println("Comments parsed out.");
        return lineCopy;
    }





    /**
     * This function takes in a file of instructions and parses it to find any inline hex numbers and replaces them with a
     * memory location
     *
     * @param file the file that is being read in
     * @return The memory array is being returned.
     */
    private String [] parseInLineHexNumbers(String [] file){
       int end = file.length;
       int start = 256; // max memory space

       if(end < 256){
           String [] memory = initializeArray();
           Pattern pattern = Pattern.compile("[0-9]{4}");

           for(int i = 0; i< file.length; i++){
               String newLine = file[i].trim();
               Matcher matcher = pattern.matcher(newLine);
               if(matcher.find() &&  newLine.length() > 5){

                   String labelAddress = "m"+Integer.toHexString(start -1);
                   String number = matcher.group();

                   memory[start - 1] = number;

                   // replace number with memory location
                   newLine = newLine.replaceAll(number, labelAddress);

                   start--;
               }

               // set line instruction
               memory[i] = newLine;

           }
           return memory;
       }

       return file;
    }


    /**
     * It reads a file and returns the contents as a string
     *
     * @param file The ARM file to read from.
     * @return A string of the text in the file.
     */

    private String readFile(String file) {
       // System.out.println("Reading File from provided path. ");
        return fileMan.readFile(file);
    }


    /**
     * It takes a string of ARM assembly code, removes all comments, replaces all hexadecimal instructions with their short
     * equivalent, and returns an array of strings, each of which is a line of assembly code
     *
     * @param armFile The file that contains the ARM assembly code.
     * @return An array of strings, each string is a line of the file.
     */
    private String[] parseFile(String armFile) {
       // System.out.println("Successfully read file. Parsing out Comments, #, 0x.");
        String noComments = removeComments(armFile)
                .replaceAll("0x", "")
                .replaceAll("#", "");

       // System.out.println("");

        if(noComments.isEmpty()){
            return  null;
        }

        return noComments.split(";");
    }

    private void parseOutLabels(String [] file){
       // System.out.println("Removing Labels.");
        for(int i = 0; i < file.length; i++){
            String line = file[i];
            if (line.contains(":")) {
                // replace all label occurrence
                this.setLabels(line.replaceAll("\\s", "")
                        , file, i);
            }
        }
       // System.out.println("Labels Removed.");
    }


    /**
     * This function initializes the memory array by creating a new array of 256 elements and filling it with empty strings
     *
     * @return An array of 256 Strings, each initialized to the empty String.
     */
    private String[] initializeArray(){
        String [] memory = new String[256];
        Arrays.fill(memory, "");
        return memory;
    }


    /**
     * This function takes a line of code, the parsed file, and the line index. It then splits the line of code into an
     * array, and if the line of code ends with a colon, it removes the element from the parsed file. Otherwise, it
     * replaces the label with an empty string. It then replaces the label with the label address
     *
     * @param lineOfCode The line of code that is being parsed.
     * @param parsedFile The array of strings that contains the parsed file.
     * @param lineIndex  The index of the line of code in the parsedFile array.
     */
    private void setLabels(String lineOfCode, String[] parsedFile, int lineIndex) {
        String[] lineArray = lineOfCode.split(":");


        // checks if label is defined on one line
        if (lineOfCode.endsWith(":")) {
            parsedFile = removeTheElement(parsedFile, lineIndex);
        } else {
            parsedFile[lineIndex] = parsedFile[lineIndex].replaceAll(lineArray[0] + ":", "");
        }

        String labelAddress;
        if (lineIndex < 10){
             labelAddress = "0"+Integer.toHexString(lineIndex);
        }else{
            labelAddress = Integer.toHexString(lineIndex);
        }

        for (int i = 0; i < parsedFile.length; i++) {
            parsedFile[i] = parsedFile[i].replaceAll(lineArray[0], labelAddress);
        }


//        this.replaceLabels(lineArray[0], labelAddress, parsedFile);
    }



    /**
     * This function takes in a string array of the parsed file and returns an arraylist of hex4digit objects
     *
     * @param parsedFile This is the file that has been parsed by the Parser class.
     * @return An arraylist of hex4digit objects
     */
    public ArrayList<Hex4digit> convertToHex(String[] parsedFile) {

        this.parseOutLabels(parsedFile);
        InstructionParser instructionParser = InstructionParser.getInstance();
        parsedFile = this.parseInLineHexNumbers(parsedFile);

        ArrayList<Hex4digit> translatedFile = initializeHexMemory();

        for (int lineIndex = 0; lineIndex < parsedFile.length; lineIndex++) {
            String line = parsedFile[lineIndex];
            StringBuilder builder = new StringBuilder();

            // remove trailing space
            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }


            // split on each instruction
            for (String elem : line.split(" ")) {
                String instruction = "";
                if (!elem.isEmpty()) {
                    instruction = instructionParser.getParser().get(elem);
                }

                // adds instruction code to string if it is valid
                // else append unknown instruction
                builder.append(Objects.requireNonNullElse(instruction, elem));
            }

//            System.out.println("Line: "+line);
//


            if (builder.length() > 4 && builder.charAt(0) != '-') {
                String exception = String.format("Instruction memory overflow occurred at Line %d. \n" +
                        "Line: %s" , lineIndex, builder);
                this.setExceptionMessage(exception);
                this.clearFile();
               // System.out.println(this.getExceptionMessage());
                return null;
            } else {
                int stop = 5;
                // not negative instruction
                if (builder.charAt(0) != '-') {
                    stop = 4;
                }

                // stop at 4 if non-negative
                while (builder.length() < stop) {
                    builder.append("0");
                }

            }

//            System.out.println("After checking Translated instruct:"+builder);


            String lineOfCode = builder.toString();
            // check if line matches hex format
            if (lineOfCode.matches("^[-0-9a-f]+$")) {
                // create hex digit
                Hex4digit hex = new Hex4digit();
                hex.setValue(lineOfCode);
                translatedFile.set(lineIndex, hex); // adds hex code to list
            } else {
                String exception = String.format("Line %d contains unknown instructions.", lineIndex);
                this.setExceptionMessage(exception);
                this.clearFile();
               // System.out.println(this.getExceptionMessage());
                return null;
            }


        }
       // System.out.println("File successfully converted to hex code.");

        return translatedFile;
    }


    /**
     * This function initializes the memory array with 256 Hex4digit objects
     *
     * @return An ArrayList of 256 Hex4digit objects.
     */

    private ArrayList<Hex4digit> initializeHexMemory(){
        ArrayList<Hex4digit> memory = new ArrayList<Hex4digit>(256);

        for(int i = 0; i< 256; i++){
            memory.add(new Hex4digit());
        }
        return memory;
    }




//    public static void main(String[] args) throws Exception {
//        //"Translator/src/main/java/CS4488/Capstone/Translator/Program 8 - Random Instructions.txt"
//        //"Example Code/Program 1, Hello Branch and Math.txt"
//        //"Example Code/Program 2, 4 Input 4 Operations.txt"
//        //"Example Code/Program 3, Hello Memory.txt"
//        //"Example Code/Program 4, Hello In Out.txt"
//        //Example Code/Program 6, Dangerous Input.txt
//        //Example Code/Program XYZ, TestingCoverage.txt
//        //ResourceDirectories/translationTester.txt
//        //Translator/ResourceDirectories/Example Code/Program XYZ, TestingCoverage.txt
//        //"Translator/ResourceDirectories/bad comment.txt"
//
//        Translator translator = new Translator("");
//        //@@--[\s\S]*--@@
//        //(?<=@).*?(?=@)
//
//
//
//
//        String file = translator.readFile("Translator/ResourceDirectories/translationTester.txt");
//
//
//        String[] parsedFile = translator.parseFile(file);
//
//        if(parsedFile != null){
//            for(int i  = 0; i< parsedFile.length; i++){
//                System.out.println(i + ") " + parsedFile[i]);
//            }
//
//            translator.parseOutLabels(parsedFile);
//            System.out.println("\n");
//
//            for(int i  = 0; i< parsedFile.length; i++){
//                System.out.println(i + ") " + parsedFile[i].trim());
//            }
//            System.out.println();
//
//            String [] newFile = translator.parseInLineHexNumbers(parsedFile);
//
//            for(int i  = 0; i< newFile.length; i++){
//                System.out.println(i + ") " + newFile[i]);
//            }
//
//
//            System.out.println("Translation: \n");
//
//            ArrayList<Hex4digit> translatedCode = translator.convertToHex(parsedFile);
//            System.out.println(translatedCode.size() + "\n");
//
//            for(int i  = 0; i< translatedCode.size(); i++){
//
//                System.out.println(i + ") " + translatedCode.get(i).getString());
//
//            }
//
//        }else{
//            System.out.println("Error parsing file");
//        }
//
//
//
//
//    }


}
