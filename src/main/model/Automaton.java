package main.model;

import java.util.ArrayList;

/**
 * Represents the interface of automats
 * @author Gyorgy Saghi
 */
public interface Automaton {

    public void read(char character);
    public void addState(State state);
    public void addState(String name,double x, double y);
    public void addStartState(State state);
    public void addStartState(String name,double x, double y);
    public void addAcceptState(State state);
    public void addAcceptState(String name,double x, double y);
    public State getStateByName(String name);
    //public void addTransition(State from,char with,State to);
   // public void addTransition(String from,char with,String to);
    




    
}