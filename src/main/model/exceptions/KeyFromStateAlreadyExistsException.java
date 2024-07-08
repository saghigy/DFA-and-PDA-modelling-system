package main.model.exceptions;

import main.Languages;
import main.model.automaton.State;

/**
 * KeyFromStateAlreadyExistsException
 */
public class KeyFromStateAlreadyExistsException extends Exception {

    public KeyFromStateAlreadyExistsException(State state,char letter ) {
        super(Languages.msg("KeyFromState1") + letter + Languages.msg("KeyFromState2") + state.getName());
    }
    
}