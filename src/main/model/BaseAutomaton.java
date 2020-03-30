package main.model;

import java.util.ArrayList;

import main.model.exceptions.MissingStartStateException;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;


/**
 * Base of Automats with basic methods
 * @author Gyorgy Saghi
 */
public abstract class BaseAutomaton implements Automaton {

    protected int idCounter;
    protected ArrayList<State> states;
    protected State startState;
    protected State currentState;
    protected State previousState;
    protected char lastReadLetter;

    public char getLastReadLetter() {
        return this.lastReadLetter;
    }
    
    

    public BaseAutomaton() {
        idCounter = 0;
        this.states = new ArrayList<>();
    }

    @Override
    public abstract void read(char character) throws MissingStartStateException, StateNotFoundException;


    @Override
    public void reset() {
        this.currentState = this.startState;
        this.previousState = this.startState;
    }

    @Override
    public void addState(State state) throws StateAlreadyExistsException {
        for (State s : states) {
            if(s.equals(state)) {
                throw new StateAlreadyExistsException();
            }
        }
        state.setId(idCounter++);
        states.add(state);
        
    }

    @Override
    public void addState(String name, double x, double y) throws StateAlreadyExistsException {
        State state = new State(name,x,y);
        addState(state);
    }

    @Override
    public void addStartState(State state) throws StateAlreadyExistsException, StartStateAlreadyExistsException {
        for (State s : states) {
            if (s.isStartState()) {
                // Exception: there is another startState
                throw new StartStateAlreadyExistsException();
            }
        }
        addState(state);
        this.startState = state;
        this.currentState = state;
        this.previousState = state;
        state.setStartState(true);
        
    }

    @Override
    public void addStartState(String name, double x, double y) throws StateAlreadyExistsException, StartStateAlreadyExistsException {
        State state = new State(name,x,y);
        addStartState(state);
    }

    @Override
    public void addAcceptState(State state) throws StateAlreadyExistsException {
        addState(state);
        state.setAccepState(true);

    }

    @Override
    public void addAcceptState(String name, double x, double y) throws StateAlreadyExistsException {
        State state = new State(name,x,y);
        addAcceptState(state);

    }


    @Override
    public State getStateByName(String name) throws StateNotFoundException {
        for (State s : states) {
            if(s.getName().equals( name )) {
                return s;
            }
        }
        throw new StateNotFoundException();
    }

    @Override
    public State getStateById(int id) throws StateNotFoundException {
        for (State s : states) {
            if(s.getID() ==  id ) {
                return s;
            }
        }
        throw new StateNotFoundException();
    }

    @Override
    public abstract void deleteState(State state);



    public ArrayList<State> getStates() {
        return states;
    }

    public State getCurrentState(){
        return this.currentState;
    }

    public State getPreviousState(){
        return this.previousState;
    }

    @Override
    public abstract String generateFileFormat() ;

    

  

    
}