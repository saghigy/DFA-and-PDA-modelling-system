package main.model;

import java.util.ArrayList;


/**
 * Transiiton value for PDAutomatons
 * Value for transition maps in PDAutomatons
 */
public class PDATransitionValue {

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