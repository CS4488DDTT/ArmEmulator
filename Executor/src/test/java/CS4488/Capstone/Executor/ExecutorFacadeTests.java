/*
Testing by Thomas Neyman
 */
package CS4488.Capstone.Executor;

import CS4488.Capstone.Library.Tools.Hex4digit;
import CS4488.Capstone.Library.Tools.ProgramState;
import org.junit.jupiter.api.*;
import java.util.ArrayList;

public class ExecutorFacadeTests {

    ExecutorFacade executor;

    @BeforeEach
    public void setUp() {
        // Fill the memory with values that will act as a program
        // The program should be as follows:
        //
        // br 3             pc = 0
        // #5               pc = 1
        // #8               pc = 2
        // ld 1 r0          pc = 3
        // ld 2 r1          pc = 4
        // add r0 r1 r2     pc = 5
        ArrayList<Hex4digit> program = new ArrayList<>();
        program.add(new Hex4digit(36912));
        program.add(new Hex4digit(5));
        program.add(new Hex4digit(8));
        program.add(new Hex4digit(4112));
        program.add(new Hex4digit(4129));
        program.add(new Hex4digit(12306));
        ProgramState.getInstance().memoryStateHistory.add(program);

        executor = new ExecutorFacade();
    }

    @AfterEach
    public void tearDown() {
        // clear the program state
        ProgramState.getInstance().clearProgramState();
        executor = null;
    }

    /*
    Things that should change with the next function:
    - Program counter should be '0' to start then update depending on the instruction executed
    - MEMORYSTATEINDEX should always start at '0' then increment by 1 after an instruction
    - The memory list should be copied over properly
    instruction execution itself will not be tested, that is for the instructionSetTests class
    */
    @Test
    @DisplayName("Test basic functionality of the next() function 1")
    public void testNext_1() throws Exception {
        // Check to see if all the values are properly initialized
        Assertions.assertEquals(0, ProgramState.getInstance().registers[15].getValue());
        Assertions.assertEquals(0, executor.getMEMORYSTATEINDEX());
        Assertions.assertEquals(36912, ProgramState.getInstance().memoryStateHistory.get(0).get(0).getValue());
        executor.next();
        // Program Counter should have branched to '3'
        Assertions.assertEquals(3, ProgramState.getInstance().registers[15].getValue());
        Assertions.assertEquals(1, executor.getMEMORYSTATEINDEX());
        Assertions.assertEquals(36912, ProgramState.getInstance().memoryStateHistory.get(1).get(0).getValue());
    }

    @Test
    @DisplayName("Test basic functionality of the next() function 2")
    public void testNext_2() throws Exception {
        executor.next();
        executor.next();
        ProgramState state = ProgramState.getInstance();
        Assertions.assertEquals(4, state.registers[15].getValue());
        Assertions.assertEquals(2, executor.getMEMORYSTATEINDEX());
        Assertions.assertEquals(4129, state.memoryStateHistory.get(executor.getMEMORYSTATEINDEX()).get(state.registers[15].getValue()).getValue());
    }

    @Test
    @DisplayName("Test basic functionality of the next() function 3")
    public void testNext_3() throws Exception {
        ProgramState state = ProgramState.getInstance();
        executor.next();
        executor.next();
        executor.next();
        Assertions.assertEquals(12306, state.memoryStateHistory.get(executor.getMEMORYSTATEINDEX()).get(state.registers[15].getValue()).getValue());
        executor.next();
        Assertions.assertEquals(6, state.registers[15].getValue());
        Assertions.assertEquals(4, executor.getMEMORYSTATEINDEX());
    }

    @Test
    @DisplayName("no more instructions remain")
    public void testNext_4() throws Exception {
        executor.next();
        executor.next();
        executor.next();
        executor.next();
        Assertions.assertFalse(executor.next());
        Assertions.assertEquals("Program Counter tried to access memory out of bounds", executor.getLastExceptionMessage());
    }

    @Test
    @DisplayName("Halt has been executed")
    public void testNext_5() throws Exception {
        // add a halt to the end of the program
        ProgramState.getInstance().memoryStateHistory.get(0).add(new Hex4digit(0));
        executor.next();
        executor.next();
        executor.next();
        executor.next();
        executor.next();
        Assertions.assertFalse(executor.next());
        Assertions.assertEquals("A halt command was executed and the program has stopped", executor.getLastExceptionMessage());
    }

    @Test
    @DisplayName("Program counter is less than -1")
    public void testNext_6() throws Exception {
        // add a subtraction instruction that will result in a negative number and place it in the program counter
        ProgramState.getInstance().memoryStateHistory.get(0).add(new Hex4digit(16415));
        executor.next();
        executor.next();
        executor.next();
        executor.next();
        executor.next();
        Assertions.assertFalse(executor.next());
        Assertions.assertEquals("Program Counter tried to index negative memory", executor.getLastExceptionMessage());
    }

    @Test
    @DisplayName("No state exists")
    public void testNext_7() throws Exception {
        ProgramState.getInstance().clearProgramState();
        Assertions.assertFalse(executor.next());
        Assertions.assertEquals("No state has been created", executor.getLastExceptionMessage());
    }

    @Test
    @DisplayName("Test clearing the state")
    public void testClearState() throws Exception {
        executor.next();
        executor.next();
        executor.next();

        executor.clearState();
        Assertions.assertEquals(0, executor.getMEMORYSTATEINDEX());
        Assertions.assertTrue(ProgramState.getInstance().memoryStateHistory.isEmpty());
    }

    @Test
    @DisplayName("Test for a state")
    public void testHasState() throws Exception {
        executor.next();
        Assertions.assertTrue(executor.hasState());
    }

}
