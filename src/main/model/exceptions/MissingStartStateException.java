package main.model.exceptions;

/**
 * NoStartingStateException
 */
public class MissingStartStateException extends Exception {

    public  MissingStartStateException() {
        super("There is no start state in the automaton");
    }
}