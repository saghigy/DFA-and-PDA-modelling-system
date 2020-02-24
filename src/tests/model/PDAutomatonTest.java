package tests.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import main.model.PDAutomaton;
import main.model.State;


/**
 * PDAutomaton
 */
public class PDAutomatonTest {

    @Test   
    public void checkTransition() {
        PDAutomaton tester = new PDAutomaton('Z');
        State state1 = new State("A",1,2);
        State state2 = new State("B",5,3.2);
        State state3 = new State("C",6,1.1);
        try {
            tester.addStartState(state1);
            tester.addAcceptState(state2);
            tester.addState(state3);
            tester.addTransition(state1, 'a', 'A', state2, "");
            tester.addTransition(state1, 'a', 'Z', state3, "A");
            tester.addTransition(state3, 'b', 'A', state1, "A");
            tester.read('a');
            tester.read('b');
            assertTrue(tester.getStack().peek().equals('A'));               //  checking the stack
            tester.read('a');
            assertTrue(tester.getCurrentState().isAcceptState());           //  checking accept
            assertTrue(tester.getCurrentState().getName().equals("B"));     //  checking if it is the B state
        } catch (Exception e) {
            
        }
    }
}