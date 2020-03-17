package main.model.exceptions;

import main.model.State;

/**
 * KeyFromStateAlreadyExistsException
 */
public class KeyFromStateAlreadyExistsException extends Exception {

    public KeyFromStateAlreadyExistsException(State state,char letter ) {
        super("Transition with " + letter + " already exists from " + state.getName());
    }
    
}