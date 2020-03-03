package main.controller.exceptions;

/**
 * IncorrectTypeException
 */
public class IncorrectTypeException extends Exception{

    public  IncorrectTypeException() {
        super("Incorrect type in file");
    }
}