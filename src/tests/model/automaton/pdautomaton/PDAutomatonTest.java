package tests.model.automaton.pdautomaton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import main.model.automaton.pdautomaton.PDAutomaton;
import main.model.automaton.pdautomaton.ReadState;
import main.Languages;
import main.model.automaton.State;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;


public class PDAutomatonTest {

    private PDAutomaton automaton;
    private PDAutomaton filledAutomaton;
    private State simpleState1;
    private State simpleState2;
    private State simpleState3;
    private State otherState;

    @Before
    public void beforeTest() {
        Languages.setLanguageAndRegion("en", "US");

        automaton = new PDAutomaton('Z');
        simpleState1 = new State("Simple1", 1, 2.3);
        simpleState2 = new State("Simple2", 1, 2.3);
        simpleState3 = new State("SImple3",2.4,5.6);
        otherState = new State("Simple1", 1, 2.4);
        filledAutomaton = new PDAutomaton('Z');
        try {
            filledAutomaton.addStartState(simpleState1);
            filledAutomaton.addAcceptState(simpleState2);
            filledAutomaton.addAcceptState(simpleState3);
        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    
    @Test
    public void testIdAdding() {
        try {
            automaton.addState(simpleState1);
            automaton.addState(simpleState2);
            assertEquals(0, simpleState1.getID());
            assertEquals(1, simpleState2.getID());
        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testGetStateByName() {
        try {
            automaton.addStartState(simpleState1);
            State stateTest = automaton.getStateByName("Simple1");
            assertEquals(simpleState1, stateTest);
        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    /**
     * Checking if the added state is start state
     */
    @Test
    public void testStartState() {
        try {
            automaton.addStartState(simpleState1);
            assertTrue(automaton.getStateByName("Simple1").isStartState());
        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    /**
     * Checking if addAcceptState method works
     */
    @Test
    public void testAcceptStates() {
        try {
            automaton.addAcceptState(simpleState2);
            assertTrue(automaton.getStateByName("Simple2").isAcceptState());
        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testStateDelete() {
        try {
            automaton.addState(simpleState1);
            automaton.deleteState(simpleState1);
            assertTrue(automaton.getStates().isEmpty());
        } catch (StateAlreadyExistsException e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    /**
     * Checks StateAlreadyExistsException. Thrown when two state exist in an
     * automaton with the same name.
     */
    @Test
    public void testDoubleExistenceException() {

        assertThrows(StateAlreadyExistsException.class, () -> {
            automaton.addState(simpleState1);
            automaton.addState(otherState);

        });
    }

    /**
     * Checks StartStateAlreadyExistsException. Thrown when there are two start state in one automat.
     */
    @Test
    public void testDoubleStartStateException() {

        assertThrows(StartStateAlreadyExistsException.class, ()-> {
            automaton.addStartState(simpleState1);
            automaton.addStartState(otherState);
        });
    }

    /**
     * Checks StateNotFoundException. Thrown when getStateByName method can't find state with the name given as parameter.
     */
    @Test
    public void testStateNotFoundException() {
       
        assertThrows(StateNotFoundException.class, ()-> {
            automaton.addState(simpleState1);
            automaton.getStateByName("Simple4");
        });
    }

    @Test
    public void testReset(){
        try{
            filledAutomaton.addTransition(simpleState1, 'a', 'Z', simpleState2, "AA");
            filledAutomaton.read('a');
            assertEquals(simpleState2, filledAutomaton.getCurrentState());
            filledAutomaton.reset();
            assertEquals(simpleState1, filledAutomaton.getCurrentState());
        }catch(Exception e){
            fail("Unexpected exception : " + e.toString());
        }

    }

    @Test
    public void testCurrentAndPreviousState() {
        try {
            filledAutomaton.addTransition(simpleState1, 'a', 'Z', simpleState2, "AA");
            filledAutomaton.read('a');
           assertEquals(simpleState2, filledAutomaton.getCurrentState()); 
           assertEquals(simpleState1, filledAutomaton.getPreviousState());

        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }

    @Test
    public void testLastReadLetter() {
        try {
            filledAutomaton.addTransition(simpleState1, 'a', 'Z', simpleState2, "AA");
            filledAutomaton.read('a');
           assertEquals('a', filledAutomaton.getLastReadLetter()); 
           

        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }
    

    @Test
    public void testRead() {
        try {
            
            filledAutomaton.addTransition(simpleState1, 'a', 'Z', simpleState2, "AA");
            filledAutomaton.read('a');
            assertEquals('A',filledAutomaton.getStack().peek().charValue()); // checking the stack
            assertTrue(filledAutomaton.getCurrentState().isAcceptState()); // checking accept
            assertEquals("Simple2",filledAutomaton.getCurrentState().getName()); // checking if it is the B state

            filledAutomaton.reset();

            filledAutomaton.read('b');
            assertEquals(null, filledAutomaton.getCurrentState());

            filledAutomaton.reset();
            filledAutomaton.addTransition(simpleState1, 'b', 'B', simpleState2, "AA");
            filledAutomaton.read('b');
            assertEquals(null, filledAutomaton.getCurrentState());

        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
        
    }

    @Test
    public void testStepByStepRead() {
        try {
            
            filledAutomaton.addTransition(simpleState1, 'a', 'Z', simpleState2, "AA");
            //  POP 
            filledAutomaton.stepByStepRead('a');
            assertTrue(filledAutomaton.getStack().isEmpty()); // checking the stack
            assertEquals(ReadState.READ, filledAutomaton.getReadState());
            //  READ
            filledAutomaton.stepByStepRead('a');
            assertTrue(filledAutomaton.getCurrentState().isAcceptState()); // checking accept
            assertEquals("Simple2",filledAutomaton.getCurrentState().getName()); // checking if it is the B state
            assertEquals(ReadState.PUSH, filledAutomaton.getReadState());
            //  PUSH   
            filledAutomaton.stepByStepRead('a');
            assertEquals(1, filledAutomaton.getStack().size());
            filledAutomaton.stepByStepRead('a');
            assertEquals(2, filledAutomaton.getStack().size());
            assertEquals(ReadState.POP, filledAutomaton.getReadState());

        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
        
    }



    

    

  
}