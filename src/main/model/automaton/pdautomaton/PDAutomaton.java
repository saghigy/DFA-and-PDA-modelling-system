package main.model.automaton.pdautomaton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.json.JSONArray;

import main.model.automaton.State;
import main.model.automaton.Automaton;
import main.model.automaton.BaseAutomaton;

import main.model.exceptions.KeyFromStateAlreadyExistsException;
import main.model.exceptions.MissingStartStateException;
import main.model.exceptions.StateNotFoundException;

/**
 * Represents a Pushdown Automaton
 */
public class PDAutomaton extends BaseAutomaton implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1608256280922045217L;
    /**
     * Represents the stack of the automaton
     */
    private Stack<Character> stack;
    private Map<PDATransitionKey, PDATransitionValue> transitionFunction;
    private Character startingStackItem;

    private ReadState readState;
    private int pushedCharsIndex;
    private ArrayList<Character> itemsToPush;
    private Character stackItem;

    public PDAutomaton(Character startingStackItem) {
        super();
        this.startingStackItem = startingStackItem;
        transitionFunction = new LinkedHashMap<>();
        stack = new Stack<>();
        stack.add(startingStackItem);
        readState = ReadState.POP;
        itemsToPush = new ArrayList<>();

    }

    /**
     * 
     * @param from        A state where the transition starts.
     * @param with        A character for transition key.
     * @param stackItem   Item read from the stack for transition key. '#' means the
     *                    end of the stack
     * @param to          A state where the transition ends.
     * @param stackString A string containing charachters that should be pushed into
     *                    the stack. Items are pushed into the stack one by one per
     *                    each character.
     * @throws KeyFromStateAlreadyExistsException If a transition with the given
     *                                            character already exists.
     * @throws StateNotFoundException             If one of the states is not found
     * 
     */
    public void addTransition(State from, char with, char stackItem, State to, String stackString)
            throws KeyFromStateAlreadyExistsException, StateNotFoundException {
        for (Map.Entry<PDATransitionKey, PDATransitionValue> entry : transitionFunction.entrySet()) {
            State fromState = getStateById(entry.getKey().getStateID());
            if (fromState.equals(from) && entry.getKey().getLetter() == with) {
                throw new KeyFromStateAlreadyExistsException(from, with);
            }
        }
        PDATransitionKey transitionKey = new PDATransitionKey(from.getID(), with, stackItem);
        PDATransitionValue transitionValue = new PDATransitionValue(to.getID(), stackString);
        transitionFunction.put(transitionKey, transitionValue);
    }

    @Override
    public void read(char character) throws MissingStartStateException, StateNotFoundException {
        if (currentState == null) {
            throw new MissingStartStateException();
        }

        char stackItem = stack.empty() ? '#' : stack.pop();
        PDATransitionValue value = transitionFunction
                .get(new PDATransitionKey(this.currentState.getID(), character, stackItem));
        if (value == null) {
            // Exception: NOT SURE
            currentState = null;
        } else {
            lastReadLetter = character;
            State nextState = getStateById(value.getStateID());
            previousState = currentState;
            currentState = nextState;
            for (int i = value.getStackItems().size() - 1; i >= 0; i--) {
                this.stack.push(value.getStackItems().get(i));
            }
        }

    }

    public void stepByStepRead(char character) throws MissingStartStateException, StateNotFoundException {
        if (readState == ReadState.POP) {
            if (currentState == null) {
                throw new MissingStartStateException();
            }
            this.stackItem = stack.empty() ? '#' : stack.pop();
            readState = ReadState.READ;
        } else if (readState == ReadState.READ) {
            PDATransitionValue value = transitionFunction
                    .get(new PDATransitionKey(this.currentState.getID(), character, this.stackItem));
            if (value == null) {
                currentState = null;
                readState = ReadState.POP;
            } else {
                lastReadLetter = character;
                State nextState = getStateById(value.getStateID());
                previousState = currentState;
                currentState = nextState;
                readState = ReadState.PUSH;
                pushedCharsIndex = value.getStackItems().size() - 1;
                this.itemsToPush = value.getStackItems();
            }

        } else if (readState == ReadState.PUSH) {
            if (pushedCharsIndex > 0) {
                this.stack.push(itemsToPush.get(pushedCharsIndex));
                pushedCharsIndex--;
            } else {
                this.stack.push(itemsToPush.get(pushedCharsIndex));
                pushedCharsIndex--;
                this.readState = ReadState.POP;
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.stack.clear();
        this.stack.push(this.startingStackItem);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("States: \n");
        for (State s : states) {
            sb.append(s.toString() + "\n");
        }
        sb.append("Trasitions: \n");
        for (Map.Entry<PDATransitionKey, PDATransitionValue> entry : transitionFunction.entrySet()) {
            try {
                State fromState = getStateById(entry.getKey().getStateID());
                State toState = getStateById(entry.getValue().getStateID());
                sb.append(fromState + " ---------" + entry.getKey().getLetter() + " / " + entry.getKey().getStackItem()
                        + "---------> " + toState + " / " + entry.getValue().getStackItems() + "\n");
            } catch (StateNotFoundException e) {
                sb.append("ERROR");
            }
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

    public Map<PDATransitionKey, PDATransitionValue> getTransitionFunction() {
        return this.transitionFunction;
    }

    public char getStartingStackItem() {
        return this.startingStackItem;
    }

    @Override
    public void deleteState(State state) {
        if (state.isStartState()) {
            this.startState = null;
            this.currentState = null;
            this.previousState = null;
        }
        transitionFunction.entrySet().removeIf(entry -> state.getID() == entry.getValue().getStateID());
        transitionFunction.entrySet().removeIf(entry -> state.getID() == entry.getKey().getStateID());
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
            if (i < states.size() - 1) {
                fileFormat.append(",");
            }
            fileFormat.append("\n");
        }
        fileFormat.append("]\n");
        fileFormat.append("transitions : [\n");

        String transitionFunctonString = transitionFunction.entrySet().stream().map(entry -> {
            try {
                return "{ " + getStateById(entry.getKey().getStateID()).getName() + " ---------"
                        + entry.getKey().getLetter() + " / " + entry.getKey().getStackItem() + "---------> "
                        + getStateById(entry.getValue().getStateID()).getName() + " / "
                        + entry.getValue().getStackItems() + " }";
            } catch (StateNotFoundException e) {
                return "error";
            }
        }).collect(Collectors.joining(",\n"));
        fileFormat.append(transitionFunctonString);
        fileFormat.append("\n]");

        return fileFormat.toString();
    }

    public ReadState getReadState() {
        return this.readState;
    }

    
    public PDAutomaton copyAutomaton() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outputStream);
        out.writeObject(this);
    
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ObjectInputStream in = new ObjectInputStream(inputStream);
        PDAutomaton copied = (PDAutomaton) in.readObject();
        return copied;
    }

}