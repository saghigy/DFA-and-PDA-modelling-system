package main.model;

import java.util.ArrayList;

/**
 * Base of Automats with basic methods
 * @author Gyorgy Saghi
 */
public class BaseAutomaton implements Automaton {

    protected ArrayList<State> states;
    protected State startState;
    protected State currentState;
    

    public BaseAutomaton() {
        this.states = new ArrayList<>();
    }

    @Override
    public void read(char character) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addState(State state) {
        for (State s : states) {
            if(s.equals(state)) {
                //Exception: already exists
            }
        }
        states.add(state);
    }

    @Override
    public void addState(String name, double x, double y) {
        State state = new State(name,x,y);
        addState(state);
    }

    @Override
    public void addStartState(State state) {
        for (State s : states) {
            if (s.isStartState()) {
                // Exception: there is another acceptState
            }
        }
        addState(state);
        this.startState = state;
        this.currentState = state;
        state.setStartState(true);
        
    }

    @Override
    public void addStartState(String name, double x, double y) {
        State state = new State(name,x,y);
        addStartState(state);
    }

    @Override
    public void addAcceptState(State state) {
        addState(state);
        state.setAccepState(true);

    }

    @Override
    public void addAcceptState(String name, double x, double y) {
        State state = new State(name,x,y);
        addAcceptState(state);

    }

    @Override
    public State getStateByName(String name) {
        for (State s : states) {
            if(s.getName() == name ) {
                return s;
            }
        }
        return null; // throw Exception
    }

    public ArrayList<State> getStates() {
        return states;
    }

    public State getCurrentState(){
        return this.currentState;
    }

    

  

    
}