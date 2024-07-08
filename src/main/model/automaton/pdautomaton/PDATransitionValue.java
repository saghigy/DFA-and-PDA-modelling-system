package main.model.automaton.pdautomaton;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Transiiton value for PDAutomatons
 * Value for transition maps in PDAutomatons
 */
public class PDATransitionValue implements Serializable {

    
    private static final long serialVersionUID = 7878330005146109935L;
    private Integer stateID;
    private ArrayList<Character> stackItems;

    public PDATransitionValue(Integer stateID,String stackItemsString) {
        this.stateID = stateID;
        ArrayList<Character> stackItems = new ArrayList<>();
        for (Character c : stackItemsString.toCharArray()) {
           stackItems.add(c);
        }
        this.stackItems = stackItems;

    }

    public Integer getStateID() {
        return this.stateID;
    }

    public ArrayList<Character> getStackItems() {
        return this.stackItems;
    }

}