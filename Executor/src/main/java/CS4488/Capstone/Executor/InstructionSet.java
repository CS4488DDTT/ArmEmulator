/*
Code Written by Thomas Neyman
 */
package CS4488.Capstone.Executor;

import CS4488.Capstone.Library.Tools.Hex4digit;
import CS4488.Capstone.Library.Tools.ProgramState;

/*
========================================================
This class contains all the necessary functions that are
utilized in the Dragon Arm Emulator's instruction set, that
is, every possible instruction that can be executed by the
emulator. Below is a list of each instruction with a brief
description of their descriptions.

Halt - Ends the program
Load - Brings the value from memory location to the register.
Store - Brings the values from register to memory location.
Add - The 1st and 2nd Registers are added, result placed in 3rd.
Subtract - The 1st Register subtracts the 2nd, result placed in 3rd.
Multiply - The 1st and 2nd Registers are multiplied, result placed in 3rd.
Integer Divide - The 1st Register is divided by the Second, result placed in 3rd.
Load Indirect - Brings the value from location Pointed at by the Memory Location to the register.
Store Indirect - Brings the values from register to location Pointed at by the Memory Location.
Branch - Unconditionally branches to Memory Location by changing the Program Counter.
Branch Zero - If the value in Register is 0, branches to Memory Location.
Branch Negative - If the value in Register is negative, branches to Memory Location.
Branch Positive - If the value in Register is negative, branches to Memory Location.
Read Integer - Reads from Input box into Register.
Write Integer - Writes from register to the Output box.
Skip - Does nothing and goes to the next instruction.
 */

public final class InstructionSet {

    // The program counter is the register (r15) that stores the
    // memory address of the next instruction to be executed. It
    // needs to be incremented after an instruction to go the
    // next memory address where an instruction is held.
    public static void incrementProgramCounter(ProgramState state) {
        // This should simply be moving the index in the memory ArrayList. so
        // if you are executing the instruction at position 5 in the list, the
        // program counter will increment to position 6 next. This is not the
        // same as the actual memory address being incremented by 1. They are
        // independent in the emulation.
        state.registers[15].setValue((state.registers[15].getValue() + 1));
    }

    // putting a -1 into the program counter lets the executor know
    // that the program is finished when reading from register 15
    public static void halt(ProgramState state) {
        state.registers[15].setValue(-1);
    }

    // finds a memory location from the program state and takes its Hex4digit
    // value and sets it to the specified register's value.
    public static void load(ProgramState state, int mem, char reg, int index) {
        // Get the hex4digit object from the specified location in memory
        Hex4digit value = state.memoryStateHistory.get(index).get(mem);
        // put that hex4digit value into the specified register
        state.registers[Hex4digit.hexValue(reg)].setValue(value.getHexChars());
        incrementProgramCounter(state);
    }

    // get the specified memory index in the memory state and set its
    // Hex4digit value to that of the value in the specified register
    public static void store(ProgramState state, int mem, char reg, int index) {
        // get the Hex4digit object from the specified register
        Hex4digit value = state.registers[Hex4digit.hexValue(reg)];
        // set the memory spot specified to the value taken from the register
        state.memoryStateHistory.get(index).get(mem).setValue(value.getHexChars());
        incrementProgramCounter(state);
    }

    // add values of register 1 and register 2 and stores the result in register 3.
    // function converts the characters from hex to shorts then converts the result
    // back into hex to store in the resulting register.
    public static void add(ProgramState state, char reg1, char reg2, char reg3) {
        // add the values from the 2 given registers. Must be converted to shorts to do the addition
        int result = (state.registers[Hex4digit.hexValue(reg1)].getValue() + state.registers[Hex4digit.hexValue(reg2)].getValue());
        // set the result to the Hex4digit's value in the third given register
        state.registers[Hex4digit.hexValue(reg3)].setValue(result);
        incrementProgramCounter(state);
    }

    // subtract value within first register by the value within the second register
    // and store the result in the third register.
    // function converts the characters from hex to shorts then converts the result
    // back into hex to store in the resulting register
    public static void subt(ProgramState state, char reg1, char reg2, char reg3) {
        // subtract value from first register by the second register
        int result = (state.registers[Hex4digit.hexValue(reg1)].getValue() - state.registers[Hex4digit.hexValue(reg2)].getValue());
        // set the result to the Hex4digit's value in the third given register
        state.registers[Hex4digit.hexValue(reg3)].setValue(result);
        incrementProgramCounter(state);
    }

    // multiply the values from within first register and second register and store them
    // within third register.
    // function converts the characters from hex to shorts then converts the result
    // after multiplying back into hex to store in the third register.
    public static void mult(ProgramState state, char reg1, char reg2, char reg3) {
        // multiply values from first and second register together
        int result = (state.registers[Hex4digit.hexValue(reg1)].getValue() * state.registers[Hex4digit.hexValue(reg2)].getValue());
        // store the result in the third given register
        state.registers[Hex4digit.hexValue(reg3)].setValue(result);
        incrementProgramCounter(state);
    }

    // divides the value from register 1 by the value in register 2,
    // then stores the result in register 3
    public static void intDivide(ProgramState state, char reg1, char reg2, char reg3) {
        int result = (state.registers[Hex4digit.hexValue(reg1)].getValue() / state.registers[Hex4digit.hexValue(reg2)].getValue());
        state.registers[Hex4digit.hexValue(reg3)].setValue(result);
        incrementProgramCounter(state);
    }

    public static void loadIndirect(ProgramState state, int mem, char reg, int index) {
        Hex4digit address = state.memoryStateHistory.get(index).get(mem);
        Hex4digit value = state.memoryStateHistory.get(index).get(address.getValue());
        state.registers[Hex4digit.hexValue(reg)].setValue(value.getHexChars());
        incrementProgramCounter(state);
    }

    public static void storeIndirect(ProgramState state, int mem, char reg, int index) {
        // grab the hex4digit object that is in the specified register
        // This will hold the value to be placed in the indirect memory location
        Hex4digit value = state.registers[Hex4digit.hexValue(reg)];
        // Then, go to the specified memory location and take its hex4digit object
        // this value will be pointing to the actual memory address to be used
        Hex4digit address = state.memoryStateHistory.get(index).get(mem);
        // Now store the value into memory
        state.memoryStateHistory.get(index).get(address.getValue()).setValue(value.getHexChars());
        incrementProgramCounter(state);
    }

    // Change the program counter to be the specified memory index
    public static void branch(ProgramState state, int mem) {
        state.registers[15].setValue(mem);
    }

    // Changes the program counter to the specified memory index if
    // the value in the specified register is zero
    public static void branchZero(ProgramState state, char reg, int mem) {
        if (state.registers[Hex4digit.hexValue(reg)].getValue() == 0) {
            state.registers[15].setValue(mem);
        } else {
            incrementProgramCounter(state);
        }
    }

    // Changes the program counter to the specified memory index if
    // the value in the specified register is negative
    public static void branchNeg(ProgramState state, char reg, int mem) {
        if (state.registers[Hex4digit.hexValue(reg)].getValue() < 0) {
            state.registers[15].setValue(mem);
        } else {
            incrementProgramCounter(state);
        }
    }

    // Changes the program counter to the specified memory index if
    // the value in the specified register is positive
    public static void branchPos(ProgramState state, char reg, int mem) {
        if (state.registers[Hex4digit.hexValue(reg)].getValue() >= 0) {
            state.registers[15].setValue(mem);
        } else {
            incrementProgramCounter(state);
        }
    }

    // Takes a hex4d from the input of the program state and assigns that
    // value into the specified register location
    public static void readInt(ProgramState state, char reg) {
        state.registers[Hex4digit.hexValue(reg)].setValue(state.input.getValue());
        incrementProgramCounter(state);
    }

    // Takes a hex4d from a specified register and assigns that value
    // to the program state's output
    public static void writeInt(ProgramState state, char reg) {
        state.output.setValue(state.registers[Hex4digit.hexValue(reg)].getHexChars());
        incrementProgramCounter(state);
    }

    // literally just skips to the next instruction
    public static void skip(ProgramState state) {
        incrementProgramCounter(state);
    }

}
