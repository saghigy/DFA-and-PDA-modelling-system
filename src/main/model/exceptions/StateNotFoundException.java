package main.model.exceptions;

/**
 * StateNotFoundException
 */
public class StateNotFoundException extends Exception{


    public StateNotFoundException() {
        super("No state found with this name");
    }
    
    
}