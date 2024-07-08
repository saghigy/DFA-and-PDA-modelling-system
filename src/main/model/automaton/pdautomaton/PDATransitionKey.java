package main.model.automaton.pdautomaton;

import java.io.Serializable;
import java.util.Objects;

/**
 * Transiiton key for PDAutomatons
 * Key for transition maps in PDAutomatons
 */
public class PDATransitionKey implements Serializable {

    
    private static final long serialVersionUID = 2997445396484512739L;
    private Integer stateID;
    private Character letter;
    private Character stackItem;



    public PDATransitionKey(Integer stateID, Character letter, Character stackItem) {
        this.stateID = stateID;
        this.letter = letter;
        this.stackItem = stackItem;
    }
    


    public Integer getStateID() {
        return this.stateID;
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
        return Objects.equals(stateID, pDATransitionKey.stateID) && Objects.equals(letter, pDATransitionKey.letter) && Objects.equals(stackItem, pDATransitionKey.stackItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateID, letter, stackItem);
    }
  
    
}