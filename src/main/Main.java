package main;

import main.model.DFAutomaton;
import main.model.State;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;

/**
 * Main
 */
public class Main {
    public static void main(String[] args) {
        /*
        DFAutomaton tester = new DFAutomaton();
        State startState = new State("Start", 1, 2.3);
        State simpleState = new State("Simple", 2, 3);
        try {
            tester.addStartState(startState);
            tester.addState(simpleState);
            System.out.println(tester.getStateByName("Start").isStartState());
            System.out.println(tester.getStates().size() == 1);
            tester.addTransition(startState, 'C', simpleState);
            System.out.println(tester);
            System.out.println( tester.getCurrentState());
            tester.read('C');
            System.out.println( tester.getCurrentState());
        } catch (StateAlreadyExistsException e) {
            
            e.printStackTrace();
        } catch (StartStateAlreadyExistsException e) {
            
            e.printStackTrace();
        }
        */
        State state1 = new State("Test", 1, 2);
        State state2 = new State("Test", 1, 2);
        DFAutomaton automaton = new DFAutomaton();
        try {
            automaton.addState(state1);
            automaton.addState(state2);
        } catch (StateAlreadyExistsException e) {
            System.out.println( e.getMessage());
        }
    }
    
}