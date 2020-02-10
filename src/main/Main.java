package main;

import main.model.DFAutomaton;
import main.model.State;

/**
 * Main
 */
public class Main {
    public static void main(String[] args) {
        DFAutomaton tester = new DFAutomaton();
        State startState = new State("Start", 1, 2.3);
        State simpleState = new State("Simple", 2, 3);
        tester.addStartState(startState);
        tester.addState(simpleState);
        System.out.println(tester.getStateByName("Start").isStartState());
        System.out.println(tester.getStates().size() == 1);
        tester.addTransition(startState, 'C', simpleState);
        System.out.println(tester);
        System.out.println( tester.getCurrentState());
        tester.read('C');
        System.out.println( tester.getCurrentState());
    }
    
}