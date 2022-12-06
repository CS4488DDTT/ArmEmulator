package CS4488.Capstone.UserInterface;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import CS4488.Capstone.System.Orchestrator;

import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;


import java.io.File;

public class EmulatorGUIController {
    private final Orchestrator orc = Orchestrator.getInstance();

    private String[][] RAM;

    /*
    We are using this to limit the amount of states we will record in the UI
    */
    private int totalStates = 0;

    private final int maxDisplayableStates = 499;

    private boolean badBool = false;

    private File loadedProgram;

    @FXML
    private Button exitButton;

    @FXML
    private Button loadButton;

    @FXML
    private Button assembleButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button runButton;

    @FXML
    private Button abortButton;

    @FXML
    private Button hexConverterDecimalToHexButton;

    @FXML
    private TextField pc;

    @FXML
    private TextField R0;

    @FXML
    private TextField R1;

    @FXML
    private TextField R2;

    @FXML
    private TextField R3;

    @FXML
    private TextField IN;

    @FXML
    private TextField OUT;

    @FXML
    private TextField decToHexConversionTextField;

    @FXML
    private TextField hexToDecConversionTextField;

    @FXML
    private Button debug;

    @FXML
    private TextArea inputBox;

    @FXML
    private TextArea memoryTable;

    @FXML
    private TextFlow memoryTable2;

    @FXML
    private Button hexConverterHexToDecimalButton;

    public EmulatorGUIController() {


    }
    //Decimal Conversion Methods And Buttons

    /**
     * Convert a decimal to hex
     * @param actionEvent
     */
    @FXML
    void decimalToHex(ActionEvent actionEvent){

        String stringDecimal = decToHexConversionTextField.getText();


        try{

            short decimal = Short.parseShort(stringDecimal);
            decToHexConversionTextField.setText(String.copyValueOf(orc.convertToHexChars(decimal)));
        }
        catch(Exception ignored){
        }







    }

    /**
     * Convert hex to decimal
     * @param actionEvent
     */
    @FXML
    void hexToDecimal(ActionEvent actionEvent){
        String stringHex = hexToDecConversionTextField.getText();
        try{
        char[] charHex = stringHex.toCharArray();
            hexToDecConversionTextField.setText(String.valueOf(orc.convertToInt(charHex)));
        }
        catch (Exception ignored){
        }





    }

    //File Management Methods and Buttons

    /**
     * Load a file into the input box and load the program from the file
     * @param actionEvent
     */
    @FXML
    void loadFile(ActionEvent actionEvent){

        FileChooser txtChooser = new FileChooser();
        txtChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Txt files","*.txt"));
        File file = txtChooser.showOpenDialog(null);
        if (file != null){
            orc.clearProgram();
            loadedProgram = file;
            inputBox.setText(orc.loadFile(file.getAbsolutePath()));
            orc.translateAndLoad(file.getAbsolutePath());
        }
        totalStates = 0;
        badBool = false;
        initializeMemoryTable();

    }

    //Memory table Methods and Buttons

    /**
     * initialize the memory table, need to refactor

     */
    @FXML
    void initializeMemoryTable(){
        short annoying = 0;
        RAM = new String[256][500];
        for (int i = 1;i<255;i++){
            for(int j = 1; j < 2; j++){
                RAM[i][j] = String.valueOf(orc.convertToHexChars(annoying));
            }
        }
        RAM[0][0] = "Loc     ";
        for(int i = 1;i<2;i++){
            short painful = (short)(i-1);
            RAM[0][i] = String.valueOf(orc.convertToHexChars(painful));
        }
        for(int i = 1;i<255;i++){
            short location = (short)(i-1);
            RAM[i][0] = String.valueOf(orc.convertToHexChars(location));
        }
        printInitialRAMValues();

    }

    /**
     * Print the intial ram values
     */
    @FXML
    void printInitialRAMValues(){

        String memArray = "";
        for (int i = 0; i< 255;i++){
            for( int j = 0; j < 2; j++){
                memArray += RAM[i][j] + "  ";
            }
            memArray += "\n";

        }
        memoryTable.setText(memArray);






    }
    //Update RAM values, incomplete
    @FXML
    void updateRAMValuesInDisplay(){


        setRAMValues(totalStates);
        if(!maxStatesReached()){
            memoryTable.clear();
            String memArray = "";
            for (int i = 0; i< 255;i++){
                for( int j = 0; j < totalStates-1; j++){
                    memArray += RAM[i][j] + "  ";
                }
                memArray += "\n";

            }

            memoryTable.setText(memArray);

        }
        else if(maxStatesReached() && !badBool){
            badBool = true;
            Alert weCantShowMore = new Alert(Alert.AlertType.WARNING);
            weCantShowMore.setContentText("Maximum possible number of states to be displayed reached, state history can not be updated in display!");
            weCantShowMore.showAndWait();
        }





    }

    /**
     * Sets RAM values at a point in time
     * @param n

     */
    @FXML
    private void setRAMValues(int n) {
        if(n>=maxDisplayableStates){
            throw new RuntimeException();
        }
        RAM[0][n]= String.valueOf(orc.convertToHexChars(Short.valueOf(String.valueOf(n))));

        for (int i = 1;i<255;i++){
            RAM[i][n] = orc.getProgramState().getMemoryStateHistoryValue(i).getString();

        }




    }

    @FXML
    private boolean maxStatesReached() {

        if(totalStates >= maxDisplayableStates){
            return true;
        }
        return false;
    }



    /**
     * Execute step once
     * @param actionEvent
     */
    @FXML
    void next(ActionEvent actionEvent){
        executeStep();
    }

    /**
     * Runs the program without stopping, unless the program has ended or an error is reached
     * @param actionEvent
     */
    @FXML
    void run(ActionEvent actionEvent) throws InterruptedException {

        while(true) {
            if (orc.getProgramState().registers[15].getValue() == -1){
                Alert end = new Alert(Alert.AlertType.WARNING);
                end.setContentText("End of file reached");
                end.showAndWait();

                break;
            }

            if (orc.getError() == "Orchestrator: No Error.") {
                executeStep();

            }
            else if (orc.getError() != "Orchestrator: No Error."){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(orc.getError());
                alert.showAndWait();
                break;
            }


        }



    }

    /**
     * Sets the value of the registers
     */
    @FXML
    void getRegisters(){
        R0.setText(String.valueOf(orc.getProgramState().registers[0].getHexChars()));

        R1.setText(String.valueOf(orc.getProgramState().registers[1].getHexChars()));

        R2.setText(String.valueOf(orc.getProgramState().registers[2].getHexChars()));

        R3.setText(String.valueOf(orc.getProgramState().registers[3].getHexChars()));

        pc.setText(String.valueOf(orc.getProgramState().registers[15].getValue()));



        OUT.setText(orc.getProgramState().output.getString());



    }



    /**
     * abort the currently running program. Can be used while program is running step by step or running in totality
     */
    @FXML
    void abortProgram(){
        orc.clearProgram();


    }

    /**
     * executes one step through the code, unless the end of the program is reached or an error is reached
     */
    @FXML
    void executeStep(){

        incrimententTotalStates();

        System.out.println(orc.getProgramState().output.getString());

        getRegisters();

        try{
            if(orc.getError() != "Orchestrator: No Error."){
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText(orc.getError());
                error.showAndWait();
                abortProgram();
                orc.translateAndLoad(loadedProgram.getAbsolutePath());
            }
            else if (orc.getProgramState().registers[15].getValue() != -1){



                if(IN.getText() != ""){
                    orc.sendInput(orc.convertToHexChars(Short.valueOf(IN.getText())));
                }

                orc.next();



            }
            else if (orc.getProgramState().registers[15].getValue() == -1){

                Alert end = new Alert(Alert.AlertType.WARNING);
                end.setContentText("End of file reached");

                end.showAndWait();



            }

        } catch (Exception e){
            System.out.println("I am in execute step");
        }

        //I was right here getting updating of ram values working

        updateRAMValuesInDisplay();
    }
    @FXML
    void incrimententTotalStates(){
        totalStates +=1;
    }

    @FXML
    void assemble(ActionEvent actionEvent){
        orc.clearProgram();
        inputBox.setText(orc.loadFile(loadedProgram.getAbsolutePath()));
        orc.translateAndLoad(loadedProgram.getAbsolutePath());
        memoryTable.clear();

        totalStates = 0;
        badBool = false;
        initializeMemoryTable();

        getRegisters();
    }


    /**
     * Exit the program
     */
    @FXML
    void exit(){
        System.exit(0);
    }





}
