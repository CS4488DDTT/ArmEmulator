/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package CS4488.Capstone.Translator;



import CS4488.Capstone.Library.BackEndSystemInterfaces.TranslatorInterface;
import CS4488.Capstone.Library.Tools.Hex4digit;

import java.util.ArrayList;

public class TranslatorFacade implements TranslatorInterface {

    private Translator translator;

    public TranslatorFacade() {

    }

    @Override
    public boolean loadFile(String armFile) throws Exception {
        this.translator = Translator.getInstance(armFile);
        return this.translator.isLoaded();
    }

    @Override
    public void clearFile() {
        translator.clearFile();
    }

    @Override
    public boolean isTranslatable() {

        return translator.getTranslatedCode() != null;
    }

    @Override
    public ArrayList<Hex4digit> translateToMachine() {
        return translator.getTranslatedCode();
    }

    @Override
    public String getLastExceptionMessage() {
        return null;
    }
}
