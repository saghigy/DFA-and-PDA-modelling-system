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

    private Map<DFATransitionKey, Integer> transitionFunction;
    

    public DFAutomaton() {
        super();
        transitionFunction = new HashMap<>();
    }

    /**
     * 
     * @param from A state where the transition starts.
     * @param with A character for transition key.
     * @param to A state where the transition ends.
     * @throws KeyFromStateAlreadyExistsException If a transition with the given character already exists.
     */
    public void addTransition(State from, char with, State to) throws KeyFromStateAlreadyExistsException {
        DFATransitionKey key = new DFATransitionKey(from.getID(), with);
        if (transitionFunction.get(key) == null) {
            transitionFunction.put(key, to.getID());
        } else {
            throw new KeyFromStateAlreadyExistsException(from, with);
        }
    }

    

    @Override
    public void read(char character) throws MissingStartStateException, StateNotFoundException {
        if (currentState == null) {
            throw  new MissingStartStateException();
        }
        lastReadLetter = character;
        State nextState = getStateById( transitionFunction.get(new DFATransitionKey(this.currentState.getID(),character)));
        previousState = currentState;
        currentState = nextState;
    }

    @Override
    public void reset() {
        super.reset();
    }

    public Map<DFATransitionKey,Integer> getTransitionFunction() {
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
        for (Map.Entry<DFATransitionKey,Integer> entry : transitionFunction.entrySet() ) {
            try {
            String fromStateName = getStateById( entry.getKey().getStateID()).getName();
            String toStateName = getStateById( entry.getValue()).getName();
           sb.append(fromStateName + " ---------" + entry.getKey().getLetter() + "---------> " + toStateName + "\n");
            } catch(Exception e) {
                sb.append("ERROR");
            }
        }

        return sb.toString();

    }
    
    @Override
    public void deleteState(State state) {
        transitionFunction.entrySet().removeIf(entry -> state.getID() == entry.getValue());
        transitionFunction.entrySet().removeIf(entry -> state.getID() == entry.getKey().getStateID());
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
            .map(entry -> {
                    try {
                        return "{ " + getStateById(entry.getKey().getStateID()).getName() + " ---------"
                                + entry.getKey().getLetter() + "---------> " + getStateById(entry.getValue()).getName()
                                + " }";
                    } catch (StateNotFoundException e) {
                        return "error";
                    }
                })
            .collect(Collectors.joining(",\n")); 
        fileFormat.append(transitionFunctonString);
        fileFormat.append("\n]");

        
        return fileFormat.toString();
        
    }

    


    
}