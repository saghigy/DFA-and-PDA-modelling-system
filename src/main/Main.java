package main;

import java.util.Scanner;

import javax.swing.JFrame;

import main.controller.AutomatonController;
import main.model.automaton.dfautomaton.DFAutomaton;
import main.model.automaton.pdautomaton.PDAutomaton;
import main.model.automaton.State;
import main.model.exceptions.KeyFromStateAlreadyExistsException;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;
import main.view.MainFrame;

/**
 * Main
 */
public class Main {
    public static void main(String[] args) {
        /*
         * DFAutomaton tester = new DFAutomaton(); State startState = new State("Start",
         * 1, 2.3); State simpleState = new State("Simple", 2, 3); try {
         * tester.addStartState(startState); tester.addState(simpleState);
         * System.out.println(tester.getStateByName("Start").isStartState());
         * System.out.println(tester.getStates().size() == 1);
         * tester.addTransition(startState, 'C', simpleState);
         * System.out.println(tester); System.out.println( tester.getCurrentState());
         * tester.read('C'); System.out.println( tester.getCurrentState()); } catch
         * (StateAlreadyExistsException e) {
         * 
         * e.printStackTrace(); } catch (StartStateAlreadyExistsException e) {
         * 
         * e.printStackTrace(); }
         */
        /*
         * State state1 = new State("Test", 1, 2); State state2 = new State("Test", 1,
         * 2); DFAutomaton automaton = new DFAutomaton(); try {
         * automaton.addState(state1); automaton.addState(state2); } catch
         * (StateAlreadyExistsException e) { System.out.println( e.getMessage()); }
         */

        /*
         * PDAutomaton tester = new PDAutomaton('Z'); State state1 = new State("A",1,2);
         * State state2 = new State("B",5,3.2); State state3 = new State("C",6,1.1); try
         * { tester.addStartState(state1); tester.addAcceptState(state2);
         * tester.addState(state3); tester.addTransition(state1, 'a', 'A', state2, "");
         * tester.addTransition(state1, 'a', 'Z', state3, "A");
         * tester.addTransition(state3, 'b', 'A', state1, "A"); tester.read('a');
         * tester.read('b'); System.out.println(tester); tester.read('a');
         * 
         * } catch (Exception e) { System.out.println(e.getMessage()); }
         */
        /*
         * PDAutomaton automaton = new PDAutomaton('Z'); try { // Making the automaton
         * automaton.addStartState("0", 1, 2); automaton.addState("1",2,3);
         * automaton.addState("2",4,7); automaton.addAcceptState("3",4,2); State state0
         * = automaton.getStateByName("0"); State state1 =
         * automaton.getStateByName("1"); State state2 = automaton.getStateByName("2");
         * State state3 = automaton.getStateByName("3"); automaton.addTransition(state0,
         * 'a', 'Z', state1, "Za"); automaton.addTransition(state1, 'a', 'a', state1,
         * "aa"); automaton.addTransition(state1, 'b', 'a', state2, "");
         * automaton.addTransition(state2, 'b', 'a', state2, "");
         * automaton.addTransition(state2, '#', 'Z', state3, "");
         * 
         * 
         * // Test word: aaaabbbb (good) String word = "bbbaaa#"; boolean errorInReading
         * = false; int i = 0; while ( i < word.length() && !errorInReading) {
         * automaton.read(word.charAt(i)); if (automaton.getCurrentState() == null) {
         * errorInReading = true;
         * 
         * } i++; }
         * 
         * } catch (Exception e) { System.out.println(e.getMessage());
         * 
         * }
         */
        /*
         * PDAutomaton automaton = new PDAutomaton('Z'); try { // Making the automaton
         * automaton.addStartState("0", 1, 2); automaton.addState("1",2,3);
         * automaton.addState("2",4,7); automaton.addAcceptState("3",4,2); State state0
         * = automaton.getStateByName("0"); State state1 =
         * automaton.getStateByName("1"); State state2 = automaton.getStateByName("2");
         * State state3 = automaton.getStateByName("3"); automaton.addTransition(state0,
         * 'a', 'Z', state1, "Za"); automaton.addTransition(state1, 'a', 'a', state1,
         * "aa"); automaton.addTransition(state1, 'b', 'a', state2, "");
         * automaton.addTransition(state2, 'b', 'a', state2, "");
         * automaton.addTransition(state2, '#', 'Z', state3, "");
         * 
         * System.out.println(automaton.generateFileFormat());
         * 
         * 
         * 
         * 
         * } catch (Exception e) { System.out.println(e.getMessage());
         * 
         * }
         */

        /*
         * DFAutomaton tester = new DFAutomaton(); State startState = new State("Start",
         * 1, 2.3); State simpleState = new State("Simple", 2, 3); try {
         * tester.addStartState(startState); tester.addState(simpleState);
         * System.out.println(tester); tester.addTransition(startState, 'C',
         * simpleState); tester.setAcceptState(startState.getName());
         * 
         * tester.read('C'); System.out.println(tester.getCurrentState());
         * 
         * 
         * } catch (Exception e) {
         * 
         * e.printStackTrace(); }
         */

         new MainFrame();
      
    



    }
    
}