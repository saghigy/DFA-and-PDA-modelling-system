package main.model;

import java.util.ArrayList;


/**
 * PDATransitionValue
 */
public class PDATransitionValue {

    private State state;
    private ArrayList<Character> stackItems;

    public PDATransitionValue(State state,String stackItemsString) {
        this.state = state;
        ArrayList<Character> stackItems = new ArrayList<>();
        for (Character c : stackItemsString.toCharArray()) {
           stackItems.add(c);
        }
        this.stackItems = stackItems;

    }

    public State getState() {
        return this.state;
    }

    public ArrayList<Character> getStackItems() {
        return this.stackItems;
    }

}