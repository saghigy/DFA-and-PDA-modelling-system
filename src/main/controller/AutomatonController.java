package main.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import main.controller.exceptions.IncorrectTypeException;
import main.model.Automaton;
import main.model.BaseAutomaton;
import main.model.DFAutomaton;
import main.model.PDAutomaton;
import main.model.State;
import main.model.exceptions.MissingStartStateException;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;
import main.view.AutomatonVisualisationPanel;

/**
 * AutomatonController
 */
public class AutomatonController {

    private BaseAutomaton automaton;
    private AutomatonVisualisationPanel view;
    private String wordToRead;
    private int indexOfCurrentChar;
    private String filePath;
    private boolean savedProject;
    private boolean latestSave; // change when the model is changing
    private double radiusOfStates = 20; // change later in settings
    private boolean isDFA;

    public AutomatonController() {

    }

    public void addNewDFAutomaton() {
        automaton = new DFAutomaton();
        view = new AutomatonVisualisationPanel(automaton,radiusOfStates);
        savedProject = false;
        isDFA = true;
    }

    public void addNewDFAutomaton(DFAutomaton automaton) {
        this.automaton = automaton;
        view = new AutomatonVisualisationPanel(automaton,radiusOfStates);
        savedProject = true;
        isDFA = true;

    }

    public void addNewPDAutomaton(char startingStackItem) {
        automaton = new PDAutomaton(startingStackItem);
        view = new AutomatonVisualisationPanel(automaton,radiusOfStates);
        savedProject = false;
        isDFA = false;
    }

    public void addNewPDAutomaton(PDAutomaton automaton) {
        this.automaton = automaton;
        view = new AutomatonVisualisationPanel(automaton,radiusOfStates);
        savedProject = true;
        isDFA = false;

    }

    public void addNewDFAutomaton(String filePath) throws StateAlreadyExistsException, StartStateAlreadyExistsException,StateNotFoundException, FileNotFoundException,IncorrectTypeException {
        Scanner sc = new Scanner(new File(filePath));
        sc.nextLine();
        String nextLine = sc.nextLine();
        if (nextLine.contains("DFA")) {
            DFAutomaton automaton = new DFAutomaton();
            sc.nextLine();
            boolean endOfStates = false;
            while (!endOfStates) {
                nextLine = sc.nextLine();
                if (nextLine.contains("]")) {
                    endOfStates = true;
                } else {
                    State state = State.JSONtoState(nextLine);
                    if ( state.isStartState()) {
                        automaton.addStartState(state);
                    } else {
                        automaton.addState(state);
                    }
                }
            }
            sc.nextLine();
            boolean endOfTransitions = false;
            while (!endOfTransitions) {
                nextLine = sc.nextLine();
                if (nextLine.contains("]")) {
                    endOfTransitions = true;
                } else {
                    nextLine = nextLine.replace(" ", "");
                    nextLine = nextLine.replace("{", "");
                    nextLine = nextLine.replace("}", "");
                    nextLine = nextLine.replace("--------->", "-");
                    nextLine = nextLine.replace("---------", "-");
                    String[] lines = nextLine.split("-");
                    
                    State from = automaton.getStateByName(lines[0]);
                    char with =  lines[1].charAt(0);
                    State to = automaton.getStateByName(lines[2]);
                    automaton.addTransition(from, with, to);
                }
            }
            addNewDFAutomaton(automaton);
        } else {
            throw new IncorrectTypeException();
        }
        
        
    }

    public void addNewPDAutomaton(String filePath) throws StateAlreadyExistsException, StartStateAlreadyExistsException, StateNotFoundException,FileNotFoundException,IncorrectTypeException {
        Scanner sc = new Scanner(new File(filePath));
        sc.nextLine();
        String nextLine = sc.nextLine();
        if (nextLine.contains("PDA")) {
            char symbol = sc.nextLine().replace(" ", "").replace("startSymbol:", "").charAt(0);
            PDAutomaton automaton = new PDAutomaton(symbol);
            sc.nextLine();
            boolean endOfStates = false;
            while (!endOfStates) {
                nextLine = sc.nextLine();
                if (nextLine.contains("]")) {
                    endOfStates = true;
                } else {
                    State state = State.JSONtoState(nextLine);
                    if ( state.isStartState()) {
                        automaton.addStartState(state);
                    } else {
                        automaton.addState(state);
                    }
                }
            }
            sc.nextLine();
            
            boolean endOfTransitions = false;
            while (!endOfTransitions) {
                
                nextLine = sc.nextLine();
                if (nextLine.equals("]")) {
                    endOfTransitions = true;
                } else if (!nextLine.equals("")){
                    nextLine = nextLine.replace(" ", "");
                    nextLine = nextLine.replace("{", "");
                    nextLine = nextLine.replace("}", "");
                    nextLine = nextLine.replace("--------->", "-");
                    nextLine = nextLine.replace("---------", "-");
                    nextLine = nextLine.replace("/","-");
                    String[] lines = nextLine.split("-");
                    
                    State from = automaton.getStateByName(lines[0]);
                    char with =  lines[1].charAt(0);
                    char stackItem = lines[2].charAt(0);
                    State to = automaton.getStateByName(lines[3]);
                    String stackString = lines[4].replace(",", "");
                    stackString = stackString.replace(" ", "");
                    stackString = stackString.replace("[", "");
                    stackString = stackString.replace("]", "");
                    automaton.addTransition(from, with, stackItem, to, stackString);
                }
            }
            addNewPDAutomaton(automaton);
        } else {
            throw new IncorrectTypeException();
        }
        
        
    }

    public void addWordToRead(String word) {
        if (automaton instanceof PDAutomaton) {
            word += '#';
        }
        this.wordToRead = word;
        this.indexOfCurrentChar = 0;
    }

    public boolean isInputWordCorrect() throws MissingStartStateException {
        
        boolean errorInReading = false;
        int i = 0;
        
        while ( i < wordToRead.length() && !errorInReading) {
            automaton.read(wordToRead.charAt(i));
            if (automaton.getCurrentState() == null) {
                errorInReading = true;
            }
            i++;
        }
        return !errorInReading && automaton.getCurrentState().isAcceptState();
    }

    public void nextStepInReading() throws MissingStartStateException {
        this.automaton.read(this.wordToRead.charAt(this.indexOfCurrentChar++)); 
        
    }
    
    public void printCurrentOutput() {

        System.out.println(this.wordToRead.charAt(this.indexOfCurrentChar));
        System.out.println( this.automaton.getCurrentState().toString());
    }

    public State getCurrentState() {
        return this.automaton.getCurrentState();
    }

    public BaseAutomaton getAutomaton() {
        return this.automaton;
    }

    public AutomatonVisualisationPanel getView() {
        return this.view;
    }

    public void save() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(filePath));
        pw.print(automaton.generateFileFormat());
        pw.flush();
        this.savedProject = true;
        this.latestSave = true;
        

    }

    public void saveAs(String path) throws FileNotFoundException {
        this.filePath = path;
        this.save();
        this.savedProject = true;
        this.latestSave = true;
        
    }

    public State stateNear(double x, double y) {
        for (State state : automaton.getStates()) {
            if( Math.pow(state.getX() - x,2) + Math.pow(state.getY() - y,2) < radiusOfStates * radiusOfStates )  {
                return state;
            }
        }
        return null;
    }

    public boolean canMakeState(double x, double y) {
        boolean intersectOtherState = false;
        int i = 0;
        while( i <automaton.getStates().size() && !intersectOtherState) {
            State state = automaton.getStates().get(i);
            if( Math.pow((state.getX() - x),2) + Math.pow((state.getY() - y), 2) < 4*radiusOfStates*radiusOfStates) {
                intersectOtherState = true;
            }
            i++;
        }
        return !intersectOtherState;
    }

    public void addState(String name,double x, double y) throws StateAlreadyExistsException {
        automaton.addState(name,x,y);
    }

    public void addStartState(String name, double x , double y) throws StateAlreadyExistsException, StartStateAlreadyExistsException {
        automaton.addStartState(name,x,y);
    }

    public void addAcceptState(String name,double x, double y) throws StateAlreadyExistsException {
        automaton.addAcceptState(name,x,y);
    }

    public void makePDATransition(State from, char with, char stackItem, State to, String stackString)  {
        PDAutomaton automaton = (PDAutomaton)this.automaton;
        automaton.addTransition(from, with, stackItem, to, stackString);
    }

    public void makeDFATransition(State from, char with, State to)  {
        DFAutomaton automaton = (DFAutomaton)this.automaton;
        automaton.addTransition(from, with, to);
    }

    public void changePosition(State state,double x, double y) throws StateNotFoundException {
        State automatonState = automaton.getStateByName(state.getName());
        automatonState.setNewPosition(x, y);
    }

    

     public void drawView() {
        this.view.repaint();
     }


    public String getWordToRead() {
        return this.wordToRead;
    }

    public int getIndexOfCurrentChar() {
        return this.indexOfCurrentChar;
    }

    public String getFilePath() {
        return this.filePath;
    }


    public boolean isSavedProject() {
        return this.savedProject;
    }


    public boolean isLatestSave() {
        return this.latestSave;
    }


    public double getRadiusOfStates() {
        return this.radiusOfStates;
    }


    public boolean isDFA() {
        return this.isDFA;
    }
    

    
}