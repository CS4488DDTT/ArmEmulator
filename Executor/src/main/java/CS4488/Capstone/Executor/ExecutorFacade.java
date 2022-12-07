/*
Code Written by Thomas Neyman
 */
package CS4488.Capstone.Executor;

import CS4488.Capstone.Library.BackEndSystemInterfaces.ExecutorInterface;
import CS4488.Capstone.Library.Tools.MemoryHistorySpace;
import CS4488.Capstone.Library.Tools.ProgramState;
import CS4488.Capstone.Library.Tools.Hex4digit;

import java.util.ArrayList;

/*
========================================================
This class is used to interface with the rest of the application
when performing instructions. the next() function is called by
the orchestrator and all other functionality of the ExecutorFacade
is handled internally.
 */

public class ExecutorFacade implements ExecutorInterface {

    // Holds the index of what place the memory is at in the arraylist
    private int MEMORYSTATEINDEX = 0;
    private String exceptionMessage = "No Error";

    public int getMEMORYSTATEINDEX() {
        return MEMORYSTATEINDEX;
    }

    // Updates the index of what stage the memory is in
    private void incrementMemoryIndex() {
        MEMORYSTATEINDEX += 1;
    }

    // Adds the last location in memory of the program counter
    // to the pcHistory list in the programState
    private void updatePCHistory() {
        MemoryHistorySpace pc = new MemoryHistorySpace();
        pc.value = ProgramState.getInstance().registers[15];
        pc.memoryLocation = MEMORYSTATEINDEX;
        ProgramState.getInstance().pcHistory.add(pc);
    }

    // Simply checks the next instruction being executed, which will be
    // stored in register 15, and if the instruction is a read, the function
    // returns true.
    public boolean checkForRead() {
        Hex4digit inst = ProgramState.getInstance().memoryStateHistory.get(MEMORYSTATEINDEX).get(ProgramState.getInstance().registers[15].getValue());
        return inst.getFirst() == 'd';
    }

    // takes in a hex4digit instruction and based on the first value
    // executes the corresponding instruction from the InstructionSet class
    //
    // There is an option to instead just pass in the entire hex4digit object
    // into the function and let the function itself separate the values it needs,
    // but it makes the functions cleaner if they instead are given the exact
    // values they need, so that there isn't a bunch of variable assignments in
    // all the instructionSet functions
    private void determineInstruction(Hex4digit inst) {
        // ProgramState instance for passing into InstructionSet
        ProgramState state = ProgramState.getInstance();
        switch (inst.getHexChars()[1]) {
            case '0' -> InstructionSet.halt(state);
            case '1' -> InstructionSet.load(state, inst.getMiddle2Value(), inst.getHexChars()[4], MEMORYSTATEINDEX);
            case '2' -> InstructionSet.store(state, inst.getMiddle2Value(), inst.getHexChars()[4], MEMORYSTATEINDEX);
            case '3' -> InstructionSet.add(state, inst.getHexChars()[2], inst.getHexChars()[3], inst.getHexChars()[4]);
            case '4' -> InstructionSet.subt(state, inst.getHexChars()[2], inst.getHexChars()[3], inst.getHexChars()[4]);
            case '5' -> InstructionSet.mult(state, inst.getHexChars()[2], inst.getHexChars()[3], inst.getHexChars()[4]);
            case '6' -> InstructionSet.intDivide(state, inst.getHexChars()[2], inst.getHexChars()[3], inst.getHexChars()[4]);
            case '7' -> InstructionSet.loadIndirect(state, inst.getMiddle2Value(), inst.getHexChars()[4], MEMORYSTATEINDEX);
            case '8' -> InstructionSet.storeIndirect(state, inst.getMiddle2Value(), inst.getHexChars()[4], MEMORYSTATEINDEX);
            case '9' -> InstructionSet.branch(state, inst.getMiddle2Value());
            case 'a' -> InstructionSet.branchZero(state, inst.getHexChars()[2], inst.getLast2Value());
            case 'b' -> InstructionSet.branchNeg(state, inst.getHexChars()[2], inst.getLast2Value());
            case 'c' -> InstructionSet.branchPos(state, inst.getHexChars()[2], inst.getLast2Value());
            case 'd' -> InstructionSet.readInt(state, inst.getHexChars()[2]);
            case 'e' -> InstructionSet.writeInt(state, inst.getHexChars()[2]);
            case 'f' -> InstructionSet.skip(state);
            default -> unrecognizedInstruction(state);
        }
    }

    @Override
    public boolean next() {
        // First, check to see if a ProgramState has been instantiated
        if (!hasState()) {
            System.out.println("Did not have state");
            return false;
        }

        // Check to see if there is another instruction from the program Counter i.e. Register 15
        if (!hasNext()) {
            System.out.println("Did not have next instruction");
            return false;
        }

        // the next instruction that is going to be executed is indexed by the program Counter.
        // so in register 15, the index in memory is held for that instruction. So if the value
        // of register 15 is 7, that means index 7 in the memoryStateHistory will be executed.
        // instruction = ArrayList[ ArrayList[ ProgramCounter ] ]
        Hex4digit instruction = ProgramState.getInstance().memoryStateHistory.get(MEMORYSTATEINDEX).get(ProgramState.getInstance().registers[15].getValue());
        // figure out what the instruction is and execute it
        determineInstruction(instruction);

        // Update the pcHistory
        updatePCHistory();
        // The memory index then needs to be incremented since the list will be copied over 1
        incrementMemoryIndex();
        // Finally, the memory ArrayList needs to be copied over
        // to the next index in the list of lists itself
        ProgramState.getInstance().memoryStateHistory.add(MEMORYSTATEINDEX, ProgramState.getInstance().memoryStateHistory.get(MEMORYSTATEINDEX - 1));
        return true;
    }

    @Override
    public boolean hasNext() {
        // if the PC is equal or larger than the actual length of the memory list itself, then it has
        // gone beyond the reaches of the program and there is no command for it to access
        if (ProgramState.getInstance().registers[15].getValue() >= ProgramState.getInstance().memoryStateHistory.get(MEMORYSTATEINDEX).size()) {
            exceptionMessage = "Program Counter tried to access memory out of bounds";
            return false;
        }
        // if the program counter contains a -1, the halt command was executed
        if (ProgramState.getInstance().registers[15].getValue() == -1) {
            exceptionMessage = "A halt command was executed and the program has stopped";
            return false;
        }
        // The program counter is trying to access negative memory, which makes no sense
        if (ProgramState.getInstance().registers[15].getValue() < -1) {
            exceptionMessage = "Program Counter tried to index negative memory";
            return false;
        }
        return true;
    }

    @Override
    public void setProgramState(ProgramState state) {
        ArrayList<Hex4digit> code = new ArrayList<>();
        state.initializeState(code);
    }

    @Override
    public boolean hasState() {
        if (ProgramState.getInstance().memoryStateHistory.isEmpty()) {
            exceptionMessage = "No state has been created";
            return false;
        }
        return true;
    }

    @Override
    public void clearState() {
        MEMORYSTATEINDEX = 0;
        ProgramState.getInstance().clearProgramState();
    }

    @Override
    public String getLastExceptionMessage() {
        return exceptionMessage;
    }

    private void unrecognizedInstruction(ProgramState state) {
        exceptionMessage = "The given instruction was not recognized!";
        InstructionSet.halt(state);
    }
}
