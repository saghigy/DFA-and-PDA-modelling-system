package main.model.exceptions;

import main.Languages;

/**
 * StateAlreadyExistsException
 */
public class StateAlreadyExistsException extends Exception{


    public StateAlreadyExistsException() {
        super(Languages.msg("StateAlreadyExists"));
    }

    

    
}