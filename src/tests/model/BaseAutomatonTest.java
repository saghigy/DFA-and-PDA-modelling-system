package tests.model;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import main.model.BaseAutomaton;
import main.model.State;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;

/**
 * BaseAutomatonTest
 */
public class BaseAutomatonTest {

    /**
     * Checking if the added state is start state
     
    @Test
    public void checkStartState() {
        try {
            BaseAutomaton tester = new BaseAutomaton();
            State testState = new State("Test", 1, 2.3);
            tester.addStartState(testState);
            assertTrue(tester.getStateByName("Test").isStartState());
        } catch (Exception e) {

        }
    }*/

     /**
     * Checking if addAcceptState method works
     
    @Test
    public void checkAcceptStates() {
        try {
            BaseAutomaton tester = new BaseAutomaton();
            State startState = new State("Start", 1, 1);
            State testAcceptState = new State("Accept", 2, 2);
            tester.addStartState(startState);
            tester.addAcceptState(testAcceptState);
            assertTrue(tester.getStateByName("Accept").isAcceptState());
        } catch (Exception e) {
           
        }
    }*/

    /**
     * Checks StateAlreadyExistsException. Thrown when two state exist in an automaton with the same name.
    
    @Test
    public void checkDoubleExistenceException()  {
        State state1 = new State("Test", 1, 2);
        State state2 = new State("Test", 1, 2);
        BaseAutomaton automaton = new BaseAutomaton();
        assertThrows(StateAlreadyExistsException.class, ()-> {
            automaton.addState(state1);
            automaton.addState(state2);
        });
    }
    */
    /**
     * Checks StartStateAlreadyExistsException. Thrown when there are two start state in one automat.
     
    @Test
    public void checkDoubleStartStateException() {
        State state1 = new State("Test1", 1, 2);
        State state2 = new State("Test2", 1, 2);
        BaseAutomaton automaton = new BaseAutomaton();
        assertThrows(StartStateAlreadyExistsException.class, ()-> {
            automaton.addStartState(state1);
            automaton.addStartState(state2);
        });
    }
    */
    /**
     * Checks StateNotFoundException. Thrown when getStateByName method can't find state with the name given as parameter.
     
    @Test
    public void checkStateNotoundException() {
        State state1 = new State("Test1", 1, 2);
        State state2 = new State("Test2", 1, 2);
        BaseAutomaton automaton = new BaseAutomaton();
        assertThrows(StateNotFoundException.class, ()-> {
            automaton.addState(state1);
            automaton.addState(state2);
            automaton.getStateByName("Test3");
        });
    }
    */
}