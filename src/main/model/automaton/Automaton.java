package main.model.automaton;

import java.io.IOException;

import main.model.exceptions.MissingStartStateException;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;

/**
 * Represents the interface of automats
 * @author Gyorgy Saghi
 */
public interface Automaton {

    /**
     * Read a character and modify the automaton according to the transition function
     * @param character Character the automaton read (# counts as epsilon).
     * @throws MissingStartStateException If there is no start state in the automaton.
     * @throws StateNotFoundException If a used state index doesn't exist.
     */
    public void read(char character) throws MissingStartStateException, StateNotFoundException;

    /**
     * Resets the automaton
     */
    public void reset();
        
    /**
     * Add a state to the automat.
     * @param state State to add.
     * @throws StateAlreadyExistsException State already exists with the name of the given state.
     */
    public void addState(State state) throws StateAlreadyExistsException;

    /**
     * Add a state to the automaton with state parameters.
     * @param name  Name of the state.
     * @param x The x coordinate of the state.
     * @param y The y coordinate of the state.
     * @throws StateAlreadyExistsException State already exists with the name.
     */
    public void addState(String name,double x, double y) throws StateAlreadyExistsException;

    /**
     * Add a state to the automaton and set it as the start state, if there is no start state yet.
     * @param state State to add.
     * @throws StateAlreadyExistsException State already exists with the name of the given state.
     * @throws StartStateAlreadyExistsException There is already a start state in the automaton.
     */
    public void addStartState(State state) throws StateAlreadyExistsException, StartStateAlreadyExistsException;

    /**
     * Add a state to the automaton by state parameters and set it as the start state.
     * @param name Name of the state to add.
     * @param x Parameter x of the state to add.
     * @param y Parameter y of the state to add.
     * @throws StateAlreadyExistsException State already exists with the given name.
     * @throws StartStateAlreadyExistsException There is already a start state in the automaton.
     */
    public void addStartState(String name,double x, double y) throws StateAlreadyExistsException, StartStateAlreadyExistsException;

    /**
     * Add a state to the automaton and set it as an accept state.
     * @param state State to add as an accept state.
     * @throws StateAlreadyExistsException State already exists with the name of the given state.
     */
    public void addAcceptState(State state) throws StateAlreadyExistsException;

    /**
     * Add a state to the automaton by state parameters and set it as an accept state.
     * @param name Name of the state to add.
     * @param x Parameter x of the state to add.
     * @param y Parameter y of the state to add.
     * @throws StateAlreadyExistsException State already exists with the given name.
     */
    public void addAcceptState(String name,double x, double y) throws StateAlreadyExistsException;

    /**
     * Return the state of the automaton with the same name as the parameter
     * @param name Name of the state 
     * @return The state with the same name given as the parameter.
     * @throws StateNotFoundException There is no state with the given name in the automaton.
     */
    public State getStateByName(String name) throws StateNotFoundException;

    /**
     * Delete the state given as parameter and the transitons containing the state.
     * @param state State to delete.
     */
    public void deleteState(State state);

    /**
     * Make string format from automaton can be writeable to file.
     * @return Filewriteable string.
     * @throws StateNotFoundException A used state not found.
     */
    public String generateFileFormat();

    /**
     * Return the state by a given id.
     * @param id The id of the wanted state.
     * @return State with given id.
     * @throws StateNotFoundException There is no state with the given id in the automaton.
     */
    public State getStateById(int id) throws StateNotFoundException ;

    
    



    
}