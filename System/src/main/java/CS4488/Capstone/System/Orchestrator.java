/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package CS4488.Capstone.System;



import CS4488.Capstone.Executor.ExecutorFacade;
import CS4488.Capstone.Library.FacadeInterfaces.ExecutorAccess;
import CS4488.Capstone.Library.FacadeInterfaces.NumberConverterAccess;
import CS4488.Capstone.Library.FacadeInterfaces.ProgramStateAccess;
import CS4488.Capstone.Library.FacadeInterfaces.TranslatorAccess;
import CS4488.Capstone.Library.Tools.FileManager;
import CS4488.Capstone.Library.Tools.HexadecimalConverter;
import CS4488.Capstone.Library.Tools.ProgramState;
import CS4488.Capstone.Translator.TranslatorFacade;

/**
 * The Back End Facade Class that orchestrates everything else.
 *
 * @version 0.0.9
 * @author Traae
 */
public class Orchestrator implements ProgramStateAccess, TranslatorAccess, ExecutorAccess, NumberConverterAccess {
    // SINGLETON INSTANCE
    private static Orchestrator instance = null;

    // INSTANCE VARIABLES
    private ProgramState state;
    private TranslatorFacade translator;
    private ExecutorFacade executor;
    private FileManager fileManager;
    private String error;



    private Orchestrator(){
        state = ProgramState.getInstance();
        // translator = new TranslatorFacade();
        executor = new ExecutorFacade();
        fileManager = FileManager.getInstance();
        this.resetError();
    }

    public static Orchestrator getInstance() {
        if (instance==null){
            instance = new Orchestrator();
        }
        return instance;
    }

    private void resetError(){
        error = "Orchestrator: No Error.";
    }

    public String getError() {
        return error;
    }

    @Override
    public boolean next() {
        resetError();
        boolean result = executor.hasState();
        if (!result){
            error = executor.getLastExceptionMessage();
        }
        else {
            result = executor.hasNext();
            if (!result){
                error = executor.getLastExceptionMessage();
            }
        }
        if (result){
            result = executor.next();
            if (!result){
                error = executor.getLastExceptionMessage();
            }
        }
        return result;
    }

    public void clearProgram(){
        executor.clearState();
    }


    @Override
    public ProgramState getProgramState() {
            return ProgramState.getInstance();
    }

    @Override
    public void sendInput(char[] input) {
        state.input.setValue(input);
    }

    @Override
    public char[] getOutput() {
        return state.output.getHexChars();
    }

    @Override
    public boolean translateAndLoad(String path) throws Exception {
        resetError();
        boolean result = translator.loadFile(path);
        if (result) {

            result = translator.isTranslatable();
            if (result){
                state.clearProgramState();
                state.initializeState(translator.translateToMachine());
                executor.setProgramState(state);
                translator.clearFile();
            }
            else {
                error = translator.getLastExceptionMessage();
            }
        }
        else {
            error = translator.getLastExceptionMessage();
        }

        return result;
    }


    @Override
    public char[] convertToHexChars(Short number) {
        return HexadecimalConverter.decimalToHex(number);
    }

    @Override
    public int convertToInt(char[] number) {
        return HexadecimalConverter.hexToDecimal(number);
    }
}
