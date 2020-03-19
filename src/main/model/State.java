package main.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;






/**
 * Represents a state of an automat
 * @author Gyorgy Saghi
 */
public class State {

    private  int id;
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

    public int getID() {
        return this.id;
    }

    public void setNewPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setId(int id) {
        this.id = id;
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
            " id='" + getID() + "'" +
            ", name='" + getName() + "'" +
            ", x='" + getX() + "'" +
            ", y='" + getY() + "'" +
            ", acceptState='" + isAcceptState() + "'" +
            ", startState='" + isStartState() + "'" +
            "}";
    }
   


    public static String codeToFileFormat(State state) {
        return "{" +
            " name='" + state.name + "'" +
            ", x='" + state.x + "'" +
            ", y='" + state.y + "'" +
            ", acceptState='" + state.acceptState + "'" +
            ", startState='" + state.startState + "'" +
            "}";

    }

    public static String stateToJSON(State state) {
        JSONObject obj = new JSONObject();

        obj.put("name", state.name);
        obj.put("x", state.x);
        obj.put("y", state.y);
        obj.put("acceptState", state.acceptState);
        obj.put("startState", state.startState);
        return obj.toString();


    }
    public static State JSONtoState(String JSON) {
        
        JSONObject obj = new JSONObject(JSON);
        String name = (String)obj.get("name");
        Double x = Double.valueOf(obj.get("x").toString());
        Double y = Double.valueOf(obj.get("y").toString());
        Boolean acceptState = (Boolean)obj.get("acceptState");
        Boolean startState = (Boolean)obj.get("startState");
        State state = new State(name,x,y);
        state.setAccepState(acceptState);
        state.setStartState(startState);
        return state;
       
       

    }

    public static State decodeFromFileFormat(String fileFormat){
        double x;
        double y;
        String name;
        fileFormat = fileFormat.replaceAll("[\n \t]", "");
        // get name
        Pattern p = Pattern.compile("name='[a-zA-Z][a-zA-Z0-9]*'"); 
        Matcher m = p.matcher(fileFormat);
        name = m.group(0).substring(6);
        // get x 
         p = Pattern.compile("x='[0-9]*.[0-9]*'");   
         m = p.matcher(fileFormat);
        x = Double.parseDouble(m.group(0).substring(3));
        //  get y
        p = Pattern.compile("y='[0-9]*.[0-9]*'"); 
        m = p.matcher(fileFormat);
        y = Double.parseDouble(m.group(0).substring(3));
        return new State(name,x,y);
       






    }
    

    







}