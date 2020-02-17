package main.model.exceptions;

/**
 * StartingStateAlreadyExistsException
 */
public class StartStateAlreadyExistsException extends Exception {

    public  StartStateAlreadyExistsException() {
        super("Start state already exists");
    }
    
}