package main.model.exceptions;

import main.Languages;

/**
 * NoStartingStateException
 */
public class MissingStartStateException extends Exception {

    public  MissingStartStateException() {
        super(Languages.msg("NoStartState"));
    }
}