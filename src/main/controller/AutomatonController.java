package main.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Stack;

import org.json.Property;

import main.Languages;
import main.controller.exceptions.IncorrectTypeException;
import main.controller.regex.RegexToDFAConverter;
import main.model.automaton.Automaton;
import main.model.automaton.BaseAutomaton;
import main.model.automaton.dfautomaton.DFAutomaton;
import main.model.automaton.pdautomaton.PDAutomaton;
import main.model.automaton.State;
import main.model.automaton.pdautomaton.ReadState;
import main.model.exceptions.KeyFromStateAlreadyExistsException;
import main.model.exceptions.MissingStartStateException;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;

import main.view.automaton.AutomatonVisualisationPanel;

/**
 * AutomatonController
 */
public class AutomatonController implements Serializable {

    private static final long serialVersionUID = 1L;

    private BaseAutomaton automaton;
    private AutomatonVisualisationPanel view;
    private String wordToRead;
    private int indexOfCurrentChar;
    private String filePath;
    private boolean savedProject;
    private boolean latestSave; // change when the model is changing
    private double radiusOfStates = 20; // change later in settings
    private boolean isDFA;
    private boolean stepByStepRead;

    // uno/redo
    
    private Stack<BaseAutomaton> previousAutomatons;
    private Stack<BaseAutomaton> nextAutomatons;

    // i18n
    String language;
    String country;
    Locale currentLocale;
    ResourceBundle messages;

    public AutomatonController() {
        // language settings
        setLanguage("en", "US");
        previousAutomatons = new Stack<>();
        nextAutomatons = new Stack<>();

    }

    public AutomatonController(String language, String country) {
        // language settings
        setLanguage(language, country);
        previousAutomatons = new Stack<>();
        nextAutomatons = new Stack<>();
    }

    /**
     * Sets the language and the country.
     * 
     * @param language The wanted language.
     * @param country  The wanted country
     */
    public void setLanguage(String language, String country) {
        this.language = language;
        this.country = country;
        Languages.setLanguageAndRegion(language, country);
    }

    public void setRadiusOfStates(double radius) {
        this.radiusOfStates = radius;
        this.view.changeRadius(radius);
    }

    /**
     * Sets the automaton of the contoller to a new empty Deterministic Finite
     * Automaton.
     */
    public void addNewDFAutomaton() {
        automaton = new DFAutomaton();
        view = new AutomatonVisualisationPanel(automaton, radiusOfStates);
        savedProject = false;
        isDFA = true;

    }

    /**
     * Sets the automaton of the controller to the Deterministic Finite Automaton
     * given.
     * 
     * @param automaton The Deterministic Finite Automaton wanted to be set as the
     *                  automaton of the controller
     */
    public void addNewDFAutomaton(DFAutomaton automaton) {
        this.automaton = automaton;
        view = new AutomatonVisualisationPanel(automaton, radiusOfStates);
        savedProject = true;
        isDFA = true;
        this.latestSave = true;
    }

    /**
     * Sets the automaton of the contoller to a new empty Pushdown Automaton with a
     * starting symbol.
     * 
     * @param startingStackItem The symbol first pushed in the stack of the
     *                          automaton.
     */
    public void addNewPDAutomaton(char startingStackItem) {
        automaton = new PDAutomaton(startingStackItem);
        view = new AutomatonVisualisationPanel(automaton, radiusOfStates);
        savedProject = false;
        isDFA = false;
        stepByStepRead = false;

    }

    /**
     * Sets the automaton of the controller to the Pushdown Automaton given.
     * 
     * @param automaton The Pushdown Automaton wanted to be set as the automaton of
     *                  the controller
     */
    public void addNewPDAutomaton(PDAutomaton automaton) {
        this.automaton = automaton;
        view = new AutomatonVisualisationPanel(automaton, radiusOfStates);
        savedProject = true;
        isDFA = false;
        this.latestSave = true;
        stepByStepRead = false;

    }

    /**
     * Sets the automaton of the contoller to a Deterministic Finite Automaton read
     * from the given file.
     * 
     * @param filePath The path of the file containing a DFAutomaton's code.
     *                 (.amproj)
     * @throws StateAlreadyExistsException        If there are a state wich appears
     *                                            more than once.
     * @throws StartStateAlreadyExistsException   If there are more than one start
     *                                            state.
     * @throws StateNotFoundException             The used state in the transition
     *                                            is not declared in states.
     * @throws FileNotFoundException              The given file is not found.
     * @throws IncorrectTypeException             The given file does not declare a
     *                                            DFA.
     * @throws KeyFromStateAlreadyExistsException If there are two transition orders
     *                                            the same key to the same state.
     */
    public void addNewDFAutomaton(String filePath) throws StateAlreadyExistsException, StartStateAlreadyExistsException,
            StateNotFoundException, FileNotFoundException, IncorrectTypeException, KeyFromStateAlreadyExistsException {
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
                    if (state.isStartState()) {
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
                    nextLine = nextLine.replace(",", "");
                    nextLine = nextLine.replace("{", "");
                    nextLine = nextLine.replace("}", "");
                    nextLine = nextLine.replace("--------->", "-");
                    nextLine = nextLine.replace("---------", "-");
                    String[] lines = nextLine.split("-");

                    State from = automaton.getStateByName(lines[0]);
                    char with = lines[1].charAt(0);
                    State to = automaton.getStateByName(lines[2]);
                    automaton.addTransition(from, with, to);
                }
            }
            addNewDFAutomaton(automaton);
            this.filePath = filePath;
        } else {
            throw new IncorrectTypeException();
        }

    }

    /**
     * Sets the automaton of the contoller to a Pushdown Automaton read from the
     * given file.
     * 
     * @param filePath The path of the file containing a PDAutomaton's code.
     *                 (.amproj)
     * @throws StateAlreadyExistsException        If there are a state wich appears
     *                                            more than once.
     * @throws StartStateAlreadyExistsException   If there are more than one start
     *                                            state.
     * @throws StateNotFoundException             The used state in the transition
     *                                            is not declared in states.
     * @throws FileNotFoundException              The given file is not found.
     * @throws IncorrectTypeException             The given file does not declare a
     *                                            DFA.
     * @throws KeyFromStateAlreadyExistsException If there are two transition orders
     *                                            the same key to the same state.
     */
    public void addNewPDAutomaton(String filePath) throws StateAlreadyExistsException, StartStateAlreadyExistsException,
            StateNotFoundException, FileNotFoundException, IncorrectTypeException, KeyFromStateAlreadyExistsException {
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
                    if (state.isStartState()) {
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
                } else if (!nextLine.equals("")) {
                    nextLine = nextLine.replace(" ", "");
                    nextLine = nextLine.replace("{", "");
                    nextLine = nextLine.replace("}", "");
                    nextLine = nextLine.replace("--------->", "-");
                    nextLine = nextLine.replace("---------", "-");
                    nextLine = nextLine.replace("/", "-");
                    String[] lines = nextLine.split("-");

                    State from = automaton.getStateByName(lines[0]);
                    char with = lines[1].charAt(0);
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
            this.filePath = filePath;
        } else {
            throw new IncorrectTypeException();
        }

    }

    /**
     * Sets the automaton of the contoller to a Deterministic Finite Automaton
     * converted by a given regular expression
     * 
     * @param regex The regular expression, the automaton is going to made by.
     * 
     * @throws StateAlreadyExistsException        If there are a state wich appears
     *                                            more than once.
     * @throws StartStateAlreadyExistsException   If there are more than one start
     *                                            state.
     * @throws StateNotFoundException             The used state in the transition
     *                                            is not declared in states.
     * @throws KeyFromStateAlreadyExistsException If there are two transition orders
     *                                            the same key to the same state.
     */
    public void addNewDFAutomatonByRegex(String regex) throws StateAlreadyExistsException,
            StartStateAlreadyExistsException, StateNotFoundException, KeyFromStateAlreadyExistsException {
        addNewDFAutomaton(new RegexToDFAConverter(regex).makeDFA());
        savedProject = false;
        latestSave = false;
    }

    /**
     * Adds an input word for the controller to be readable by the controller's
     * auomaton.
     * 
     * @param word The input word added for reading.
     */
    public void addWordToRead(String word) {
        if (automaton instanceof PDAutomaton) {
            word += '#';
        }
        this.wordToRead = word;
        this.indexOfCurrentChar = 0;
    }

    /**
     * Checks if is the input word correct or not.
     * 
     * @return The input word is corect or not.
     * @throws MissingStartStateException There is no start state.
     * @throws StateNotFoundException     A state used in the read is not found.
     */
    public boolean isInputWordCorrect() throws MissingStartStateException, StateNotFoundException {

        boolean errorInReading = false;
        int i = 0;

        while (i < wordToRead.length() && !errorInReading) {
            automaton.read(wordToRead.charAt(i));
            if (automaton.getCurrentState() == null) {
                errorInReading = true;
            }
            i++;
        }
        return !errorInReading && automaton.getCurrentState().isAcceptState();
    }

    /**
     * Reads one character from the input word and modify the automaton's current
     * state.
     * 
     * @throws MissingStartStateException
     * @throws StateNotFoundException
     */
    public void nextStepInReading() throws MissingStartStateException, StateNotFoundException {

        if (!isDFA) {
            if (stepByStepRead) {
                PDAutomaton pdAutomaton = (PDAutomaton) automaton;
                pdAutomaton.stepByStepRead(this.wordToRead.charAt(this.indexOfCurrentChar));
                if (pdAutomaton.getReadState() == ReadState.POP) {
                    this.indexOfCurrentChar++; // if it's the first state of reading(pop) than it increases the index of
                                               // current char
                }
            } else {
                this.automaton.read(this.wordToRead.charAt(this.indexOfCurrentChar++));
            }
        } else {
            this.automaton.read(this.wordToRead.charAt(this.indexOfCurrentChar++));
        }
    }

    /**
     * Checks if the current state is reject state or not.
     * 
     * @return The current state is reject state or not.
     */
    public boolean isCurrentStateRejectState() {
        return automaton.getCurrentState() == null;
    }

    /**
     * Checks if is this the last letter in the input word.
     * 
     * @return Ths is the last letter of the input word or not.
     */
    public boolean isLastLetter() {
        return indexOfCurrentChar == wordToRead.length();
    }

    /**
     * Checks if the current state is accept state or not.
     * 
     * @return The current state is accept state or not.
     */
    public boolean isCurrentStateAcceptState() {
        return automaton.getCurrentState().isAcceptState();
    }

    /**
     * Resets the automaton and the reader of the input word to the start of the
     * word.
     */
    public void reset() {
        this.automaton.reset();
        this.indexOfCurrentChar = 0;
    }

    public void printCurrentOutput() {

        System.out.println(this.wordToRead.charAt(this.indexOfCurrentChar));
        System.out.println(this.automaton.getCurrentState().toString());
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

    /**
     * Saves the automaton of the controller to a filepath in controller.
     * 
     * @throws FileNotFoundException If the file does not exist.
     */
    public void save() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(filePath));
        pw.print(automaton.generateFileFormat());
        pw.flush();
        this.savedProject = true;
        this.latestSave = true;

    }

    /**
     * Save the automaton of the controller to the file given.
     * 
     * @param path The path to save the automaton.
     * @throws FileNotFoundException If the Given file is not found
     */
    public void saveAs(String path) throws FileNotFoundException {
        this.filePath = path;
        this.save();
        this.savedProject = true;
        this.latestSave = true;

    }

    /**
     * Checks if there is a state in the radius of the point given as parameter. The
     * radius is in the controller.
     * 
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     * @return There is a state in the area or not.
     */
    public State stateNear(double x, double y) {
        for (State state : automaton.getStates()) {
            if (Math.pow(state.getX() - x, 2) + Math.pow(state.getY() - y, 2) < radiusOfStates * radiusOfStates) {
                return state;
            }
        }
        return null;
    }

    /**
     * Checks if it is possible to make a state in the coordinate given. If the new
     * state would intersect an other it's rejected.
     * 
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     * @return Possible to make a state or not.
     */
    public boolean canMakeState(double x, double y) {
        boolean intersectOtherState = false;
        int i = 0;
        while (i < automaton.getStates().size() && !intersectOtherState) {
            State state = automaton.getStates().get(i);
            if (Math.pow((state.getX() - x), 2) + Math.pow((state.getY() - y), 2) < 4 * radiusOfStates
                    * radiusOfStates) {
                intersectOtherState = true;
            }
            i++;
        }
        return !intersectOtherState;
    }

    /**
     * Adds a state to the automaton.
     * 
     * @param name Name of the state.
     * @param x    x coordinate of the state.
     * @param y    y coordinate of the state.
     * @throws StateAlreadyExistsException There is already a state in the automaton
     *                                     with the same name.
     */
    public void addState(String name, double x, double y) throws StateAlreadyExistsException {
        makeUndoRedoCopy();
        automaton.addState(name, x, y);
        latestSave = false;
    }

    /**
     * Adds a start state to the automaton.
     * 
     * @param name Name of the state.
     * @param x    x coordinate of the state.
     * @param y    y coordinate of the state.
     * @throws StateAlreadyExistsException      There is already a state in the
     *                                          automaton with the same name.
     * @throws StartStateAlreadyExistsException There is already a start state.
     */
    public void addStartState(String name, double x, double y)
            throws StateAlreadyExistsException, StartStateAlreadyExistsException {
        makeUndoRedoCopy();
        automaton.addStartState(name, x, y);
        latestSave = false;
    }

    /**
     * Adds an accept state to the automaton.
     * 
     * @param name Name of the state.
     * @param x    x coordinate of the state.
     * @param y    y coordinate of the state.
     * @throws StateAlreadyExistsException There is already a state in the automaton
     *                                     with the same name.
     */
    public void addAcceptState(String name, double x, double y) throws StateAlreadyExistsException {
        makeUndoRedoCopy();
        automaton.addAcceptState(name, x, y);
        latestSave = false;
    }

    /**
     * Deletes the state given as parameter.
     * 
     * @param state Deletable state.
     */
    public void deleteState(State state) {
        makeUndoRedoCopy();
        automaton.deleteState(state);
        latestSave = false;
    }

    /**
     * Sets the given state as start state.
     * 
     * @param state State to make as start state.
     */
    public void setStateStartState(State state) throws StartStateAlreadyExistsException {
        makeUndoRedoCopy();
        automaton.setStateStartState(state);
    }

    
    /**
     * Unsets the given state from being a start state.
     * 
     * @param state State to make as start state.
     */
    public void unSetStateStartState(State state) {
        makeUndoRedoCopy();
        automaton.unSetStateStartState(state);
    }

    /**
     * Makes a transition for Pushdown Automatons.
     * 
     * @param from        A state where the transition starts.
     * @param with        A character for transition key.
     * @param stackItem   Item read from the stack for transition key. '#' means the
     *                    end of the stack
     * @param to          A state where the transition ends.
     * @param stackString A string containing charachters that should be pushed into
     *                    the stack. Items are pushed into the stack one by one per
     *                    each character.
     * @throws KeyFromStateAlreadyExistsException If a transition with the given
     *                                            character already exists.
     * @throws StateNotFoundException             If one of the states is not found
     */
    public void makePDATransition(State from, char with, char stackItem, State to, String stackString)
            throws KeyFromStateAlreadyExistsException, StateNotFoundException {
        makeUndoRedoCopy();
        PDAutomaton automaton = (PDAutomaton) this.automaton;
        automaton.addTransition(from, with, stackItem, to, stackString);
    }

    /**
     * Makes a transition for Deterministic Finite Automatons
     * 
     * @param from A state where the transition starts.
     * @param with A character for transition key.
     * @param to   A state where the transition ends.
     * @throws KeyFromStateAlreadyExistsException If a transition with the given
     *                                            character already exists.
     */
    public void makeDFATransition(State from, char with, State to) throws KeyFromStateAlreadyExistsException {
        makeUndoRedoCopy();
        DFAutomaton automaton = (DFAutomaton) this.automaton;
        automaton.addTransition(from, with, to);
    }

    /**
     * Changes the position of a state to another coordinate.
     * 
     * @param state The moveable state.
     * @param x     x coordinate.
     * @param y     x coordinate.
     * @throws StateNotFoundException The given state does not exist.
     */
    public void changePosition(State state, double x, double y) throws StateNotFoundException {
        State automatonState = automaton.getStateByName(state.getName());
        automatonState.setNewPosition(x, y);
        latestSave = false;
    }

    /**
     * Makes a copy of the automaton of the controller
     */
    public void makeUndoRedoCopy() {
        
        try {
            if(isDFA) {
                DFAutomaton a = (DFAutomaton) automaton;
                previousAutomatons.push(a.copyAutomaton());
            } else {
                PDAutomaton a = (PDAutomaton) automaton;
                previousAutomatons.push(a.copyAutomaton());

            }
            if(previousAutomatons.size() == 5) {
                previousAutomatons.removeElementAt(0);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Undo to the controller's automaton.
     */
    public void undo() {
        if(!previousAutomatons.isEmpty()) {
            boolean wasItSavedProject = savedProject;
            boolean wasItLatestSave = latestSave;
            if(isDFA) {
                nextAutomatons.push(automaton);
                DFAutomaton a  = (DFAutomaton) this.previousAutomatons.pop(); //previousAutomaton;
                addNewDFAutomaton(a);
            } else {
                nextAutomatons.push(automaton);
                PDAutomaton a  = (PDAutomaton) this.previousAutomatons.pop();// previousAutomaton;
                addNewPDAutomaton(a);
            }
            savedProject = wasItSavedProject;
            latestSave = wasItLatestSave;
        }else {
            System.out.println("EMPTY");
        }
       
       
    }

    /**
     * Undo to the controller's automaton.
     */
    public void redo() {
        if(!nextAutomatons.isEmpty()) {
            boolean wasItSavedProject = savedProject;
            boolean wasItLatestSave = latestSave;
            if(isDFA) {
                previousAutomatons.push(automaton);
                DFAutomaton a  = (DFAutomaton) this.nextAutomatons.pop(); //previousAutomaton;
                addNewDFAutomaton(a);
            } else {
                previousAutomatons.push(automaton);
                PDAutomaton a  = (PDAutomaton) this.nextAutomatons.pop();// previousAutomaton;
                addNewPDAutomaton(a);
            }
            savedProject = wasItSavedProject;
            latestSave = wasItLatestSave;
        }else {
            System.out.println("EMPTY");
        }
       
       
    }

    /**
     * Returns a colored input, shows the current letter of the input word.
     * 
     * @return The input word colored on the current letter.
     */
    public String getColoredInputWord() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        for (int i = 0; i < getWordToRead().length(); i++) {
            if (i == indexOfCurrentChar) {
                sb.append("<font color= blue>" + getWordToRead().charAt(i) + "</font>");
            } else if (i == indexOfCurrentChar - 1 && indexOfCurrentChar != 0) {
                sb.append("<font color= red>" + getWordToRead().charAt(i) + "</font>");
            } else {
                sb.append(getWordToRead().charAt(i));
            }

        }
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * Returns the view of the stack.
     * 
     * @return View of the stack.
     */
    public String getStack() {
        PDAutomaton automaton = (PDAutomaton) this.automaton;
        StringBuilder sb = new StringBuilder();
        sb.append("<html><pre >");
        sb.append("_     _\n");
        for (int i = automaton.getStack().size() - 1; i >= 0; i--) {
            sb.append(" | " + automaton.getStack().get(i) + " |\n");
        }
        sb.append(" | # |\n");
        sb.append(" ----- ");
        sb.append("</pre></html>");
        return sb.toString();
    }

    // Getters

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

    public ResourceBundle getMessages() {
        return this.messages;
    }

    public boolean isStepByStepRead() {
        return this.stepByStepRead;
    }

    public void setStepByStepRead(boolean stepByStepRead) {
        this.stepByStepRead = stepByStepRead;
    }

    public String getLanguage() {
        return this.language;
    }

    


}
