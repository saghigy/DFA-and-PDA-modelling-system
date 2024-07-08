package main.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.awt.Color;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.event.WindowAdapter;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import main.Languages;
import main.controller.AutomatonController;
import main.model.automaton.pdautomaton.PDAutomaton;
import main.model.automaton.pdautomaton.ReadState;
import main.model.automaton.State;
import main.model.exceptions.MissingStartStateException;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;
import main.view.help.HelpPanel;
import main.view.settings.LanguageItem;
import main.view.settings.LanguageItemRenderer;

/**
 * MainFrame
 */
public class MainFrame extends JFrame {

    // controller
    private AutomatonController controller;

    // items

    // menu items
    private JMenuBar menuBar;
    private JMenu menuEdit;
    private JMenu menuFile;
    private JMenu menuHelp;
    private JMenuItem menuItemAddNewDFA;
    private JMenuItem menuItemAddNewPDA;
    private JMenuItem menuItemOpenNewDFA;
    private JMenuItem menuItemOpenNewPDA;
    private JMenuItem menuItemSave;
    private JMenuItem menuItemSaveAs;
    private JMenuItem menuItemSettings;
    private JMenuItem menuItemGeneralHelp;
    private JMenuItem menuItemUndo;
    private JMenuItem menuItemRedo;

    private JDialog settingsDialog;
    JComboBox<LanguageItem> languageChooser;
    JComboBox<Double> radiusSizeChooser;

    // main items
    private JPanel viewPanel;
    private JPanel controllingPanel;

    // popup items
    private JPopupMenu emptySpacePopupMenu;
    private JPopupMenu statePopupMenu;

    // controlling panel items
    private JLabel wordLabel;
    private JSlider speedSlider;
    private JCheckBox stepByStep;
    private JLabel stepByStepLabel;
    private JLabel stackLabel;
    private JButton stopButton;
    private JButton runButton;
    private JButton nextButton;

    // checkers
    private boolean regexpCorrect;
    private boolean DFAsimple;
    private boolean transitionMaking;
    volatile private boolean mouseDown = false;
    volatile private boolean moveIsRunning = false;
    private boolean inputReadRunning;
    private int t; // current speed of run
    private int previousTime; // previous speed of run
    private double clickPositionX;
    private double clickPositionY;
    private State clickedState;
    private String[] yesOrNoOption;
    private long startTime;
    private boolean modellingPhase = false;

    public MainFrame() {
        initialize("hu", "HU");
    }

    public void initialize(String language, String country) {
        modellingPhase = false;
        getContentPane().removeAll();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(actionClose);
        repaint();
        ImageIcon icon = new ImageIcon(new File("resources/icon.png").getAbsolutePath());
        setIconImage(icon.getImage());
        // test start
        controller = new AutomatonController(language, country);
        String[] yesOrNoOption = { msg("Yes"), msg("No") };
        this.yesOrNoOption = yesOrNoOption;
        // basic settings

       
        setTitle("AutomatonModeller");

        // menu bar
        menuBar = new JMenuBar();
        menuFile = new JMenu(msg("File"));
        menuEdit = new JMenu(msg("Edit"));
        menuHelp = new JMenu(msg("Help"));
        menuItemAddNewDFA = new JMenuItem(msg("NewDFA"));
        menuItemAddNewPDA = new JMenuItem(msg("NewPDA"));
        menuItemOpenNewDFA = new JMenuItem(msg("OpenDFA"));
        menuItemOpenNewPDA = new JMenuItem(msg("OpenPDA"));
        menuItemSave = new JMenuItem(msg("Save"));
        menuItemSaveAs = new JMenuItem(msg("SaveAs"));
        menuItemSettings = new JMenuItem(msg("Settings"));
        menuItemUndo = new JMenuItem(msg("Undo"));
        menuItemRedo = new JMenuItem(msg("Redo"));
        menuItemGeneralHelp = new JMenuItem(msg("GeneralHelp"));

        // actions
        menuItemAddNewDFA.addActionListener(actionOpenNewDFAWindow);
        menuItemAddNewPDA.addActionListener(actionOpenNewPDAWindow);
        menuItemOpenNewDFA.addActionListener(actionOpenMakeDFAWindow);
        menuItemOpenNewPDA.addActionListener(actionOpenMakePDAWindow);
        menuItemSave.addActionListener(actionSave);
        menuItemSaveAs.addActionListener(actionSaveAs);
        menuItemSettings.addActionListener(actionSettings);
        menuItemUndo.addActionListener(actionUndo);
        menuItemRedo.addActionListener(actionRedo);
        menuItemGeneralHelp.addActionListener(actionHelp);

        // accelerators
        menuItemAddNewDFA
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        menuItemAddNewPDA
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        menuItemOpenNewDFA
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        menuItemOpenNewPDA
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        menuItemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        menuItemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        menuItemSaveAs
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        menuItemSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, ActionEvent.CTRL_MASK));

        menuFile.add(menuItemAddNewDFA);
        menuFile.add(menuItemAddNewPDA);
        menuFile.addSeparator();
        menuFile.add(menuItemOpenNewDFA);
        menuFile.add(menuItemOpenNewPDA);
        menuFile.addSeparator();
        menuFile.add(menuItemSave);
        menuFile.add(menuItemSaveAs);

        menuEdit.add(menuItemSettings);
        menuEdit.add(menuItemUndo);
        menuEdit.add(menuItemRedo);

        menuHelp.add(menuItemGeneralHelp);

        // disabled while no project opened
        menuItemSave.setEnabled(false);
        menuItemSaveAs.setEnabled(false);
        menuItemUndo.setEnabled(false);
        menuItemRedo.setEnabled(false);


        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // main panel
         
        
        JPanel p = new JPanel();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='margin: 30 10 10 30;width: 1000px'>");
        sb.append("<h1 style=font-size:50px>");
        sb.append(msg("Welcome"));
        sb.append("</h1>");
        sb.append("<h2 style='font-size:20px;border-bottom:1px solid gray;margin-bottom:20px;'>");
        sb.append(msg("AboutLabel"));
        sb.append("</h2>");
        sb.append("<p style='font-size:12px;margin-bottom:20px;'>");
        sb.append(msg("AboutMsg"));
        sb.append("</p>");
        sb.append("<h2 style='font-size:20px;border-bottom:1px solid gray;margin-bottom:20px;'>");
        sb.append(msg("Help"));
        sb.append("</h2>");
        sb.append("<p style='font-size:11px;margin-bottom:20px;'>");
        sb.append(msg("HelpMsg"));
        sb.append("<a href=''>");
        sb.append(msg("HelpLink"));
        sb.append("</a>");
        sb.append(msg("HelpMsg2"));
        sb.append("</p>");
        sb.append("</body></html>");

        JLabel welcomeLabel = new JLabel(sb.toString());
        JEditorPane jEditorPane = new JEditorPane("text/html", sb.toString());
        jEditorPane.setEditable(false);
        jEditorPane.setOpaque(false);
        p.add(welcomeLabel);
        jEditorPane.setLayout(null);
        p.setLayout(null);
        welcomeLabel.setBounds (65, 70, 1000, 500);
        jEditorPane.setBounds (65, 70, 1000, 500);
        jEditorPane.addHyperlinkListener(new HyperlinkListener(){
        
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                try {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        new HelpPanel(MainFrame.this);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        this.getContentPane().add(jEditorPane);
        transitionMaking = false;

        

        setVisible(true);

    }

    /**
     * Function for i18n. Returns the correct string for language.
     * 
     * @param key The key of the string.
     * @return String on the current language.
     */
    public String msg(String key) {
        return Languages.msg(key);
        // return controller.getMessages().getString(key);
    }

    /**
     * Sets the title of the project. [*][filePath]AutomatonModeller
     * <ul>
     * <li>* : The project is not saved.
     * <li>filePath: The path of the project. Appears if it is a saved/opened
     * project. </u>
     */
    private void setProjectTitle() {
        StringBuilder title = new StringBuilder();
        if (controller.isSavedProject()) {
            if (!controller.isLatestSave()) {
                title.append("*");
            }
            title.append(controller.getFilePath());
            title.append(" - ");
        }
        title.append("AutomatonModeller");
        this.setTitle(title.toString());
    }

    /**
     * Make the modeller window with the modelling controller panel and the
     * visualization panel.
     * 
     * @param isDFA Is the type of the automaton DFA.
     */
    private void makeWindow(boolean isDFA) {
        modellingPhase = true;

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().removeAll();

        // making view panel
        viewPanel = controller.getView();// new JPanel();
        // making popup menu

        // popup menu for clicking an empty space
        emptySpacePopupMenu = new JPopupMenu();
        JMenuItem simpleStateAdder = new JMenuItem(msg("AddSimpleState"));
        JMenuItem startStateAdder = new JMenuItem(msg("AddStartState"));
        JMenuItem acceptStateAdder = new JMenuItem(msg("AddAcceptState"));

        // adding actions
        simpleStateAdder.addActionListener(actionMakeState);
        startStateAdder.addActionListener(actionMakeStartState);
        acceptStateAdder.addActionListener(actionMakeAcceptState);

        // adding to menu
        emptySpacePopupMenu.add(simpleStateAdder);
        emptySpacePopupMenu.add(startStateAdder);
        emptySpacePopupMenu.add(acceptStateAdder);

        viewPanel.addMouseListener(actionMouseClick);

        // viewPanel = controller.getView();
        controllingPanel = new JPanel(null);
        controllingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        // controllingPanel.add(controllingLabel);
        controllingPanel.setPreferredSize(new Dimension(300, 400));
        // JPanel runPanel = new JPanel(null);
        // buttons
        nextButton = new JButton(msg("Next"));
        runButton = new JButton(msg("Run"));
        stopButton = new JButton(msg("Stop"));
        nextButton.setEnabled(false);
        runButton.setEnabled(false);
        stopButton.setEnabled(false);
        wordLabel = new JLabel();
        JButton wordAdderButton = new JButton(msg("AddNewWord"));
        wordLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        JLabel speedLabel = new JLabel(msg("Speed"));

        // options of the slider
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTrack(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setEnabled(false);

        // actions
        wordAdderButton.addActionListener(actionAddWord);
        nextButton.addActionListener(actionRead);
        runButton.addActionListener(actionRun);
        stopButton.addActionListener(actionStopRun);
        speedSlider.addChangeListener(actionSpeedChange);

        // adding to panel
        controllingPanel.add(wordLabel);
        controllingPanel.add(nextButton);
        controllingPanel.add(runButton);
        controllingPanel.add(speedLabel);
        controllingPanel.add(speedSlider);
        controllingPanel.add(wordAdderButton);
        controllingPanel.add(stopButton);

        // bounds
        speedSlider.setBounds(10, 290, 275, 50);
        wordLabel.setBounds(10, 135, 280, 45);
        speedLabel.setBounds(15, 255, 100, 25);
        nextButton.setBounds(185, 365, 100, 25);
        runButton.setBounds(35, 365, 80, 25);
        wordAdderButton.setBounds(25, 200, 140, 25);
        stopButton.setBounds(35, 400, 80, 25);

        // stack and step by step settings
        if (!controller.isDFA()) {
            stackLabel = new JLabel("", SwingConstants.CENTER);
            JScrollPane scroller = new JScrollPane(stackLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            stepByStep = new JCheckBox();
            stepByStepLabel = new JLabel(msg("StepByStepLabel"));
            drawStack();
            scroller.setBorder(BorderFactory.createLineBorder(Color.black));
            controllingPanel.add(scroller);
            controllingPanel.add(stepByStep);
            controllingPanel.add(stepByStepLabel);
            scroller.setBounds(100, 550, 100, 200);
            stepByStepLabel.setBounds(35, 440, 150, 30);
            stepByStep.setBounds(120, 440, 30, 30);
            stepByStep.addActionListener(actionStepByStep);

        }

        this.getContentPane().add(viewPanel);
        this.getContentPane().add(controllingPanel, BorderLayout.EAST);

        this.repaint();
        this.revalidate();
    }

    /**
     * Draws the stack in the stack label.
     */
    private void drawStack() {
        stackLabel.setText(controller.getStack());
    }

    /**
     * Check if the state moving is allowed.
     * 
     * @return The state moving is allowed.
     */
    private synchronized boolean checkAndMark() {
        if (moveIsRunning)
            return false;
        moveIsRunning = true;
        return true;
    }

    /**
     * Moving the given state where the cursor is.
     * 
     * @param state The movable state.
     */
    private void moveThread(State state) {
        if (checkAndMark()) {
            new Thread() {
                public void run() {
                    do {

                        try {
                            controller.changePosition(state, MouseInfo.getPointerInfo().getLocation().getX(),
                                    MouseInfo.getPointerInfo().getLocation().getY());
                            getContentPane().repaint();
                            setProjectTitle();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } while (mouseDown);
                    moveIsRunning = false;
                }
            }.start();
        }
    }

    /**
     * Sets the controlling panel enabled ord disabled depends on the parameter.
     * @param enable Enable the panel or not
     */
    private void setControllingPanelEnabled(boolean enable) {
        for (Component com : controllingPanel.getComponents()) {
            if (com != nextButton && com != runButton && com != stopButton && com != speedSlider)
                com.setEnabled(enable);

        }
    }

    /**
     * Reading a char from input word and modifies the view. Check if the current
     * state exists, if not shows error. If this was the last read check if the
     * state is accepted or rejected.
     * 
     * @throws MissingStartStateException There is no start state in the automaton.
     * @throws StateNotFoundException     A used state is not found.
     */
    private void charRead() throws MissingStartStateException, StateNotFoundException {

        controller.nextStepInReading();
        // if its PDA draw stack and check stepByStep reading
        if (!controller.isDFA()) {
            drawStack();
            PDAutomaton pdAutomaton = (PDAutomaton) controller.getAutomaton();
            if (pdAutomaton.getReadState() != ReadState.POP) {
                stepByStep.setEnabled(false);
            } else {
                stepByStep.setEnabled(true);
            }
        }
        // is it an existing state or not
        if (controller.isCurrentStateRejectState()) {
            JOptionPane.showMessageDialog(MainFrame.this, msg("RejectStateMsg"), msg("ErrorLabel"),
                    JOptionPane.ERROR_MESSAGE);
            // reset the project
            controller.reset();
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
            inputReadRunning = false;
            wordLabel.setText(controller.getColoredInputWord());
            if (!controller.isDFA())
                drawStack();
        }
        // draw changes
        wordLabel.setText(controller.getColoredInputWord());
        getContentPane().repaint();
        // check if it is the last letter
        if (controller.isLastLetter()) {
            if (controller.isCurrentStateAcceptState()) { // accepted
                JOptionPane.showMessageDialog(MainFrame.this, msg("AcceptedMsg"), msg("AcceptedLabel"),
                        JOptionPane.INFORMATION_MESSAGE);
            } else {    // not accepted
                JOptionPane.showMessageDialog(MainFrame.this, msg("NotAcceptedMsg"), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }

            inputReadRunning = false;
            controller.reset();
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
            wordLabel.setText(controller.getColoredInputWord());

            if (!controller.isDFA())
                drawStack();
        }

        getContentPane().repaint();

    }

    /**
     * Run of the reading with the speed of read by the speed slider.
     */
    private void runWithTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                try {

                    if (!inputReadRunning) {
                        timer.cancel();
                        timer.purge();
                    } else {
                        if (previousTime != t) {
                            previousTime = t;
                            timer.cancel();
                            runWithTimer();
                        }

                        charRead();

                    }

                } catch (Exception e1) {

                }

            }
        }, 0, t * 1000);
    }

    // Actions

    /**
     * Action for clicking on the modelling panel.
     */
    private MouseListener actionMouseClick = new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent me) {
            // popup menu for clicking on a state
            statePopupMenu = new JPopupMenu();
            JMenuItem transitionAdder = new JMenuItem(msg("AddTransition"));
            JMenuItem acceptStateSetter = new JMenuItem(msg("SetToAcceptState"));
            JMenuItem startStateSetter = new JMenuItem(msg("SetToStartState"));
            JMenuItem stateDeleter = new JMenuItem(msg("DeleteState"));
            JMenuItem acceptStateUnSetter = new JMenuItem(msg("UnSetToAcceptState"));
            JMenuItem startStateUnSetter = new JMenuItem(msg("UnSetToStartState"));

            // adding actions
            acceptStateSetter.addActionListener(actionSetToAcceptState);
            startStateSetter.addActionListener(actionSetToStartState);
            transitionAdder.addActionListener(actionMakeTransition);
            stateDeleter.addActionListener(actionDeleteState);
            acceptStateUnSetter.addActionListener(actionUnSetFromAcceptState);
            startStateUnSetter.addActionListener(actionUnSetFromStartState);
            // adding to menu
            statePopupMenu.add(transitionAdder);
            statePopupMenu.add(stateDeleter);

            int x = me.getX();
            int y = me.getY();
            MainFrame.this.clickPositionX = x;
            MainFrame.this.clickPositionY = y;

            State stateNear = controller.stateNear(x, y);
            if (!transitionMaking) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    // no state in clicked area
                    if (stateNear == null) {
                        emptySpacePopupMenu.show(me.getComponent(), x, y);
                    } else {
                        clickedState = stateNear;
                        if (!clickedState.isStartState()) {
                            statePopupMenu.add(startStateSetter);
                        } else {
                            statePopupMenu.add(startStateUnSetter);
                        }
                        if (!clickedState.isAcceptState()) {
                            statePopupMenu.add(acceptStateSetter);
                        } else {
                            statePopupMenu.add(acceptStateUnSetter);
                        }
                        statePopupMenu.show(me.getComponent(), x, y);
                    }
                }
            } else {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    if (stateNear == null) {
                        JOptionPane.showMessageDialog(MainFrame.this, msg("NoStateNear"), msg("ErrorLabel"),
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        try {

                            if (controller.isDFA()) {
                                // making dialog for transition
                                String name;
                                do {
                                    name = JOptionPane.showInputDialog(msg("LetterForTransition"));
                                    if (name != null) {
                                        if (name.length() == 1) {

                                            char with = name.charAt(0);
                                            controller.makeDFATransition(clickedState, with, stateNear);
                                        } else {
                                            JOptionPane.showMessageDialog(MainFrame.this,
                                                    msg("TransitionNotGoodFormat"), msg("ErrorLabel"),
                                                    JOptionPane.ERROR_MESSAGE);
                                        }

                                    }
                                } while (name != null && name.length() != 1);

                            } else {
                                // addig transition
                                String name;
                                do {
                                    name = JOptionPane.showInputDialog(msg("PDATransition"));
                                    if (name != null) {
                                        name = name.replace(" ", "");
                                        if (name.matches("./.->.*")) {
                                            String[] items = name.split("->");
                                            String[] value = items[0].split("/");
                                            char with = value[0].charAt(0);
                                            char stackItem = value[1].charAt(0);
                                            String stackString = "";
                                            if (items.length == 2) {
                                                stackString = items[1];
                                            }
                                            controller.makePDATransition(clickedState, with, stackItem, stateNear,
                                                    stackString);
                                        } else {
                                            JOptionPane.showMessageDialog(MainFrame.this,
                                                    msg("TransitionNotGoodFormat"), msg("ErrorLabel"),
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                } while (name != null && !name.matches("./.->.*"));
                            }
                            transitionMaking = false;
                            setControllingPanelEnabled(true);
                            getContentPane().repaint();
                            setProjectTitle();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(MainFrame.this, e.getMessage(), msg("ErrorLabel"),
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    }
                } else {
                    transitionMaking = false;
                    setControllingPanelEnabled(true);

                }

            }

        }

        @Override
        public void mousePressed(MouseEvent me) {
            int x = me.getX();
            int y = me.getY();
            MainFrame.this.clickPositionX = x;
            MainFrame.this.clickPositionY = y;
            State stateNear = controller.stateNear(x, y);
            if (SwingUtilities.isLeftMouseButton(me) && stateNear != null && !transitionMaking) {
                mouseDown = true;
                controller.makeUndoRedoCopy();
                moveThread(stateNear);
            }

        }

        @Override
        public void mouseReleased(MouseEvent me) {
            mouseDown = false;

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

    };

    /**
     * Action which opens a window where user can make a new DFAutomaton. An empty
     * one or on from a regular expression.
     */
    private AbstractAction actionOpenNewDFAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {

            JDialog dialog = new JDialog(MainFrame.this);
            dialog.setLocationRelativeTo(MainFrame.this);
            dialog.setTitle(msg("NewDFA"));
            dialog.setSize(300, 150);

            JPanel panel = new JPanel();
            // panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

            // radio panel
            JPanel radioPanel = new JPanel();
            JRadioButton emptyAutomaton = new JRadioButton(msg("MakeEmptyAutomaton"));
            JRadioButton regexpAutomaton = new JRadioButton(msg("MakeAutomatonByRegex"));
            ButtonGroup group = new ButtonGroup();
            group.add(emptyAutomaton);
            group.add(regexpAutomaton);
            radioPanel.add(emptyAutomaton);
            radioPanel.add(regexpAutomaton);
            radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));

            // regexp panel
            JPanel regexpPanel = new JPanel();
            JTextField regexp = new JTextField(10);
            JLabel checkLabel = new JLabel();
            Icon fineIcon = new ImageIcon(new ImageIcon("resources/regexp_fine.png").getImage().getScaledInstance(21,
                    21, Image.SCALE_DEFAULT));

            Icon badIcon = new ImageIcon(new ImageIcon("resources/regexp_bad.png").getImage().getScaledInstance(21, 21,
                    Image.SCALE_DEFAULT));

            regexp.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    checkIfRegexpCorrect();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkIfRegexpCorrect();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkIfRegexpCorrect();
                }

                public void checkIfRegexpCorrect() {

                    String regex = regexp.getText();
                    PatternSyntaxException exc = null;
                    try {
                        Pattern.compile(regex);
                    } catch (PatternSyntaxException e) {
                        exc = e;
                    }
                    if (exc != null ) {
                        checkLabel.setIcon(badIcon);
                        regexpCorrect = false;

                    } else if(!regex.matches("[\\*\\|a-zA-Z1-9\\(\\)]*")) {
                        checkLabel.setIcon(badIcon);
                        regexpCorrect = false;
                    }else {
                        checkLabel.setIcon(fineIcon);
                        regexpCorrect = true;
                    }
                }
            });
            checkLabel.setIcon(fineIcon);
            regexpCorrect = true;
            regexpPanel.add(regexp);
            regexpPanel.add(checkLabel);
            regexpPanel.setLayout(new BoxLayout(regexpPanel, BoxLayout.X_AXIS));
            regexpPanel.setVisible(false);

            // button actions
            regexpAutomaton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    regexpPanel.setVisible(true);
                    DFAsimple = false;

                }
            });
            emptyAutomaton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    regexpPanel.setVisible(false);
                    DFAsimple = true;
                }
            });

            // Generate button
            JButton generateButton = new JButton(msg("Generate"));
            generateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (DFAsimple) {
                            controller.addNewDFAutomaton();
                            dialog.dispose();

                            MainFrame.this.makeWindow(true);
                            setProjectTitle();
                        } else if (regexpCorrect) {
                            controller.addNewDFAutomatonByRegex(regexp.getText());
                            dialog.dispose();
                            MainFrame.this.makeWindow(true);
                            setProjectTitle();
                        } else {
                            JOptionPane.showMessageDialog(MainFrame.this, msg("RegexIsNotCorrect"), msg("ErrorLabel"),
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(MainFrame.this, e.getMessage(), msg("ErrorLabel"),
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            menuItemSave.setEnabled(true); // set save items enable
            menuItemSaveAs.setEnabled(true);
            menuItemUndo.setEnabled(true);
            menuItemRedo.setEnabled(true);
            panel.add(radioPanel);
            panel.add(regexpPanel);
            panel.add(generateButton);

            dialog.add(panel);
            dialog.setVisible(true);

        }

    };

    /**
     * Action which opens a window where the user can make a PDAutomaton with a
     * start symbol.
     */
    private AbstractAction actionOpenNewPDAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog dialog = new JDialog(MainFrame.this);
            dialog.setLocationRelativeTo(MainFrame.this); 
            dialog.setTitle(msg("NewPDA"));
            dialog.setSize(200, 100);

            JPanel mainPanel = new JPanel();

            // start symbol
            JPanel startSymbolPanel = new JPanel(new BorderLayout(8, 8));

            JLabel startSymbolLabel = new JLabel(msg("StartSymbolLabel"), JLabel.TRAILING);
            startSymbolPanel.add(startSymbolLabel, BorderLayout.WEST);
            JTextField startSymbolText = new JTextField(2);
            startSymbolText.setHorizontalAlignment(JTextField.CENTER);
            startSymbolLabel.setLabelFor(startSymbolText);
            startSymbolPanel.add(startSymbolText, BorderLayout.EAST);

            // generate button
            JButton generateButton = new JButton(msg("Generate"));
            generateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (startSymbolText.getText().length() == 1) {
                        char startSymbol = startSymbolText.getText().charAt(0);
                        controller.addNewPDAutomaton(startSymbol);
                        dialog.dispose();

                        MainFrame.this.makeWindow(false);
                        setProjectTitle();
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, msg("StartSymbolNotOneCharLong"),
                                msg("ErrorLabel"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            menuItemSave.setEnabled(true); // set save items enable
            menuItemSaveAs.setEnabled(true);
            menuItemUndo.setEnabled(true);
            menuItemRedo.setEnabled(true);
            mainPanel.add(startSymbolPanel);
            mainPanel.add(generateButton);
            dialog.add(mainPanel);

            dialog.setVisible(true);

        }
    };

    /**
     * Action which opens a window where the user can open a DFAutomaton from file.
     */
    private AbstractAction actionOpenMakeDFAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Automaton projects (.amproj)", "amproj");
                fileChooser.setFileFilter(filter);
                fileChooser.setDialogTitle(msg("OpenDFA"));
                int returnValue = fileChooser.showOpenDialog(MainFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    controller.addNewDFAutomaton(filePath);

                    MainFrame.this.makeWindow(true);
                    setProjectTitle();
                    menuItemSave.setEnabled(true); // set save items enable
                    menuItemSaveAs.setEnabled(true);
                    menuItemUndo.setEnabled(true);
                    menuItemRedo.setEnabled(true);

                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    /**
     * Action which opens a window where the user can open a PDAutomaton from file.
     */
    private AbstractAction actionOpenMakePDAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Automaton projects (.amproj)", "amproj");
                fileChooser.setFileFilter(filter);
                fileChooser.setDialogTitle(msg("OpenPDA"));
                int returnValue = fileChooser.showOpenDialog(MainFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    controller.addNewPDAutomaton(filePath);

                    MainFrame.this.makeWindow(false);
                    setProjectTitle();
                    menuItemSave.setEnabled(true); // set save items enable
                    menuItemSaveAs.setEnabled(true);
                    menuItemUndo.setEnabled(true);
                    menuItemRedo.setEnabled(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    /**
     * Action which saves the current stage of the automaton.
     */
    private AbstractAction actionSave = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (!controller.isSavedProject()) {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Automaton projects (.amproj)",
                            "amproj");
                    fileChooser.setFileFilter(filter);
                    fileChooser.setSelectedFile(new File("untitled.amproj"));
                    fileChooser.setDialogTitle(msg("SaveAs"));
                    int returnValue = fileChooser.showSaveDialog(MainFrame.this);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File filePath = fileChooser.getSelectedFile();
                        if (filePath.exists()) {
                            int n = JOptionPane.showOptionDialog(MainFrame.this, msg("OverwriteMsg"),
                                    msg("OverWriteLabel"), 0, JOptionPane.INFORMATION_MESSAGE, null, yesOrNoOption,
                                    null);

                            if (n == JOptionPane.YES_OPTION) {
                                MainFrame.this.controller.saveAs(filePath.getAbsolutePath());
                            }
                        } else {
                            MainFrame.this.controller.saveAs(filePath.getAbsolutePath());
                        }
                    }
                } else {

                    if (!MainFrame.this.controller.isLatestSave()) {
                        MainFrame.this.controller.save();
                    }

                }
                setProjectTitle();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    /**
     * Action which opens a window where the user can choose a file to save the
     * current stage of the automaton.
     */
    private AbstractAction actionSaveAs = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Automaton projects (.amproj)", "amproj");
                fileChooser.setFileFilter(filter);
                fileChooser.setSelectedFile(new File("untitled.amproj"));
                fileChooser.setDialogTitle(msg("SaveAs"));
                int returnValue = fileChooser.showSaveDialog(MainFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File filePath = fileChooser.getSelectedFile();
                    if (filePath.exists()) {
                        int n = JOptionPane.showConfirmDialog(MainFrame.this, msg("OverwriteMsg"),
                                msg("OverWriteLabel"), JOptionPane.YES_NO_OPTION);

                        if (n == JOptionPane.YES_OPTION) {
                            MainFrame.this.controller.saveAs(filePath.getAbsolutePath());
                        }
                    } else {
                        MainFrame.this.controller.saveAs(filePath.getAbsolutePath());
                    }
                }
                setProjectTitle();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    };

    /**
     * Action to make a state where the clicked position is if it is possible.
     */
    private AbstractAction actionMakeState = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // circle in circle : (x-x0)^2 + (y-y0)^2 < 4r^2
            double x = MainFrame.this.clickPositionX;
            double y = MainFrame.this.clickPositionY;
            if (controller.canMakeState(x, y)) {
                // add state
                try {
                    String name = JOptionPane.showInputDialog(msg("Name"));
                    if (name != null) {
                        controller.addState(name, x, y);
                        getContentPane().repaint();
                    }

                    // draw
                } catch (StateAlreadyExistsException e1) {
                    JOptionPane.showMessageDialog(MainFrame.this, e1.getMessage(), msg("ErrorLabel"),
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, msg("NoItersectMsg"), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }
            setProjectTitle();
        }
    };

    /**
     * Action to make an accept state where the clicked position is if it is
     * possible.
     */
    private AbstractAction actionMakeAcceptState = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // circle in circle : (x-x0)^2 + (y-y0)^2 < 4r^2
            double x = MainFrame.this.clickPositionX;
            double y = MainFrame.this.clickPositionY;
            if (controller.canMakeState(x, y)) {
                // add state
                try {
                    // TODO: adding state
                    // draw tmp circle->get name
                    String name = JOptionPane.showInputDialog(msg("Name"));
                    if (name != null) {
                        controller.addAcceptState(name, x, y);
                        getContentPane().repaint();
                    }

                    // draw
                } catch (StateAlreadyExistsException e1) {
                    JOptionPane.showMessageDialog(MainFrame.this, e1.getMessage(), msg("ErrorLabel"),
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, msg("NoItersectMsg"), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }
            setProjectTitle();
        }
    };

    /**
     * Action to make a start state where the clicked position is if it is possible.
     */
    private AbstractAction actionMakeStartState = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // circle in circle : (x-x0)^2 + (y-y0)^2 < 4r^2
            double x = MainFrame.this.clickPositionX;
            double y = MainFrame.this.clickPositionY;
            if (controller.canMakeState(x, y)) {
                // add state
                try {
                    // TODO: adding state
                    // draw tmp circle->get name
                    String name = JOptionPane.showInputDialog(msg("Name"));
                    if (name != null) {
                        controller.addStartState(name, x, y);
                        getContentPane().repaint();
                    }

                    // draw
                } catch (StateAlreadyExistsException e1) {
                    JOptionPane.showMessageDialog(MainFrame.this, e1.getMessage(), msg("ErrorLabel"),
                            JOptionPane.ERROR_MESSAGE);
                } catch (StartStateAlreadyExistsException e2) {
                    JOptionPane.showMessageDialog(MainFrame.this, e2.getMessage(), msg("ErrorLabel"),
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, msg("NoItersectMsg"), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }
            setProjectTitle();
        }

    };

    /**
     * Action to set the choosen state to accept state.
     */
    private AbstractAction actionSetToAcceptState = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            clickedState.setAccepState(true);

            getContentPane().repaint();
            setProjectTitle();
        }
    };

    /**
     * Action to unset the choosen state from accept state.
     */
    private AbstractAction actionUnSetFromAcceptState = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            clickedState.setAccepState(false);

            getContentPane().repaint();
            setProjectTitle();
        }
    };

    private AbstractAction actionSetToStartState = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                controller.setStateStartState(clickedState);

            } catch (StartStateAlreadyExistsException e1) {
                JOptionPane.showMessageDialog(MainFrame.this, e1.getMessage(), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }

            getContentPane().repaint();
            setProjectTitle();

        }
    };

    private AbstractAction actionUnSetFromStartState = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            controller.unSetStateStartState(clickedState);
            getContentPane().repaint();
            setProjectTitle();

        }
    };

    /**
     * Action to delete a choosen state
     */
    private AbstractAction actionDeleteState = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            controller.deleteState(clickedState);

            getContentPane().repaint();
            setProjectTitle();
        }
    };

    /**
     * Action which allows transition making.
     */
    private AbstractAction actionMakeTransition = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            transitionMaking = true;
            setControllingPanelEnabled(false);
        }
    };

    /**
     * Action to add a word as the input word.
     */
    private AbstractAction actionAddWord = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {

            String input = JOptionPane.showInputDialog(msg("InputWordLabel"));
            if (input != null && !input.equals("")) {
                nextButton.setEnabled(true);
                runButton.setEnabled(true);
                speedSlider.setEnabled(true);
                controller.addWordToRead(input);
                wordLabel.setText(controller.getColoredInputWord());
                controller.reset();
                getContentPane().repaint();
            }

        }
    };

    /**
     * Action to read a character from the inut word.
     */
    private AbstractAction actionRead = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                charRead();
            } catch (Exception e1) {

                JOptionPane.showMessageDialog(MainFrame.this, e1.getMessage(), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    };

    /**
     * Action to run a reading of the input word. It's speed is setable from the
     * speed slider.
     */
    private AbstractAction actionRun = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (controller.getAutomaton().getCurrentState() != null) {
                stopButton.setEnabled(true);
                runButton.setEnabled(false);
                t = speedSlider.getValue();
                previousTime = t;
                startTime = System.currentTimeMillis();
                inputReadRunning = true;
                runWithTimer();
            }

        }
    };

    /**
     * Action to stop reading.
     */
    private AbstractAction actionStopRun = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            inputReadRunning = false;
            stopButton.setEnabled(false);
            runButton.setEnabled(true);
        }
    };

    /**
     * Listener which sets the value of the time of the reading.
     */
    private ChangeListener actionSpeedChange = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            t = speedSlider.getValue();
        }
    };

    /**
     * Action to switch to step by step reading at PDAutomatons
     */
    private AbstractAction actionStepByStep = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            controller.setStepByStepRead(!controller.isStepByStepRead());

        }
    };

    /**
     * Action to open settings
     */
    private AbstractAction actionSettings = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            settingsDialog = new JDialog(MainFrame.this);
            settingsDialog.setTitle(msg("Settings"));
            settingsDialog.setVisible(true);
            JTabbedPane tabbedPane = new JTabbedPane();
            settingsDialog.setSize(new Dimension(350, 212));

            // system settings
            JPanel systemSettingsPanel = new JPanel();
            systemSettingsPanel.setLayout(null);
            JButton systemSaveButton = new JButton(msg("Save"));
            JLabel languageLabel = new JLabel(msg("LanguageLabel"));
            Vector<LanguageItem> languages = new Vector<LanguageItem>();
            LanguageItem english = new LanguageItem("en", "US", msg("English"));
            LanguageItem hungarian = new LanguageItem("hu", "HU", msg("Hungarian"));
            languages.addElement(english);
            languages.addElement(hungarian);
            languageChooser = new JComboBox<>(languages);
            languageChooser.setRenderer(new LanguageItemRenderer());
            // find given language
            LanguageItem selectedItem = null;
            switch (controller.getLanguage()) {
                case "en":
                    selectedItem = english;
                    break;
                case "hu":
                    selectedItem = hungarian;
                    break;
            }
            languageChooser.setSelectedItem(selectedItem);
            systemSaveButton.addActionListener(actionSystemMenuSave);

            systemSettingsPanel.add(languageLabel);
            systemSettingsPanel.add(languageChooser);
            systemSettingsPanel.add(systemSaveButton);
            languageLabel.setBounds(10, 35, 70, 25);
            languageChooser.setBounds(80, 35, 100, 25);
            systemSaveButton.setBounds(200, 110, 100, 25);

            // view settings
            JPanel viewSettingsPanel = new JPanel();
            viewSettingsPanel.setLayout(null);
            JLabel radiusSizeLabel = new JLabel(msg("RadiusSize"));
            Double[] radiuses = { 10.0, 10.5, 20.0, 20.5, 30.0 };
            JButton viewSaveButton = new JButton(msg("Save"));
            radiusSizeChooser = new JComboBox<Double>(radiuses);
            Double selectedRadius = controller.getRadiusOfStates();

            languageChooser.setSelectedItem(selectedRadius);

            viewSaveButton.addActionListener(actionViewMenuSave);

            viewSettingsPanel.add(radiusSizeLabel);
            viewSettingsPanel.add(radiusSizeChooser);
            viewSettingsPanel.add(viewSaveButton);
            radiusSizeLabel.setBounds(10, 35, 160, 25);
            radiusSizeChooser.setBounds(160, 35, 50, 25);
            viewSaveButton.setBounds(180, 110, 100, 25);

            tabbedPane.addTab(msg("SystemSettings"), systemSettingsPanel);
            tabbedPane.addTab(msg("ViewSettings"), viewSettingsPanel);
            settingsDialog.add(tabbedPane);

        }
    };

    /**
     * Save the changes on system.
     */
    private AbstractAction actionSystemMenuSave = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox comboBox = languageChooser;
            LanguageItem item = (LanguageItem) comboBox.getSelectedItem();
            int dialogResult = JOptionPane.showOptionDialog(settingsDialog, msg("ChangeLanguageQuestion"),
                    msg("ConfirmLanguageChange"), 0, JOptionPane.INFORMATION_MESSAGE, null, yesOrNoOption, null);
            if (dialogResult == JOptionPane.YES_OPTION) {
                settingsDialog.dispose();
                initialize(item.getLanguage(), item.getCountry());
            }

        }
    };

    private AbstractAction actionViewMenuSave = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox comboBox = radiusSizeChooser;
            Double item = (Double) comboBox.getSelectedItem();
            controller.setRadiusOfStates(item);
            getContentPane().repaint();

        }
    };

    /**
     * Opens the help window
     */
    private AbstractAction actionHelp = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                new HelpPanel(MainFrame.this);
            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(MainFrame.this, msg("UnexpectedError"), msg("ErrorLabel"),
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    };

    private AbstractAction actionUndo = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {

            controller.undo();
            makeWindow(controller.isDFA());
            setProjectTitle();

        }
    };

    private AbstractAction actionRedo = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {

            controller.redo();
            makeWindow(controller.isDFA());
            setProjectTitle();

        }
    };

    private WindowAdapter actionClose = new WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            String[] yes_no = { msg("Yes"), msg("Save") };
            if (modellingPhase && ( !controller.isSavedProject() || (controller.isSavedProject() && !controller.isLatestSave()))) {
                int n = JOptionPane.showOptionDialog(MainFrame.this, msg("QuitLabelSave"), msg("QuitMsgSave"), 0,
                        JOptionPane.INFORMATION_MESSAGE, null, yes_no, null);

                if (n == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    if (controller.isSavedProject()) {
                        try {
                            controller.save();
                            setProjectTitle();
                        } catch (FileNotFoundException e) {
                            JOptionPane.showMessageDialog(MainFrame.this, e.getMessage(), msg("ErrorLabel"),
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        try {
                            JFileChooser fileChooser = new JFileChooser();
                            FileNameExtensionFilter filter = new FileNameExtensionFilter("Automaton projects (.amproj)", "amproj");
                            fileChooser.setFileFilter(filter);
                            fileChooser.setSelectedFile(new File("untitled.amproj"));
                            fileChooser.setDialogTitle(msg("SaveAs"));
                            int returnValue = fileChooser.showOpenDialog(MainFrame.this);
                            if (returnValue == JFileChooser.APPROVE_OPTION) {
                                File filePath = fileChooser.getSelectedFile();
                                if (filePath.exists()) {
                                    int m = JOptionPane.showConfirmDialog(MainFrame.this, msg("OverwriteMsg"),
                                            msg("OverWriteLabel"), JOptionPane.YES_NO_OPTION);
            
                                    if (m == JOptionPane.YES_OPTION) {
                                        MainFrame.this.controller.saveAs(filePath.getAbsolutePath());
                                    }
                                } else {
                                    MainFrame.this.controller.saveAs(filePath.getAbsolutePath());
                                }
                            }
                            setProjectTitle();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), msg("ErrorLabel"),
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                int t = JOptionPane.showOptionDialog(MainFrame.this, msg("QuitLabel"), msg("QuitMsg"), 0,
                JOptionPane.INFORMATION_MESSAGE, null, yesOrNoOption, null);
                if (t == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } 
            }
        }
    };

    
    
}
