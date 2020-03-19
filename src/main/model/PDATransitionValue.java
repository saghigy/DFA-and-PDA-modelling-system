package main.model;

import java.util.ArrayList;


/**
 * PDATransitionValue
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