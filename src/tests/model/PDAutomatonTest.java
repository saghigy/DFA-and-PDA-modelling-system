package tests.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import main.model.PDAutomaton;
import main.model.State;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;

/**
 * PDAutomaton
 */
public class PDAutomatonTest {

    @Test
    public void checkTransition() {
        PDAutomaton tester = new PDAutomaton('Z');
        State state1 = new State("A", 1, 2);
        State state2 = new State("B", 5, 3.2);
        State state3 = new State("C", 6, 1.1);
        try {
            tester.addStartState(state1);
            tester.addAcceptState(state2);
            tester.addState(state3);
            tester.addTransition(state1, 'a', 'A', state2, "");
            tester.addTransition(state1, 'a', 'Z', state3, "A");
            tester.addTransition(state3, 'b', 'A', state1, "A");
            tester.read('a');
            tester.read('b');
            assertTrue(tester.getStack().peek().equals('A')); // checking the stack
            tester.read('a');
            assertTrue(tester.getCurrentState().isAcceptState()); // checking accept
            assertTrue(tester.getCurrentState().getName().equals("B")); // checking if it is the B state
        } catch (Exception e) {

        }
    }

    /**
     * Tested language: a^nb^n (n in N)
     */
    @Test
    public void checkLanguage1() {
        PDAutomaton automaton = new PDAutomaton('Z');
        try {
            //  Making the automaton
            automaton.addStartState("0", 1, 2);
            automaton.addState("1",2,3);
            automaton.addState("2",4,7);
            automaton.addAcceptState("3",4,2);
            State state0 = automaton.getStateByName("0");
            State state1 = automaton.getStateByName("1");
            State state2 = automaton.getStateByName("2");
            State state3 = automaton.getStateByName("3");
            automaton.addTransition(state0, 'a', 'Z', state1, "Za");
            automaton.addTransition(state1, 'a', 'a', state1, "aa");
            automaton.addTransition(state1, 'b', 'a', state2, "");
            automaton.addTransition(state2, 'b', 'a', state2, "");
            automaton.addTransition(state2, '#', 'Z', state3, "");

            //  Test word: aaaabbbb (good)
            String word = "aaaabbbb#";
            boolean errorInReading = false;
            int i = 0;
            while ( i < word.length() && !errorInReading) {
                automaton.read(word.charAt(i));
                if (automaton.getCurrentState() == null) {
                    errorInReading = true;
                }
                i++;
            }
            assertTrue(automaton.getCurrentState().isAcceptState()); // accept state

            // Test word: aaaabbbb (bad)
            automaton.reset();
            word = "aaaabbbb#";
            errorInReading = true;
            i = 0;
            while ( i < word.length() && !errorInReading) {
                automaton.read(word.charAt(i));
                if (automaton.getCurrentState() == null) {
                    errorInReading = true;
                }
                i++;
            }
            assertFalse(!errorInReading);

            // Test word: bbbaaa (bad)
            automaton.reset();
            word = "aaaabbbb#";
            errorInReading = true;
            i = 0;
            while ( i < word.length() && !errorInReading) {
                automaton.read(word.charAt(i));
                if (automaton.getCurrentState() == null) {
                    errorInReading = true;
                }
                i++;
            }
            assertFalse(!errorInReading);
            assertFalse(automaton.getCurrentState().isAcceptState());
            
        } catch (Exception e) {
            assertTrue(false);
        
        }   
    }

  
}