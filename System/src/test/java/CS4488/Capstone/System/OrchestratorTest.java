package CS4488.Capstone.System;

import CS4488.Capstone.Library.Tools.Hex4digit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrchestratorTest {
    Orchestrator orchestrator = Orchestrator.getInstance();

    private static final String resources = "./ResourceDirectories";
    String Program1 = resources + "/Example Code/Program 1, Hello Branch and Math.txt";
    String ProgramXYZ = resources + "/Example Code/Program XYZ, TestingCoverage.txt";
    String fakeFile = resources + "/Example Code/fhw4fhq2fhq4thq.txt";
    String badFile = resources + "/Example Code/Program X, Bad Program.txt";

    String load = resources + "/Example Code/load Test.txt";
    String store = resources + "/Example Code/store test.txt";
    String add = resources + "/Example Code/add test.txt";
    String subtract = resources + "/Example Code/sub test.txt";
    String multiple = resources + "/Example Code/mul test.txt";
    String divide = resources + "/Example Code/div test.txt";
    String loadIndirect = resources + "/Example Code/load indirect.txt";
    String storeIndirect = resources + "/Example Code/store indirect.txt";
    String branch = resources + "/Example Code/branch test.txt";
    String branchZero = resources + "/Example Code/branch zero test.txt";
    String branchNegative = resources + "/Example Code/branch neg test.txt";
    String branchPositive = resources + "/Example Code/branch pos test.txt";
    String read = resources + "/Example Code/read test.txt";
    String write = resources + "/Example Code/write test.txt";
    String skip = resources + "/Example Code/skip test.txt";

    void runTestProgramWithTranslation(String file, int input, int stepLimit){
            System.out.println("Running program from \n" + file);
            int i = 0;
            orchestrator.translateAndLoad(file);
            System.out.println("\n T R A N S L A T O R  -  D O N E \n");
            for (Hex4digit h: orchestrator.getProgramState().memoryStateHistory.get(0)){
                System.out.println(h.getString());
            }

            orchestrator.getProgramState().input = new Hex4digit(input);
            System.out.println("Initial State, State " + i + ": \n");
            System.out.println(orchestrator.getProgramState().printableProgramState());
            while ((orchestrator.next()) && (stepLimit > -1)){
                stepLimit--;
                i++;
                System.out.println("\nState " + i + ": \n");
                System.out.println(orchestrator.getProgramState().printableProgramState());
            }
            orchestrator.clearProgram();
    }

    @Test
    @DisplayName("Individual tests")
    void executionAndPrint() {

        System.out.println("\nXYZ Test\n");
        runTestProgramWithTranslation(ProgramXYZ, 4097, 20);

        // I have commented out the Programs that I've verified - Traae

        //System.out.println("\nLoad Test\n");
        //runProgram(load, 25, 10);

        //System.out.println("\nStore Test\n");
        //runProgram(store, 25, 10);


        //System.out.println("\nAdd Test\n");
        //runProgram(add, 25, 10);

        //System.out.println("\nSubtract Test\n");
        //runProgram(subtract, 25, 10);

        //System.out.println("\nMultiply Test\n");
        //runProgram(multiple, 25, 10);

        //System.out.println("\nDivide Test\n");
        //runProgram(divide, 25, 10);

        //System.out.println("\nLoad Indirect Test\n");
        //runProgram(loadIndirect, 25, 10);


        //System.out.println("\nStore Indirect Test\n");
        //runProgram(storeIndirect, 25, 10);

        //System.out.println("\nBranch Test\n");
        //runProgram(branch, 25, 10);


       //System.out.println("\nBranch Zero Test\n");
        //runProgram(branchZero, 25, 10);


        //System.out.println("\nBranch Negative Test\n");
        //runProgram(branchNegative, 25, 10);


        //System.out.println("\nBranch Positive Test\n");
        //runProgram(branchPositive, 25, 10);


        //System.out.println("\nRead Test\n");
        //runProgram(read, 25, 10);


        //System.out.println("\nWrite Test\n");
        //runProgram(write, 25, 10);

        //System.out.println("\nskip Test\n");
        //runProgram(skip, 25, 10);


    }

    private void runProgram(String file, int input, int stepLimit){
        int i = 0;
        orchestrator.translateAndLoad(file);
        //System.out.println("\n T R A N S L A T O R  -  D O N E \n");
        orchestrator.getProgramState().input = new Hex4digit(input);
        System.out.println("Initial State, State " + i + ": \n");
        System.out.println(orchestrator.getProgramState().printableProgramState());
        while ((orchestrator.next()) && (stepLimit > -1)){

            stepLimit--;
            i++;
            System.out.println("\nState " + i + ": \n");
            System.out.println(orchestrator.getProgramState().printableProgramState());
        }
        orchestrator.clearProgram();
    }

    @Test
    void next() {

    }

    @Test
    void getProgramState() {
    }

    @Test
    void sendInput() {
    }

    @Test
    void getOutput() {
    }

    @Test
    void translateAndLoad() {

        orchestrator.translateAndLoad(Program1);
        System.out.println("Translate and Load output:");
        System.out.println(orchestrator.getProgramState().printableProgramState());
    }

    @Test
    void convertToHexChars() {
    }

    @Test
    void convertToShort() {
    }
}