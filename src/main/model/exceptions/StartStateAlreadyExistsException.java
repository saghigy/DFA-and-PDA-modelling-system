package main.model.exceptions;

import main.Languages;

/**
 * StartingStateAlreadyExistsException
 */
public class StartStateAlreadyExistsException extends Exception {

    public  StartStateAlreadyExistsException() {
        super(Languages.msg("StartStateAlreadyExists"));
    }
    
}