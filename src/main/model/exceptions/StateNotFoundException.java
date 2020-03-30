package main.model.exceptions;

import main.Languages;

/**
 * StateNotFoundException
 */
public class StateNotFoundException extends Exception{


    public StateNotFoundException() {
        super(Languages.msg("NoStateFoundWithThisName"));
    }
    
    
}