package tests.model;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import main.model.DFAutomaton;
import main.model.State;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;

public class DFAutomatonTest {
    
    /**
     * Checking if the added state is start state
     */
    @Test
    public void checkStartState() {
        try {
            DFAutomaton tester = new DFAutomaton();
            State testState = new State("Test", 1, 2.3);
            tester.addStartState(testState);
            assertTrue(tester.getStateByName("Test").isStartState());
        } catch (Exception e) {

        }
    }
    
    /**
     * Checking if addAcceptState method works
     */
    @Test
    public void checkAcceptStates() {
        try {
            DFAutomaton tester = new DFAutomaton();
            State startState = new State("Start", 1, 1);
            State testAcceptState = new State("Accept", 2, 2);
            tester.addStartState(startState);
            tester.addAcceptState(testAcceptState);
            assertTrue(tester.getStateByName("Accept").isAcceptState());
        } catch (Exception e) {
           
        }
    }
        
    /**
     * Checks StateAlreadyExistsException. Thrown when two state exist in an automaton with the same name.
     */
    @Test
    public void checkDoubleExistenceException()  {
        State state1 = new State("Test", 1, 2);
        State state2 = new State("Test", 1, 2);
        DFAutomaton automaton = new DFAutomaton();
        assertThrows(StateAlreadyExistsException.class, ()-> {
            automaton.addState(state1);
            automaton.addState(state2);
        });
    }

    /**
     * Checks StartStateAlreadyExistsException. Thrown when there are two start state in one automat.
     */
    @Test
    public void checkDoubleStartStateException() {
        State state1 = new State("Test1", 1, 2);
        State state2 = new State("Test2", 1, 2);
        DFAutomaton automaton = new DFAutomaton();
        assertThrows(StartStateAlreadyExistsException.class, ()-> {
            automaton.addStartState(state1);
            automaton.addStartState(state2);
        });
    }

    /**
     * Checks StateNotFoundException. Thrown when getStateByName method can't find state with the name given as parameter.
     */
    @Test
    public void checkStateNotoundException() {
        State state1 = new State("Test1", 1, 2);
        State state2 = new State("Test2", 1, 2);
        DFAutomaton automaton = new DFAutomaton();
        assertThrows(StateNotFoundException.class, ()-> {
            automaton.addState(state1);
            automaton.addState(state2);
            automaton.getStateByName("Test3");
        });
    }

    //  DFA-tests

     /**
     * Checking if the transition goes to the right state by reading
     */
    @Test
    public void checkTransition() {
        try {
            DFAutomaton tester = new DFAutomaton();
            State startState = new State("Start", 1, 2.3);
            State simpleState = new State("Simple", 2, 3);
            tester.addStartState(startState);
            tester.addState(simpleState);
            tester.addTransition(startState, 'C', simpleState);
            tester.read('C');
            assertTrue(tester.getCurrentState() == simpleState);
        } catch (Exception e) {

        }
    }

    


}