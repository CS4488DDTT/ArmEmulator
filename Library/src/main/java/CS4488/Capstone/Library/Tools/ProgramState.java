package CS4488.Capstone.Library.Tools;

import CS4488.Capstone.Library.BackEndSystemInterfaces.ProgramStateInterface;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * This class is the Emulator's program sate.
 * It's just a data class, but a complex one.
 *
 * This Class is a Double-ton, like a singleton pattern but with a main instance and a cloned instance.
 *
 * The primary instance is for operation by the executor.
 * The clone is not operated on, thus making it read only, any writing to it will do nothing.
 *
 * Note: the readableState is only copied when called upon,
 * if the main instance is used it will bypass the excess processing power.
 *
 * @version 1.0
 * @author Traae
 */
public class ProgramState implements ProgramStateInterface {
    public static final int TOTAL_MEMORY_SPACES = 256; // 0 - 255

    // Constants and Class instances
    private static final int REGISTER_COUNT = 16;
    private static ProgramState emulationState=null;

    // Sub-class for tracking the program counter's history

    // INSTANCE VARIABLES
    // Registers
    public Hex4digit[] registers;
    // I/O
    public Hex4digit input, output;
    // Memory & State
    public ArrayList<MemoryHistorySpace> pcHistory;
    public ArrayList<ArrayList<Hex4digit>> memoryStateHistory;

    // PRIVATE SINGLETON CONSTRUCTOR
    private ProgramState(){
        registers = new Hex4digit[REGISTER_COUNT];
        for (int i=0; i<REGISTER_COUNT; i++){
            registers[i] = new Hex4digit();
        }
        input = new Hex4digit();
        output = new Hex4digit();
        pcHistory = new ArrayList<>();
        memoryStateHistory = new ArrayList<>();
    }

    // SINGLETON INSTANCE GETTER
    public static ProgramState getInstance(){
        if (emulationState == null){
            emulationState = new ProgramState();
        }
        return emulationState;
    }

    // Program State Interface
    @Override
    public boolean initializeState(ArrayList<Hex4digit> code) {
        boolean result = false;

        if ((code != null) && (code.size() > 0)){

            MemoryHistorySpace pc = new MemoryHistorySpace();
            pc.memoryLocation = 0;
            pc.value.setValue(code.get(0).getHexChars());
            pcHistory.add(pc);

            fillOutMemory(code);
            memoryStateHistory.add(code);

            result = true;
        }
        return result;
    }

    private void fillOutMemory(ArrayList<Hex4digit> toFillOut){
        while (toFillOut.size() < TOTAL_MEMORY_SPACES){
            toFillOut.add(new Hex4digit(0));
        }
    }

    @Override
    public void clearProgramState() {
        for (int i=0; i<REGISTER_COUNT; i++){
            registers[i].setValue(0);
        }
        input.setValue(0);
        output.setValue(0);
        for (ArrayList<Hex4digit> h: memoryStateHistory) {h.clear();}
        memoryStateHistory.clear();
        pcHistory.clear();
    }

    public String printableProgramState(){
        StringBuilder stateSummary = new StringBuilder();
        stateSummary.append("Input: " + input.getString() + "\n");
        stateSummary.append("Output: " + output.getString() + "\n");

        stateSummary.append("PC: " + registers[registers.length-1].getString());
        stateSummary.append("\nRegisters: ");
        int size = registers.length-1;

        for (int i=0; i<size; i++){
            stateSummary.append(registers[i].getString() + ", ");
        }
        stateSummary.append("\n");

        int lastState = memoryStateHistory.size() - 1;
        int blankCounter = 0;
        stateSummary.append("\nNext:");

        for (int i=0; i<memoryStateHistory.get(lastState).size(); i++){
            Hex4digit toAdd = memoryStateHistory.get(lastState).get(i);

            if (toAdd.getValue() == 0) { blankCounter++;}
            if (blankCounter > 10) { break; }

            stateSummary.append(toAdd.getString() + " ");

        }
        stateSummary.append("\n");

        return stateSummary.toString();
    }

    /**
     * This is weird but neccessary. Returns a Hex4digit object from memoryStateHistory.
     * @param n
     * @return
     */
    public Hex4digit getMemoryStateHistoryValue(int n){
        return memoryStateHistory.get(0).get(n);
    }

}


//    // read only clone function.
//    public void writeStateToReadable(){
//        // copy the register values
//        for (int i=0; i<REGISTER_COUNT; i++){
//            readableState.registers[i].setValue(emulationState.registers[i].getHexChars());
//        }
//        // copy i/o
//        readableState.input.setValue(emulationState.input.getHexChars());
//        readableState.output.setValue(emulationState.output.getHexChars());
//        // copy the list of memory spaces
//        readableState.pcHistory.clear();
//        for (memorySpace m: emulationState.pcHistory){
//            memorySpace toAdd = new memorySpace();
//            toAdd.value.setValue(m.value.getHexChars());
//            toAdd.memoryLocation = m.memoryLocation;
//            readableState.pcHistory.add(toAdd);
//        }
//        // copy the list of lists of program memory.
//        memoryStateHistory.clear();
//        for (ArrayList<Hex4digit> arrayHex : emulationState.memoryStateHistory){
//            ArrayList<Hex4digit> listToAdd = new ArrayList<>();
//            for (Hex4digit h : arrayHex){
//                Hex4digit hexToAdd = new Hex4digit();
//                hexToAdd.setValue(h.getHexChars());
//            }
//            memoryStateHistory.add(listToAdd);
//        }
//    }
