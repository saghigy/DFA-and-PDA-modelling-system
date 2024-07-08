package tests.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import main.controller.AutomatonController;
import main.controller.exceptions.IncorrectTypeException;
import main.model.automaton.State;
import main.model.automaton.dfautomaton.DFATransitionKey;
import main.model.automaton.dfautomaton.DFAutomaton;
import main.model.automaton.pdautomaton.PDAutomaton;
import main.model.exceptions.KeyFromStateAlreadyExistsException;
import main.model.exceptions.MissingStartStateException;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;

public class AutomatonControllerTest {

    private AutomatonController controller;
    private AutomatonController dFAController;
    private AutomatonController pDAController;
    private DFAutomaton testDFAutomaton;
    private PDAutomaton testPDAutomaton;
    private State state1;

    @Before
    public void beforeTest() {
        controller = new AutomatonController();
        dFAController = new AutomatonController();
        dFAController.addNewDFAutomaton();
        pDAController = new AutomatonController();
        pDAController.addNewPDAutomaton('Z');
        testDFAutomaton = new DFAutomaton();
        testPDAutomaton = new PDAutomaton('Z');
        state1 = new State("Test", 2, 2);
    }

    @Test
    public void testMakingDFAutomaton() {
        controller.addNewDFAutomaton();
        assertTrue(controller.getAutomaton() instanceof DFAutomaton);
    }

    @Test
    public void testMakingPDAutomaton() {
        controller.addNewPDAutomaton('Z');
        assertTrue(controller.getAutomaton() instanceof PDAutomaton);
        PDAutomaton pdAutomaton = (PDAutomaton) controller.getAutomaton();
        assertEquals('Z', pdAutomaton.getStack().peek().charValue());
    }

    @Test
    public void testOpenExistingDFA() {
        controller.addNewDFAutomaton(testDFAutomaton);
        assertEquals(testDFAutomaton, controller.getAutomaton());
    }

    @Test
    public void testOpenExistingPDA() {
        controller.addNewPDAutomaton(testPDAutomaton);
        assertEquals(testPDAutomaton, controller.getAutomaton());
    }

    @Test
    public void testOpenDFAFromFileFormat() {
        try {
            File testfile = new File("resources/testfiles/abcstar.amproj");
            controller.addNewDFAutomaton(testfile.getAbsolutePath());
            assertEquals(3,controller.getAutomaton().getStates().size());
        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testOpenPDAFromFileFormat() {
        try {
            File testfile = new File("resources/testfiles/0^n1^n.amproj");
            controller.addNewPDAutomaton(testfile.getAbsolutePath());
            assertEquals(4,controller.getAutomaton().getStates().size());
        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testUndoAndRedo() {
        try {
            dFAController.addAcceptState("a", 2, 5);
            assertEquals(1,dFAController.getAutomaton().getStates().size());
            dFAController.undo();
            assertEquals(0,dFAController.getAutomaton().getStates().size());
            dFAController.redo();
            assertEquals(1,dFAController.getAutomaton().getStates().size());
        } catch (StateAlreadyExistsException e) {
            fail("Unexpected exception : " + e.toString());
        }

    }

    @Test
    public void testMakingDFAutomatonByRegex() {
        String regex = "(a|b)*(c|d)*";
        try {
            controller.addNewDFAutomatonByRegex(regex);
            assertEquals(2, controller.getAutomaton().getStates().size());
            controller.addWordToRead("ababababbabcdcdcdccd");
            assertTrue(controller.isInputWordCorrect());
            controller.addWordToRead("aaabccccccddda");
            assertFalse(controller.isInputWordCorrect());
        } catch (StateAlreadyExistsException | StartStateAlreadyExistsException | StateNotFoundException
                | KeyFromStateAlreadyExistsException | MissingStartStateException e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testAddingState() {
        try {
            //  DFA
            dFAController.addStartState("A", 1, 2);
            assertNotNull(dFAController.getAutomaton().getStateByName("A")); 
            assertTrue(dFAController.getAutomaton().getStateByName("A").isStartState());
            dFAController.addState("B", 1, 2);
            assertNotNull(dFAController.getAutomaton().getStateByName("B"));
            dFAController.addAcceptState("C", 1, 2);
            assertNotNull(dFAController.getAutomaton().getStateByName("C"));
            assertTrue(dFAController.getAutomaton().getStateByName("C").isAcceptState());
            //  PDA
            pDAController.addNewPDAutomaton('Z');
            pDAController.addStartState("A", 1, 2);
            assertNotNull(pDAController.getAutomaton().getStateByName("A")); 
            assertTrue(pDAController.getAutomaton().getStateByName("A").isStartState());
            pDAController.addState("B", 1, 2);
            assertNotNull(pDAController.getAutomaton().getStateByName("B"));
            pDAController.addAcceptState("C", 1, 2);
            assertNotNull(pDAController.getAutomaton().getStateByName("C"));
            assertTrue(pDAController.getAutomaton().getStateByName("C").isAcceptState());
        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        } 
    }

    @Test
    public void testStateMaking() {
        try{
            dFAController.setRadiusOfStates(10);
            dFAController.addState("Simple",0,0);
            assertTrue(dFAController.canMakeState(12, 20));
            assertFalse(dFAController.canMakeState(5, 2));

            pDAController.setRadiusOfStates(10);
            pDAController.addState("Simple",0,0);
            assertTrue(pDAController.canMakeState(12, 20));
            assertFalse(pDAController.canMakeState(5, 2));
        }catch(Exception e){
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testPositionChange() {
        try{
            dFAController.addState("Simple",0,0);
            State simple = dFAController.getAutomaton().getStateByName("Simple");
            dFAController.changePosition(simple, 5, 9);
            assertEquals(5, dFAController.getAutomaton().getStateByName("Simple").getX(),0);
            assertEquals(9, dFAController.getAutomaton().getStateByName("Simple").getY(),0);

            pDAController.addState("Simple",0,0);
            simple = dFAController.getAutomaton().getStateByName("Simple");
            pDAController.changePosition(simple, 5, 9);
            assertEquals(5, pDAController.getAutomaton().getStateByName("Simple").getX(),0);
            assertEquals(9, pDAController.getAutomaton().getStateByName("Simple").getY(),0);
        }catch(Exception e){
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testDeletingState() {
        try {
            //  DFA
            dFAController.addNewDFAutomaton();
            dFAController.addState("A", 1, 2);
            dFAController.addState("B", 20, 44);
            dFAController.makeDFATransition(
                dFAController.getAutomaton().getStateByName("A"),
                'a',
                dFAController.getAutomaton().getStateByName("B")
            );
            dFAController.deleteState(dFAController.getAutomaton().getStateByName("A"));
            assertEquals(1,dFAController.getAutomaton().getStates().size());
            DFAutomaton dfa = (DFAutomaton) dFAController.getAutomaton();
            assertTrue(dfa.getTransitionFunction().isEmpty());
            //  PDA
            pDAController.addNewPDAutomaton('Z');
            pDAController.addState("A", 1, 2);
            pDAController.addState("B", 20, 44);
            pDAController.makePDATransition(
                pDAController.getAutomaton().getStateByName("A"),
                'a',
                'Z',
                pDAController.getAutomaton().getStateByName("B"),
                "AA"
            );
            pDAController.deleteState(pDAController.getAutomaton().getStateByName("A"));
            assertEquals(1,pDAController.getAutomaton().getStates().size());
            PDAutomaton pda = (PDAutomaton) pDAController.getAutomaton();
            assertTrue(pda.getTransitionFunction().isEmpty());

        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testWordAdding() {
        dFAController.addWordToRead("aaaaa");
        assertEquals("aaaaa", dFAController.getWordToRead());

        pDAController.addWordToRead("aaaaa");
        assertEquals("aaaaa#", pDAController.getWordToRead());
    }

    @Test
    public void testRead() {
        try {
            dFAController.addStartState("A", 1, 22);
            dFAController.addAcceptState("B", 33, 43);
            State from = dFAController.getAutomaton().getStateByName("A");
            State to = dFAController.getAutomaton().getStateByName("B");
            dFAController.makeDFATransition(from, 'c', to);
            dFAController.addWordToRead("c");
            dFAController.nextStepInReading();
            assertEquals(to, dFAController.getCurrentState()); 
            
            pDAController.addStartState("A", 1, 22);
            pDAController.addAcceptState("B", 33, 43);
            from = pDAController.getAutomaton().getStateByName("A");
            to = pDAController.getAutomaton().getStateByName("B");
            pDAController.makePDATransition(from, 'c','Z', to,"AA");
            pDAController.addWordToRead("c");
            pDAController.nextStepInReading();
            assertEquals(to, pDAController.getCurrentState()); 

        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testReadBadValue(){
        try {
            dFAController.addStartState("A", 1, 22);
            dFAController.addAcceptState("B", 33, 43);
            State from = dFAController.getAutomaton().getStateByName("A");
            State to = dFAController.getAutomaton().getStateByName("B");
            dFAController.makeDFATransition(from, 'c', to);
            dFAController.addWordToRead("d");
            dFAController.nextStepInReading();  //ERROR
           assertNull(dFAController.getCurrentState());
             
            pDAController.addStartState("A", 1, 22);
            pDAController.addAcceptState("B", 33, 43);
            from = pDAController.getAutomaton().getStateByName("A");
            to = pDAController.getAutomaton().getStateByName("B");
            pDAController.makePDATransition(from, 'c','Z', to,"AA");
            pDAController.addWordToRead("d");
            pDAController.nextStepInReading();
            assertNull(pDAController.getCurrentState());

        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testReset() {
        try {
            dFAController.addStartState("A", 1, 22);
            dFAController.addAcceptState("B", 33, 43);
            State from = dFAController.getAutomaton().getStateByName("A");
            State to = dFAController.getAutomaton().getStateByName("B");
            dFAController.makeDFATransition(from, 'c', to);
            dFAController.addWordToRead("c");
            dFAController.nextStepInReading();
            assertEquals(to, dFAController.getCurrentState()); 
            dFAController.reset();
            assertEquals(from, dFAController.getCurrentState()); 
            
            pDAController.addStartState("A", 1, 22);
            pDAController.addAcceptState("B", 33, 43);
            from = pDAController.getAutomaton().getStateByName("A");
            to = pDAController.getAutomaton().getStateByName("B");
            pDAController.makePDATransition(from, 'c','Z', to,"AA");
            pDAController.addWordToRead("c");
            pDAController.nextStepInReading();
            assertEquals(to, pDAController.getCurrentState()); 
            pDAController.reset();
            assertEquals(from, pDAController.getCurrentState());  

        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }

    }



    




    








}