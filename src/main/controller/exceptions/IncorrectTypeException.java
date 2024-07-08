package main.controller.exceptions;

import main.Languages;

/**
 * IncorrectTypeException
 */
public class IncorrectTypeException extends Exception{

    public  IncorrectTypeException() {
        super(Languages.msg("IncorrectType"));
    }
}