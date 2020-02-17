package main.model;

import java.util.Objects;

/**
 * PDATransitionKey
 */
public class PDATransitionKey {

    private State state;
    private Character letter;
    private Character stackItem;



    public PDATransitionKey(State state, Character letter, Character stackItem) {
        this.state = state;
        this.letter = letter;
        this.stackItem = stackItem;
    }
    


    public State getState() {
        return this.state;
    }

    public Character getLetter() {
        return this.letter;
    }

    public Character getStackItem() {
        return this.stackItem;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PDATransitionKey)) {
            return false;
        }
        PDATransitionKey pDATransitionKey = (PDATransitionKey) o;
        return Objects.equals(state, pDATransitionKey.state) && Objects.equals(letter, pDATransitionKey.letter) && Objects.equals(stackItem, pDATransitionKey.stackItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, letter, stackItem);
    }
    
}