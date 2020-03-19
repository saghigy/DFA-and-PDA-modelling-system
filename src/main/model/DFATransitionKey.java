package main.model;

import java.util.Objects;

/**
 * TransitionKey
 */
public class DFATransitionKey {

    private int stateID;
    private Character letter;

    public DFATransitionKey(int stateID, char letter) {
        this.stateID = stateID;
        this.letter = letter;
    }

    public int getStateID() {
        return this.stateID;
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
        DFATransitionKey dFATransitionKey = (DFATransitionKey) o;
        return stateID == dFATransitionKey.stateID && Objects.equals(letter, dFATransitionKey.letter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateID, letter);
    }
   

}