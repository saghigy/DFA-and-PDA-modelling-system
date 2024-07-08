package main.controller.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import main.model.automaton.Automaton;
import main.model.automaton.State;
import main.model.automaton.dfautomaton.DFAutomaton;
import main.model.exceptions.KeyFromStateAlreadyExistsException;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;

public class RegexToDFAConverter {

    private Set<Integer>[] followPos;
    private int stateNameCounter;
    private Set<String> input; // set of characters is used in input regex
    private HashMap<Set<Integer>, String> dStates;
    private SyntaxTree st;

    private HashMap<Integer, String> symbNum;

    public RegexToDFAConverter(String regex) {

        input = new HashSet<String>();
        dStates = new HashMap<>();
        symbNum = new HashMap<>();

        regex = regex + "#";
        
        getSymbols(regex);
        

        st = new SyntaxTree(regex);
        followPos = st.getFollowPos(); // the followpos of the syntax tree
       
        
        
          
        
    

    }

    private void getSymbols(String regex) {
        /**
         * op is a set of characters have operational meaning for example '*' could be a
         * closure operator
         */
        Set<Character> op = new HashSet<>();
        Character[] ch = { '(', ')', '*', '|', '&', '.', '\\', '[', ']', '+' };
        op.addAll(Arrays.asList(ch));

        input = new HashSet<>();
        symbNum = new HashMap<>();
        int num = 1;
        for (int i = 0; i < regex.length(); i++) {
            char charAt = regex.charAt(i);

            if (op.contains(charAt)) {
                if (i - 1 >= 0 && regex.charAt(i - 1) == '\\') {
                    input.add("\\" + charAt);
                    symbNum.put(num++, "\\" + charAt);
                }
            } else {
                input.add("" + charAt);
                symbNum.put(num++, "" + charAt);
            }
        }
    }

    public DFAutomaton makeDFA() throws StateAlreadyExistsException, StartStateAlreadyExistsException, StateNotFoundException,
            KeyFromStateAlreadyExistsException {
        DFAutomaton automaton = new DFAutomaton();
        boolean newSignPositive = true;
        double newX = 200;
        double newY = 200;
        ArrayList<Set<Integer>> followPosesToCheck = new ArrayList<>();
        State s = new State("S",100,100);
        dStates.put(st.getRoot().getFirstPos(), "S");
        followPosesToCheck.add(st.getRoot().getFirstPos());
        automaton.addStartState(s);
        Set<Integer> currentFollowPos = st.getRoot().getFirstPos();// followPos[0]; 
        int followPosCheckCounter = 0;
        input.remove("#");
        boolean isNewState= true;
        /*
        System.out.println(st.getRoot().getFirstPos());
        System.out.println(symbNum);
        for (Set<Integer> a : followPos) {
            System.out.println(a);
        }*/
        while (isNewState) {
            for (String letter : input ) {
                Set<Integer> state = new HashSet<>();
                for (Map.Entry<Integer, String> entry : symbNum.entrySet()) {
                    if(entry.getValue().equals(letter) && currentFollowPos.contains(entry.getKey())) {
                        state = Stream.concat(state.stream(),followPos[entry.getKey()-1].stream()).collect(Collectors.toSet()); 
                    }
                }
                
                if(!state.isEmpty()){
                    if(!dStates.containsKey(state)) {
                        dStates.put(state, String.valueOf(stateNameCounter));
                        followPosesToCheck.add(state);
                        
                        if(state.contains(followPos.length)){
                            automaton.addAcceptState(String.valueOf(stateNameCounter), newX, newY);
                        } else {
                            automaton.addState(String.valueOf(stateNameCounter), newX, newY);
                        }
                        if( newX == 1000) {
                            newSignPositive = false;
                            newY += 100;
                        } else if(newX == 200 && newY > 200){
                            newSignPositive = true;
                            newY += 100;
                        }
                        newX += 100 * (newSignPositive ? 1 : -1);
                        stateNameCounter++;
                    }
                    State a = automaton.getStateByName(dStates.get(currentFollowPos));
                    State b = automaton.getStateByName(dStates.get(state));
                    if(state.contains(followPos.length)){ 
                        b.setAccepState(true);
                    }
                    automaton.addTransition(a, letter.charAt(0), b);
                   
                }
            }
            
            followPosCheckCounter++;
            
            if(followPosesToCheck.size() > followPosCheckCounter){
                isNewState = true;
                currentFollowPos = followPosesToCheck.get(followPosCheckCounter);
            }else {
                isNewState = false;
            }
            
        }
        
       
        return automaton;

        
       

    }

    
}