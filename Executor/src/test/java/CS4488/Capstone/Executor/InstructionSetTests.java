/*
Testing by Thomas Neyman
 */
package CS4488.Capstone.Executor;

import CS4488.Capstone.Library.Tools.Hex4digit;
import CS4488.Capstone.Library.Tools.ProgramState;
import org.junit.jupiter.api.*;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

/*
Testing class for the instruction set. Notice that the ExecutorFacade
is responsible for updating the MEMORYSTATEINDEX which is used to
track the progress of the memory as instructions are executed. This
is not the job of the instruction set, therefore, the MEMORYSTATEINDEX
for every function will be '0'.
 */

public class InstructionSetTests {

    ProgramState state = ProgramState.getInstance();

    @BeforeEach
    public void setUp() {
        // Fill the memory with a program designed for testing
        ArrayList<Hex4digit> program = new ArrayList<>();
        program.add(new Hex4digit("9030"));     // branch to memory 3
        program.add(new Hex4digit("0005"));     // value of 5
        program.add(new Hex4digit("0008"));     // value of 8
        program.add(new Hex4digit("1010"));     // load memory at 1 into reg 0
        program.add(new Hex4digit("1021"));     // load memory at 2 into reg 1
        program.add(new Hex4digit("3012"));     // add register 0 and 1 and store output in reg 2
        program.add(new Hex4digit("-0030"));    // value of -30
        program.add(new Hex4digit("9999"));     // value of 9999

        state.memoryStateHistory.add(program);
    }

    @AfterEach
    public void tearDown() {
        state.clearProgramState();
    }

    @Test
    @DisplayName("Test Halt")
    public void testHalt() {
        InstructionSet.halt(state);
        Assertions.assertEquals(-1, state.registers[15].getValue());
    }

    @Test
    @DisplayName("Test Load 1")
    public void testLoad_1() {
        // take the value in memory space 1 and load it into register 0
        InstructionSet.load(state, 1, '0', 0);
        // take the value in memory space 2 and load it into register 1
        InstructionSet.load(state, 2, '1', 0);
        Assertions.assertEquals(5, state.registers[0].getValue());
        Assertions.assertEquals(8, state.registers[1].getValue());
        // the program counter should have been incremented twice
        Assertions.assertEquals(2, state.registers[15].getValue());
    }

    @Test
    @DisplayName("Test Load 2")
    public void testLoad_2() {
        InstructionSet.load(state, 3, '0', 0);
        InstructionSet.load(state, 6, '1', 0);
        InstructionSet.load(state, 7, '2', 0);
        // the numbers need to be converted into hex, remember
        // so 1010 becomes 4112
        Assertions.assertEquals(4112, state.registers[0].getValue());
        Assertions.assertEquals(-48, state.registers[1].getValue());
        Assertions.assertEquals(39321, state.registers[2].getValue());
    }

    @Test
    @DisplayName("Test Store 1")
    public void testStore_1() {
        // nothing has been set in register 5, so the value should default to '0'
        InstructionSet.store(state, 1, '5', 0);
        Assertions.assertEquals(0, state.memoryStateHistory.get(0).get(1).getValue());
    }

    @Test
    @DisplayName("Test Store 2")
    public void testStore_2() {
        state.registers[0].setValue(512);
        state.registers[3].setValue(-4329);
        state.registers[5].setValue(60000);

        InstructionSet.store(state, 3, '0', 0);
        InstructionSet.store(state, 4, '3', 0);
        InstructionSet.store(state, 6, '5', 0);

        Assertions.assertEquals(512, state.memoryStateHistory.get(0).get(3).getValue());
        Assertions.assertEquals(-4329, state.memoryStateHistory.get(0).get(4).getValue());
        Assertions.assertEquals(60000, state.memoryStateHistory.get(0).get(6).getValue());
    }

    @Test
    @DisplayName("Test Add 1")
    public void testAdd_1() {
        // should be '0' as no values have been assigned to registers
        InstructionSet.add(state, '6', '9', '3');
        Assertions.assertEquals(0, state.registers[3].getValue());
    }

    @Test
    @DisplayName("Test Add 2")
    public void testAdd_2() {
        state.registers[4].setValue(300);
        state.registers[5].setValue(996543);
        state.registers[6].setValue(-2);
        InstructionSet.add(state, '4', '5', '6');
        Assertions.assertEquals(996843, state.registers[6].getValue());
    }

    @Test
    @DisplayName("Test Add 3")
    public void testAdd_3() {
        state.registers[7].setValue(-47321);
        state.registers[8].setValue(-32819);
        state.registers[9].setValue(74832);
        InstructionSet.add(state, '7', '8', '9');
        Assertions.assertEquals(-80140, state.registers[9].getValue());
    }

    @Test
    @DisplayName("Test Subtract 1")
    public void testSub_1() {
        InstructionSet.subt(state, '0', '2', '4');
        Assertions.assertEquals(0, state.registers[4].getValue());
    }

    @Test
    @DisplayName("Test Subtract 2")
    public void testSub_2() {
        state.registers[3].setValue(-47321);
        state.registers[6].setValue(-32819);
        state.registers[9].setValue(74832);
        InstructionSet.subt(state, '3', '6', '9');
        Assertions.assertEquals(-14502, state.registers[9].getValue());
    }

    @Test
    @DisplayName("Test Multiply 1")
    public void testMult_1() {
        InstructionSet.mult(state, '5', '1', 'b');
        Assertions.assertEquals(0, state.registers[11].getValue());
    }

    @Test
    @DisplayName("Test Multiply 2")
    public void testMult_2() {
        state.registers[5].setValue(15);
        state.registers[1].setValue(-7);
        state.registers[11].setValue(100);
        InstructionSet.mult(state, '5', '1', 'b');
        Assertions.assertEquals(-105, state.registers[11].getValue());
    }

    @Test
    @DisplayName("Test Integer Divide 1")
    public void testIntDiv_1() {
        // you can't divide by 0
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            InstructionSet.intDivide(state, 'a', 'b', '1');
        });

        String expectedMessage = "/ by zero";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test Integer Divide 2")
    public void testIntDiv_2() {
        state.registers[10].setValue(30);
        state.registers[11].setValue(6);
        state.registers[5].setValue(555);
        InstructionSet.intDivide(state, 'a', 'b', '5');
        Assertions.assertEquals(5, state.registers[5].getValue());
    }

    @Test
    @DisplayName("Test Integer Divide 3")
    public void testIntDiv_3() {
        state.registers[7].setValue(-574);
        state.registers[8].setValue(19);
        state.registers[0].setValue(8);
        InstructionSet.intDivide(state, '7', '8', '0');
        Assertions.assertEquals(-30, state.registers[0].getValue());
    }

    @Test
    @DisplayName("Test Load Indirect")
    public void testLoadIndirect() {
        InstructionSet.loadIndirect(state, 1, '0', 0);
        Assertions.assertEquals(12306, state.registers[0].getValue());
    }

    @Test
    @DisplayName("Test Store Indirect")
    public void testStoreIndirect() {
        state.registers[5].setValue(777);
        InstructionSet.storeIndirect(state, 1, '5', 0);
        Assertions.assertEquals(777, state.memoryStateHistory.get(0).get(5).getValue());
    }

    @Test
    @DisplayName("Test Branch 1")
    public void testBranch_1() {
        InstructionSet.branch(state, 6);
        Assertions.assertEquals(6, state.registers[15].getValue());
    }

    @Test
    @DisplayName("Test Branch 2")
    public void testBranch_2() {
        // register 15 can be set to negative, this is fine, however
        // it will be caught by the ExecutorFacade the next time it
        // is called to complete an instruction
        InstructionSet.branch(state, -55);
        Assertions.assertEquals(-55, state.registers[15].getValue());
    }

    @Test
    @DisplayName("Test Read Int 1")
    public void testReadInt_1() {
        InstructionSet.readInt(state, '7');
        Assertions.assertEquals(0, state.registers[7].getValue());
    }

    @Test
    @DisplayName("Test Read Int 2")
    public void testReadInt_2() {
        state.input.setValue(32);
        InstructionSet.readInt(state, '2');
        Assertions.assertEquals(32, state.registers[2].getValue());
    }

    @Test
    @DisplayName("Test Write Int 1")
    public void testWriteInt_1() {
        InstructionSet.writeInt(state, '3');
        Assertions.assertEquals(0, state.registers[3].getValue());
    }
    @Test
    @DisplayName("Test Write Int 2")
    public void testWriteInt_2() {
        state.registers[14].setValue(-300);
        InstructionSet.writeInt(state, 'e');
        Assertions.assertEquals(-300, state.output.getValue());
    }


    @Test
    @DisplayName("Test Skip 1")
    public void testSkip_1() {
        InstructionSet.skip(state);
        Assertions.assertEquals(1, state.registers[15].getValue());
    }

    @Test
    @DisplayName("Test Skip 2")
    public void testSkip_2() {
        InstructionSet.skip(state);
        InstructionSet.skip(state);
        InstructionSet.skip(state);
        InstructionSet.skip(state);
        InstructionSet.skip(state);
        InstructionSet.skip(state);
        Assertions.assertEquals(6, state.registers[15].getValue());
    }

}
