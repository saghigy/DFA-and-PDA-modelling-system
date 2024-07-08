package tests.model.automaton.dfautomaton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import main.model.automaton.dfautomaton.DFAutomaton;
import main.Languages;
import main.model.automaton.State;
import main.model.exceptions.KeyFromStateAlreadyExistsException;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;

public class DFAutomatonTest {

    private DFAutomaton automaton;
    private DFAutomaton filledAutomaton;
    private State simpleState1;
    private State simpleState2;
    private State otherState;

    @Before
    public void beforeTest() {
        Languages.setLanguageAndRegion("en", "US");

        automaton = new DFAutomaton();
        simpleState1 = new State("Simple1", 1, 2.3);
        simpleState2 = new State("Simple2", 1, 2.3);
        otherState = new State("Simple1", 1, 2.4);
        filledAutomaton = new DFAutomaton();
        try {
            filledAutomaton.addStartState(simpleState1);
            filledAutomaton.addAcceptState(simpleState2);
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
            automaton.getStateByName("Simple3");
        });
    }

    //  DFA-tests

    @Test
    public void testReset(){
        try{
            filledAutomaton.addTransition(simpleState1, 'a', simpleState2);
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
            filledAutomaton.addTransition(simpleState1, 'a', simpleState2);
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
            filledAutomaton.addTransition(simpleState1, 'a', simpleState2);
            filledAutomaton.read('a');
           assertEquals('a', filledAutomaton.getLastReadLetter()); 
           

        } catch (Exception e) {
            fail("Unexpected exception : " + e.toString());
        }
    }


     /**
     * Checking if the transition goes to the right state by reading
     */
    @Test
    public void testRead() {
        try {
            automaton.addStartState(simpleState1);
            automaton.addState(simpleState2);
            automaton.addTransition(simpleState1, 'C', simpleState2);
            automaton.read('C');
            assertEquals(simpleState2,automaton.getCurrentState());
        } catch (Exception e) {

        }
    }



    


}