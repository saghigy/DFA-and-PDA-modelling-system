package main.model;

import java.util.HashMap;
import java.util.Map;

import main.model.exceptions.MissingStartStateException;

/**
 * Representation of District Finite Automaton
 */
public class DFAutomaton extends BaseAutomaton {

    private Map<DFATransitionKey,State> transitionFunction;

    public DFAutomaton() {
        super();
        transitionFunction = new HashMap<>();
    }

    public void addTransition(State from,char with,State to) {
        DFATransitionKey key = new DFATransitionKey(from, with);
        transitionFunction.put(key, to);
    }

    @Override
    public void read(char character) throws MissingStartStateException {
        if (currentState == null) {
            throw  new MissingStartStateException();
        }
        State nextState = transitionFunction.get(new DFATransitionKey(this.currentState,character));
        if(nextState == null) {
            //Exception: NOT SURE
        } else {
            currentState = nextState;
        }
    }

    @Override
    public void reset() {
        super.reset();
    }

    public Map<DFATransitionKey,State> getTransitionFunction() {
        return this.transitionFunction;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("States: \n");
        for(State s : states) {
            sb.append(s.toString() + "\n");
        }
        sb.append("Trasitions: \n");
        for (Map.Entry<DFATransitionKey,State> entry : transitionFunction.entrySet() ) {
           sb.append(entry.getKey().getState().getName() + " ---------" + entry.getKey().getLetter() + "---------> " + entry.getValue().getName() + "\n");
        }

        return sb.toString();

    }
    
    @Override
    public void deleteState(State state) {
        transitionFunction.entrySet().removeIf(entry -> state.equals(entry.getValue()));
        transitionFunction.entrySet().removeIf(entry -> state.equals(entry.getKey().getState()));
        states.remove(state);
    }


    
}