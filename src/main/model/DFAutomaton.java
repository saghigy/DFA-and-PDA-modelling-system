package main.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of District Finite Automaton
 */
public class DFAutomaton extends BaseAutomaton {

    private Map<TransitionKey,State> transitionFunction;


    public DFAutomaton() {
        super();
        transitionFunction = new HashMap<>();
    }

    public void addTransition(State from,char with,State to) {
        TransitionKey key = new TransitionKey(from, with);
        transitionFunction.put(key, to);
    }

    @Override
    public void read(char character) {
        State nextState = transitionFunction.get(new TransitionKey(this.currentState,character));
        if(nextState == null) {
            //Exception: 
        } else {
            currentState = nextState;
        }
    }


    @Override
    public String toString() {
       String string = "States: \n";
       for(State s : states) {
            string +=  s.toString() + "\n";
       }
       string += "Trasitions: \n";
       for (Map.Entry<TransitionKey,State> entry : transitionFunction.entrySet() ) {
           string += entry.getKey().getState().getName() + " ---------" + entry.getKey().getLetter() + "---------> " + entry.getValue().getName() + "\n";
       }

       return string;

    }
    


    
}