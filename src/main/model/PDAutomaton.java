package main.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.json.JSONArray;

import main.model.exceptions.KeyFromStateAlreadyExistsException;
import main.model.exceptions.MissingStartStateException;

/**
 * Represents a Pushdown Automaton
 */
public class PDAutomaton extends BaseAutomaton {

    private Stack<Character> stack;
    private Map<PDATransitionKey,PDATransitionValue> transitionFunction;
    private Character startingStackItem;


    public PDAutomaton(Character startingStackItem) {
        super();
        this.startingStackItem = startingStackItem;
        transitionFunction = new HashMap<>();
        stack = new Stack<>();
        stack.add(startingStackItem);
    }
    
    /**
     * 
     * @param from
     * @param with
     * @param stackItem   '#'' means the end of the stack
     * @param to
     * @param stackString A string containing charachters that should be pushed into
     *                    the stack. Items are pushed into the stack one by one per
     *                    each character.
     * @throws KeyFromStateAlreadyExistsException
     */
    public void addTransition(State from, char with, char stackItem, State to, String stackString)
            throws KeyFromStateAlreadyExistsException {
        for (Map.Entry<PDATransitionKey,PDATransitionValue> entry : transitionFunction.entrySet() ) {
            if (entry.getKey().getState().equals(from) && entry.getKey().getLetter() == with ){
                throw new KeyFromStateAlreadyExistsException(from, with);
            }
        }
        PDATransitionKey transitionKey = new PDATransitionKey(from,with,stackItem);
        PDATransitionValue transitionValue = new PDATransitionValue(to,stackString);
        transitionFunction.put(transitionKey, transitionValue);
    }

    @Override
    public void read(char character) throws MissingStartStateException {
        if (currentState == null) {
            throw  new MissingStartStateException();
        }
        char stackItem = stack.empty() ? '#' : stack.pop();
        PDATransitionValue value = transitionFunction.get(new PDATransitionKey(this.currentState,character,stackItem));
        if(value == null) {
            //Exception: NOT SURE
            currentState = null;
        } else {
            State nextState = value.getState();
            currentState = nextState;
            for (char item : value.getStackItems() ) {
                this.stack.add(item);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.stack.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("States: \n");
        for(State s : states) {
            sb.append(s.toString() + "\n");
        }
        sb.append("Trasitions: \n");
        for (Map.Entry<PDATransitionKey,PDATransitionValue> entry : transitionFunction.entrySet() ) {
           sb.append(entry.getKey().getState().getName() + " ---------" + entry.getKey().getLetter() + " / " + entry.getKey().getStackItem() + "---------> " + entry.getValue().getState().getName() +" / " + entry.getValue().getStackItems() + "\n");
        }
        sb.append("Stack: \n");
        sb.append("_     _\n");
        for (Character item : stack) {
            sb.append(" | " + item + " |\n");
        }
        sb.append(" | # |\n");
        sb.append(" ----- ");

        return sb.toString();

    }


    public Stack<Character> getStack() {
        return this.stack;
    }


    public Map<PDATransitionKey,PDATransitionValue> getTransitionFunction() {
        return this.transitionFunction;
    }

    @Override
    public void deleteState(State state) {
        transitionFunction.entrySet().removeIf(entry -> state.equals(entry.getValue().getState()));
        transitionFunction.entrySet().removeIf(entry -> state.equals(entry.getKey().getState()));
        states.remove(state);
    }

    @Override
    public String generateFileFormat() {
        StringBuilder fileFormat = new StringBuilder();
        fileFormat.append("#AutomatonModeller-Model\n");
        fileFormat.append("type : PDA\n");
        fileFormat.append("startSymbol : " + this.startingStackItem + "\n");
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
            .map(entry -> "{ " + entry.getKey().getState().getName() + " ---------" + entry.getKey().getLetter() + " / " + entry.getKey().getStackItem() + "---------> " + entry.getValue().getState().getName() +" / " + entry.getValue().getStackItems() + " }")
            .collect(Collectors.joining(",\n")); 
        fileFormat.append(transitionFunctonString);
        fileFormat.append("\n]");


        return fileFormat.toString();
    }




}