package main.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents a Pushdown Automaton
 */
public class PDAutomaton extends BaseAutomaton {

    private Stack<Character> stack;
    private Map<PDATransitionKey,PDATransitionValue> transitionFunction;


    public PDAutomaton(Character startingStackItem) {
        super();
        transitionFunction = new HashMap<>();
        stack = new Stack<>();
        stack.add(startingStackItem);
    }
    /**
     * 
     * @param from
     * @param with
     * @param stackItem '#'' means the end of the stack 
     * @param to 
     * @param stackString A string containing charachters that should be pushed into the stack. Items are pushed into the stack one by one per each character.
     */
    public void addTransition(State from,char with,char stackItem,State to,String stackString) {
        PDATransitionKey transitionKey = new PDATransitionKey(from,with,stackItem);
        PDATransitionValue transitionValue = new PDATransitionValue(to,stackString);
        transitionFunction.put(transitionKey, transitionValue);
    }

    @Override
    public void read(char character) {
        
        char stackItem = stack.empty() ? '#' : stack.pop();
       
        PDATransitionValue value = transitionFunction.get(new PDATransitionKey(this.currentState,character,stackItem));
        State nextState = value.getState();
        if(nextState == null) {
            //Exception: NOT SURE
        } else {
            currentState = nextState;
            for (char item : value.getStackItems() ) {
                this.stack.add(item);
            }
        }
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

}