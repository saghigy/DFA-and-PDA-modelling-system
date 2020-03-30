package main.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.MouseInfo;
import java.awt.color.*;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
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
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.Languages;
import main.controller.AutomatonController;
import main.model.PDAutomaton;
import main.model.State;
import main.model.exceptions.KeyFromStateAlreadyExistsException;
import main.model.exceptions.MissingStartStateException;
import main.model.exceptions.StartStateAlreadyExistsException;
import main.model.exceptions.StateAlreadyExistsException;
import main.model.exceptions.StateNotFoundException;

/**
 * TempFrame
 */
public class TempFrame extends JFrame {

    private AutomatonController controller;

    private boolean regexpCorrect;
    private boolean DFAsimple;
    private JPanel viewPanel;
    private JPanel controllingPanel;
    private JPanel mainPanel;
    private double clickPositionX;
    private double clickPositionY;
    private JPopupMenu emptySpacePopupMenu;
    private JPopupMenu statePopupMenu;
    private State clickedState;
    private State movableState;
    private boolean transitionMaking;
    volatile private boolean mouseDown = false;
    volatile private boolean moveIsRunning = false;
    JLabel wordLabel;
    JSlider speedSlider;
    long startTime;
    boolean endOfReading = false;
    boolean inputReadRunning;
    int t; // current speed ofrun
    int previousTime; // previous speed of run
    JLabel stackLabel;
    JButton stopButton;
    JButton runButton;
    JButton nextButton;

    public TempFrame() {

        // test start
        controller = new AutomatonController();

        // basic settings

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("AutomatonModeller");

        // menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu(msg("File"));
        JMenuItem menuItemAddNewDFA = new JMenuItem(msg("NewDFA"));
        JMenuItem menuItemAddNewPDA = new JMenuItem(msg("NewPDA"));
        JMenuItem menuItemOpenNewDFA = new JMenuItem(msg("OpenDFA"));
        JMenuItem menuItemOpenNewPDA = new JMenuItem(msg("OpenPDA"));
        JMenuItem menuItemSave = new JMenuItem(msg("Save"));
        JMenuItem menuItemSaveAs = new JMenuItem(msg("SaveAs"));

        // actions
        menuItemAddNewDFA.addActionListener(actionOpenNewDFAWindow);
        menuItemAddNewPDA.addActionListener(actionOpenNewPDAWindow);
        menuItemOpenNewDFA.addActionListener(actionOpenMakeDFAWindow);
        menuItemOpenNewPDA.addActionListener(actionOpenMakePDAWindow);
        menuItemSave.addActionListener(actionSave);
        menuItemSaveAs.addActionListener(actionSaveAs);

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
        menuItemSaveAs
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));

        menuFile.add(menuItemAddNewDFA);
        menuFile.add(menuItemAddNewPDA);
        menuFile.addSeparator();
        menuFile.add(menuItemOpenNewDFA);
        menuFile.add(menuItemOpenNewPDA);
        menuFile.addSeparator();
        menuFile.add(menuItemSave);
        menuFile.add(menuItemSaveAs);

        menuBar.add(menuFile);
        setJMenuBar(menuBar);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // main panel
        JLabel welcomeLabel = new JLabel(msg("Welcome"));

        this.getContentPane().add(welcomeLabel);
        transitionMaking = false;

        // view panel

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

        // popup menu for clicking on a state
        statePopupMenu = new JPopupMenu();
        JMenuItem transitionAdder = new JMenuItem(msg("AddTransition"));
        JMenuItem acceptStateSetter = new JMenuItem(msg("SetToAcceptState"));
        JMenuItem startStateSetter = new JMenuItem(msg("SetToStartState"));
        JMenuItem stateDeleter = new JMenuItem(msg("DeleteState"));

        // adding actions
        acceptStateSetter.addActionListener(actionSetToAcceptState);

        transitionAdder.addActionListener(actionMakeTransition);
        stateDeleter.addActionListener(actionDeleteState);

        // adding to menu
        statePopupMenu.add(transitionAdder);
        statePopupMenu.add(acceptStateSetter);
        statePopupMenu.add(startStateSetter);
        statePopupMenu.add(stateDeleter);

        viewPanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent me) {
                int x = me.getX();
                int y = me.getY();
                TempFrame.this.clickPositionX = x;
                TempFrame.this.clickPositionY = y;

                State stateNear = controller.stateNear(x, y);
                if (!transitionMaking) {
                    if (SwingUtilities.isRightMouseButton(me)) {
                        // no state in clicked area
                        if (stateNear == null) {
                            emptySpacePopupMenu.show(me.getComponent(), x, y);
                        } else {
                            clickedState = stateNear;
                            statePopupMenu.show(me.getComponent(), x, y);
                        }
                    }
                } else {
                    if (SwingUtilities.isLeftMouseButton(me)) {
                        if (stateNear == null) {
                            JOptionPane.showMessageDialog(TempFrame.this, msg("NoStateNear"), msg("ErrorLabel"),
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            try {
                                
                                
                                if (controller.isDFA()) {
                                    //  making dialog for transition
                                    String name;
                                    do{
                                        name = JOptionPane.showInputDialog(msg("LetterForTransition"));
                                        if(name != null ) {
                                            if(name.length() == 1) {
                                                
                                                char with = name.charAt(0);
                                                controller.makeDFATransition(clickedState, with, stateNear);
                                            } else {
                                                JOptionPane.showMessageDialog(TempFrame.this, msg("TransitionNotGoodFormat"), msg("ErrorLabel"),
                                                JOptionPane.ERROR_MESSAGE);
                                            }
                                           
                                        }
                                    }while(name != null && name.length() != 1);
                                   
                                } else {
                                    // addig transition
                                    String name;
                                    do{
                                        name = JOptionPane.showInputDialog(msg("PDATransition"));
                                        if(name != null) {
                                            name = name.replace(" ", "");
                                            if(name.matches("./.->.*")) {
                                                String[] items = name.split("->");
                                                String[] value = items[0].split("/");
                                                char with = value[0].charAt(0);
                                                char stackItem = value[1].charAt(0);
                                                String stackString = "";
                                                if (items.length == 2) {
                                                    stackString = items[1];
                                                }
                                                controller.makePDATransition(clickedState, with, stackItem, stateNear, stackString);
                                            } else {
                                                JOptionPane.showMessageDialog(TempFrame.this, msg("TransitionNotGoodFormat"), msg("ErrorLabel"),
                                                JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    }while(name != null && !name.matches("./.->.*")  );
                                }
                                transitionMaking = false;
                                setControllingPanelEnabled(true);
                                getContentPane().repaint();
                                setProjectTitle();
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(TempFrame.this, e.getMessage(), msg("ErrorLabel"),
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
                TempFrame.this.clickPositionX = x;
                TempFrame.this.clickPositionY = y;
                State stateNear = controller.stateNear(x, y);
                if (SwingUtilities.isLeftMouseButton(me) && stateNear != null && !transitionMaking) {
                    mouseDown = true;
                    initThread(stateNear);
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
                // TODO Auto-generated method stub

            }

        });

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
        nextButton.setBounds(185, 365, 75, 25);
        runButton.setBounds(35, 365, 80, 25);
        wordAdderButton.setBounds(25, 200, 120, 25);
        stopButton.setBounds(35, 400, 80, 25);

        // stack settings
        if (!controller.isDFA()) {
            stackLabel = new JLabel();
            drawStack();
            stackLabel.setBorder(BorderFactory.createLineBorder(Color.black));
            controllingPanel.add(stackLabel);
            stackLabel.setBounds(100, 550, 100, 200);
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
    private void initThread(State state) {
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
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } while (mouseDown);
                    moveIsRunning = false;
                }
            }.start();
        }
    }

    private void setControllingPanelEnabled(boolean enable) {
        for (Component com : controllingPanel.getComponents()) {
            if(com != nextButton && com != runButton && com != stopButton && com != speedSlider )
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

        if (!controller.isDFA())
            drawStack();

        if (controller.isCurrentStateRejectState()) {
            JOptionPane.showMessageDialog(TempFrame.this, msg("RejectStateMsg"), msg("ErrorLabel"),
                    JOptionPane.ERROR_MESSAGE);

            controller.reset();
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
            inputReadRunning = false;
            wordLabel.setText(controller.getColoredInputWord());
            if (!controller.isDFA())
                drawStack();
        }
        wordLabel.setText(controller.getColoredInputWord());
        getContentPane().repaint();
        if (controller.isLastLetter()) {
            if (controller.isCurrentStateAcceptState()) {
                JOptionPane.showMessageDialog(TempFrame.this, msg("AcceptedMsg"), msg("AcceptedLabel"),
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(TempFrame.this, msg("NotAcceptedMsg"), msg("ErrorLabel"),
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
     * Action which opens a window where user can make a new DFAutomaton. An empty
     * one or on from a regular expression.
     */
    private AbstractAction actionOpenNewDFAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {

            JDialog dialog = new JDialog(TempFrame.this);
            dialog.setLocationRelativeTo(TempFrame.this); // set location width/2 and height/2
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
                    if (exc != null) {
                        checkLabel.setIcon(badIcon);
                        regexpCorrect = false;

                    } else {
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
                    if (DFAsimple) {
                        controller.addNewDFAutomaton();
                        dialog.dispose();

                        TempFrame.this.makeWindow(true);
                        setProjectTitle();
                    } else if (regexpCorrect) {
                        // majd
                    } else {
                        JOptionPane.showMessageDialog(TempFrame.this, msg("RegexIsNotCorrect"), msg("ErrorLabel"),
                                JOptionPane.ERROR_MESSAGE);
                    }

                }
            });

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
            JDialog dialog = new JDialog(TempFrame.this);
            dialog.setLocationRelativeTo(TempFrame.this); // set location width/2 and height/2
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

                        TempFrame.this.makeWindow(false);
                        setProjectTitle();
                    } else {
                        JOptionPane.showMessageDialog(TempFrame.this, msg("StartSymbolNotOneCharLong"),
                                msg("ErrorLabel"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

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
                int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    controller.addNewDFAutomaton(filePath);

                    TempFrame.this.makeWindow(true);
                    setProjectTitle();

                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(), msg("ErrorLabel"),
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
                int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    controller.addNewPDAutomaton(filePath);

                    TempFrame.this.makeWindow(false);
                    setProjectTitle();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(), msg("ErrorLabel"),
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
                    int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File filePath = fileChooser.getSelectedFile();
                        if (filePath.exists()) {
                            int n = JOptionPane.showConfirmDialog(TempFrame.this, msg("OverwriteMsg"),
                                    msg("OverWriteLabel"), JOptionPane.YES_NO_OPTION);

                            if (n == JOptionPane.YES_OPTION) {
                                TempFrame.this.controller.saveAs(filePath.getAbsolutePath());
                            }
                        } else {
                            TempFrame.this.controller.saveAs(filePath.getAbsolutePath());
                        }
                    }
                } else {

                    if (!TempFrame.this.controller.isLatestSave()) {
                        TempFrame.this.controller.save();
                    }

                }
                setProjectTitle();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(), msg("ErrorLabel"),
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
                int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File filePath = fileChooser.getSelectedFile();
                    if (filePath.exists()) {
                        int n = JOptionPane.showConfirmDialog(TempFrame.this, msg("OverwriteMsg"),
                                msg("OverWriteLabel"), JOptionPane.YES_NO_OPTION);

                        if (n == JOptionPane.YES_OPTION) {
                            TempFrame.this.controller.saveAs(filePath.getAbsolutePath());
                        }
                    } else {
                        TempFrame.this.controller.saveAs(filePath.getAbsolutePath());
                    }
                }
                setProjectTitle();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(), msg("ErrorLabel"),
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
            double x = TempFrame.this.clickPositionX;
            double y = TempFrame.this.clickPositionY;
            if (controller.canMakeState(x, y)) {
                // add state
                try {
                    // TODO: adding state
                    // draw tmp circle->get name
                    String name = JOptionPane.showInputDialog("Name:");
                    controller.addState(name, x, y);
                    getContentPane().repaint();
                    // draw
                } catch (StateAlreadyExistsException e1) {
                    JOptionPane.showMessageDialog(TempFrame.this, e1.getMessage(), msg("ErrorLabel"),
                            JOptionPane.ERROR_MESSAGE);
                }
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
            double x = TempFrame.this.clickPositionX;
            double y = TempFrame.this.clickPositionY;
            if (controller.canMakeState(x, y)) {
                // add state
                try {
                    // TODO: adding state
                    // draw tmp circle->get name
                    String name = JOptionPane.showInputDialog("Name:");
                    controller.addAcceptState(name, x, y);
                    getContentPane().repaint();
                    // draw
                } catch (StateAlreadyExistsException e1) {
                    JOptionPane.showMessageDialog(TempFrame.this, e1.getMessage(), msg("ErrorLabel"),
                            JOptionPane.ERROR_MESSAGE);
                }
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
            double x = TempFrame.this.clickPositionX;
            double y = TempFrame.this.clickPositionY;
            if (controller.canMakeState(x, y)) {
                // add state
                try {
                    // TODO: adding state
                    // draw tmp circle->get name
                    String name = JOptionPane.showInputDialog("Name:");
                    controller.addStartState(name, x, y);
                    getContentPane().repaint();
                    // draw
                } catch (StateAlreadyExistsException e1) {
                    JOptionPane.showMessageDialog(TempFrame.this, e1.getMessage(), msg("ErrorLabel"),
                            JOptionPane.ERROR_MESSAGE);
                } catch (StartStateAlreadyExistsException e2) {
                    JOptionPane.showMessageDialog(TempFrame.this, e2.getMessage(), msg("ErrorLabel"),
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(TempFrame.this, msg("NoItersectMsg"), msg("ErrorLabel"),
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
            nextButton.setEnabled(true);
            runButton.setEnabled(true);
            speedSlider.setEnabled(true);
            controller.addWordToRead(input);
            wordLabel.setText(controller.getColoredInputWord());
            controller.reset();
            

            
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

                JOptionPane.showMessageDialog(TempFrame.this, e1.getMessage(), msg("ErrorLabel"), JOptionPane.ERROR_MESSAGE);
            }
            

        }
    };

    /**
     * Action to run a reading of the input word. It's speed is setable from the speed slider.
     */
    private AbstractAction actionRun = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) { 
            if(controller.getAutomaton().getCurrentState() != null){
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
    private ChangeListener actionSpeedChange = new ChangeListener(){
    
        @Override
        public void stateChanged(ChangeEvent e) {
            t = speedSlider.getValue();
        }
    }; 

     
    
   

    

}

