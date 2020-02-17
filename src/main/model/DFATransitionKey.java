package main.model;

import java.util.Objects;

/**
 * TransitionKey
 */
public class DFATransitionKey {

    private State state;
    private Character letter;

    public DFATransitionKey(State state, char letter) {
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
        if (!(o instanceof DFATransitionKey)) {
            return false;
        }
        DFATransitionKey transitionKey = (DFATransitionKey) o;
        return Objects.equals(state, transitionKey.state) && Objects.equals(letter, transitionKey.letter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, letter);
    }

}