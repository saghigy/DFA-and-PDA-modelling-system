package main.controller;

import main.model.Automaton;
import main.model.BaseAutomaton;
import main.model.DFAutomaton;
import main.model.PDAutomaton;
import main.model.State;
import main.model.exceptions.MissingStartStateException;
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

    public AutomatonController() {
        
    }

    public void addNewDFAutomaton() {
        automaton = new DFAutomaton();
        view = new AutomatonVisualisationPanel(automaton);
    }

    public void addNewDFAutomaton(DFAutomaton automaton) {
        this.automaton = automaton;
        view = new AutomatonVisualisationPanel(automaton);
        savedProject = false;

    }

    public void addNewPDAutomaton(char startingStackItem) {
        automaton = new PDAutomaton(startingStackItem);
        view = new AutomatonVisualisationPanel(automaton);
        savedProject = false;
    }

    public void addNewPDAutomaton(PDAutomaton automaton) {
        this.automaton = automaton;
        view = new AutomatonVisualisationPanel(automaton);

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

    public void save() {
        //TODO: check if the file in path exists if exists: save the new version in it else make the file and save the new version

    }

    public void saveAs(String path) {
        this.filePath = path;
        this.save();
        this.savedProject = true;
        this.latestSave = true;
        
    }


}