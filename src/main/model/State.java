package main.model;

import java.util.Objects;
/**
 * Represents a state of an automat
 * @author Gyorgy Saghi
 */
public class State {

    private  String name;
    private  double x;
    private  double y;
    private  boolean acceptState;
    private  boolean startState;

    public State( String name,  double x,  double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.acceptState = false;
        this.startState = false;
    }

    public void setAccepState(boolean acceptable) {
        this.acceptState = acceptable;
    }

    public void setStartState(boolean startState) {
        this.startState = startState;
    }

    public String getName() {
        return this.name;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
    
    public boolean isStartState() {
        return this.startState;
    }

    public boolean isAcceptState() {
        return this.acceptState;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof State)) {
            return false;
        }
        State state = (State) o;
        return Objects.equals(name, state.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, x, y, acceptState, startState);
    }


    @Override
    public String toString() {
        return "{" +
            " name='" + name + "'" +
            ", (" + x + "" +
            ", " + y + ")" +
            ", acceptState='" + acceptState + "'" +
            ", startState='" + startState + "'" +
            "}";
    }





}