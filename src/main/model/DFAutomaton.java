package main.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import main.model.exceptions.KeyFromStateAlreadyExistsException;
import main.model.exceptions.MissingStartStateException;
import main.model.exceptions.StateNotFoundException;

/**
 * Representation of District Finite Automaton
 */
public class DFAutomaton extends BaseAutomaton {

    private Map<DFATransitionKey, State> transitionFunction;

    public DFAutomaton() {
        super();
        transitionFunction = new HashMap<>();
    }

    public void addTransition(State from, char with, State to) throws KeyFromStateAlreadyExistsException {
        DFATransitionKey key = new DFATransitionKey(from, with);
        if (transitionFunction.get(key) == null) {
            transitionFunction.put(key, to);
        } else {
            throw new KeyFromStateAlreadyExistsException(from, with);
        }
    }

    

    @Override
    public void read(char character) throws MissingStartStateException {
        if (currentState == null) {
            throw  new MissingStartStateException();
        }
        State nextState = transitionFunction.get(new DFATransitionKey(this.currentState,character));
        currentState = nextState;
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

    @Override
    public String generateFileFormat() {
        StringBuilder fileFormat = new StringBuilder();
        fileFormat.append("#AutomatonModeller-Model\n");
        fileFormat.append("type : DFA\n");
        fileFormat.append("states : [\n");
        for (int i = 0; i < states.size(); i++) {
            fileFormat.append(State.stateToJSON(states.get(i)));
            if(i < states.size()-1) {
                fileFormat.append(",");
            }
            fileFormat.append("\n");
        }
        fileFormat.append("]\n");
        fileFormat.append("transitions : [\n");
        String transitionFunctonString = transitionFunction.entrySet()
            .stream()
            .map(entry -> "{ " + entry.getKey().getState().getName() + " ---------" + entry.getKey().getLetter() + "---------> " + entry.getValue().getName() + " }")
            .collect(Collectors.joining(",\n")); 
        fileFormat.append(transitionFunctonString);
        fileFormat.append("\n]");


        return fileFormat.toString();
        
    }

    


    
}