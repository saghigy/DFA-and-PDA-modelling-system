package main.model;

import java.util.Objects;

/**
 * TransitionKey
 */
public class TransitionKey {

    private State state;
    private char letter;


    public TransitionKey(State state, char letter) {
        this.state = state;
        this.letter = letter;
    }


    public State getState() {
        return this.state;
    }

    public char getLetter() {
        return this.letter;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TransitionKey)) {
            return false;
        }
        TransitionKey transitionKey = (TransitionKey) o;
        return Objects.equals(state, transitionKey.state) && Objects.equals(letter, transitionKey.letter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, letter);
    }

}