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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

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
    JSlider speedSlider ;
    long startTime ;
    boolean endOfReading = false;
    boolean inputReadRunning;
    int t;

    public TempFrame() {

        // test start
        controller = new AutomatonController();

        // basic settings

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("AutomatonModeller");

        // menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem menuItemAddNewDFA = new JMenuItem("New DFA");
        JMenuItem menuItemAddNewPDA = new JMenuItem("New PDA");
        JMenuItem menuItemOpenNewDFA = new JMenuItem("Open DFA");
        JMenuItem menuItemOpenNewPDA = new JMenuItem("Open PDA");
        JMenuItem menuItemSave = new JMenuItem("Save");
        JMenuItem menuItemSaveAs = new JMenuItem("Save As");

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
        JLabel welcomeLabel = new JLabel("Welcome");

        this.getContentPane().add(welcomeLabel);
        transitionMaking = false;

        // view panel

        setVisible(true);

    }

    private void makeWindow(boolean isDFA) {

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().removeAll();

        // making view panel
        viewPanel = controller.getView();// new JPanel();
        // making popup menu

        // popup menu for clicking an empty space
        emptySpacePopupMenu = new JPopupMenu();
        JMenuItem simpleStateAdder = new JMenuItem("Add simple state");
        JMenuItem startStateAdder = new JMenuItem("Add start state");
        JMenuItem acceptStateAdder = new JMenuItem("Add accept state");

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
        JMenuItem transitionAdder = new JMenuItem("Add transition");
        JMenuItem acceptStateSetter = new JMenuItem("Set to accept state");
        JMenuItem startStateSetter = new JMenuItem("Set to start state");

        // adding actions
        acceptStateSetter.addActionListener(actionSetToAcceptState);

        transitionAdder.addActionListener(actionMakeTransition);

        // adding to menu
        statePopupMenu.add(transitionAdder);
        statePopupMenu.add(acceptStateSetter);
        statePopupMenu.add(startStateSetter);

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
                    // check needed

                    if (controller.isDFA()) {
                        String name = JOptionPane.showInputDialog("Transition:");
                        char with = name.charAt(0);
                        try {
                            controller.makeDFATransition(clickedState, with, stateNear);
                        } catch (KeyFromStateAlreadyExistsException e) {
                            JOptionPane.showMessageDialog(TempFrame.this, e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                           
                        }
                        getContentPane().repaint();
                    } else {
                        // addig transition
                        String name = JOptionPane.showInputDialog("Transition:");
                        String[] items = name.split("->");
                        String[] value = items[0].split("/");
                        char with = value[0].charAt(0);
                        char stackItem = value[1].charAt(0);
                        String stackString = items[1];
                        try {
                            controller.makePDATransition(clickedState, with, stackItem, stateNear, stackString);
                        } catch (KeyFromStateAlreadyExistsException e) {
                            JOptionPane.showMessageDialog(TempFrame.this, e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                            
                        }
                        getContentPane().repaint();
                    }
                    
                    transitionMaking = false;
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
        //controllingPanel.add(controllingLabel);
        controllingPanel.setPreferredSize(new Dimension(300, 400));
        //JPanel runPanel = new JPanel(null);
        //  buttons
        JButton nextButton = new JButton("Next");
        JButton runButton = new JButton("Run");
        JButton stopButton = new JButton("Stop");
        wordLabel = new JLabel();
        JButton wordAdderButton = new JButton("Add new word");
        wordLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        speedSlider = new JSlider(JSlider.HORIZONTAL,1, 10, 1);
        JLabel speedLabel = new JLabel("Speed");

      
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTrack(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        //  actions
        wordAdderButton.addActionListener(actionAddWord);
        nextButton.addActionListener(actionRead);
        runButton.addActionListener(actionRun);
        stopButton.addActionListener(actionStopRun);
        

        // adding to panel
        controllingPanel.add(wordLabel);
        controllingPanel.add(nextButton);
        controllingPanel.add(runButton);
        controllingPanel.add(speedLabel);
        controllingPanel.add(speedSlider);
        controllingPanel.add(wordAdderButton);
        controllingPanel.add(stopButton);

        speedSlider.setBounds (10, 390, 275, 50);
        wordLabel.setBounds (10, 235, 280, 45);
        speedLabel.setBounds (15, 355, 100, 25);
        nextButton.setBounds (185, 465, 75, 25);
        runButton.setBounds (35, 465, 80, 25);
        wordAdderButton.setBounds (25, 300, 120, 25);
        stopButton.setBounds (35, 500, 80, 25);
       
 
      

        
        
    
        this.getContentPane().add(viewPanel);
        this.getContentPane().add(controllingPanel,BorderLayout.EAST);

        this.repaint();
        this.revalidate();
    }

    private synchronized boolean checkAndMark() {
        if (moveIsRunning)
            return false;
        moveIsRunning = true;
        return true;
    }

    private void initThread(State state) {
        if (checkAndMark()) {
            new Thread() {
                public void run() {
                    do {
                       
                        try {
                            controller.changePosition(state, MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());
                            getContentPane().repaint();
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

    
    

    

    private AbstractAction actionOpenNewDFAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {

            JDialog dialog = new JDialog(TempFrame.this);
            dialog.setLocationRelativeTo(TempFrame.this); // set location width/2 and height/2
            dialog.setTitle("New DFA");
            dialog.setSize(300, 150);

            JPanel panel = new JPanel();
            // panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

            // radio panel
            JPanel radioPanel = new JPanel();
            JRadioButton emptyAutomaton = new JRadioButton("Make empty Automaton");
            JRadioButton regexpAutomaton = new JRadioButton("Make Automaton by regular expression");
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
            Icon fineIcon = new ImageIcon(
                    new ImageIcon("res/regexp_fine.png").getImage().getScaledInstance(21, 21, Image.SCALE_DEFAULT));
            Icon badIcon = new ImageIcon(
                    new ImageIcon("res/regexp_bad.png").getImage().getScaledInstance(21, 21, Image.SCALE_DEFAULT));

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
            JButton generateButton = new JButton("Generate");
            generateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (DFAsimple) {
                        controller.addNewDFAutomaton();
                        dialog.dispose();
                        System.out.println(controller.getAutomaton());

                        TempFrame.this.makeWindow(true);

                    } else if (regexpCorrect) {
                        // majd
                    } else {
                        JOptionPane.showMessageDialog(TempFrame.this, "Regular expression is not correct!", "Error",
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

    private AbstractAction actionOpenNewPDAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog dialog = new JDialog(TempFrame.this);
            dialog.setLocationRelativeTo(TempFrame.this); // set location width/2 and height/2
            dialog.setTitle("New PDA");
            dialog.setSize(200, 100);

            JPanel mainPanel = new JPanel();

            // start symbol
            JPanel startSymbolPanel = new JPanel(new BorderLayout(8, 8));

            JLabel startSymbolLabel = new JLabel("Start symbol:", JLabel.TRAILING);
            startSymbolPanel.add(startSymbolLabel, BorderLayout.WEST);
            JTextField startSymbolText = new JTextField(2);
            startSymbolText.setHorizontalAlignment(JTextField.CENTER);
            startSymbolLabel.setLabelFor(startSymbolText);
            startSymbolPanel.add(startSymbolText, BorderLayout.EAST);

            // generate button
            JButton generateButton = new JButton("Generate");
            generateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (startSymbolText.getText().length() == 1) {
                        char startSymbol = startSymbolText.getText().charAt(0);
                        controller.addNewPDAutomaton(startSymbol);
                        dialog.dispose();
                        System.out.println(controller.getAutomaton());

                        TempFrame.this.makeWindow(false);

                    } else {
                        JOptionPane.showMessageDialog(TempFrame.this, "Start symbol should be one character long!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            mainPanel.add(startSymbolPanel);
            mainPanel.add(generateButton);
            dialog.add(mainPanel);

            dialog.setVisible(true);

        }
    };

    private AbstractAction actionOpenMakeDFAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Automaton projects (.amproj)", "amproj");
                fileChooser.setFileFilter(filter);
                fileChooser.setDialogTitle("Open DFA");
                int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    controller.addNewDFAutomaton(filePath);
                    System.out.println(controller.getAutomaton());

                    TempFrame.this.makeWindow(true);
                    /*
                     * TempFrame.this.getContentPane().setLayout(new GridLayout(0,2));
                     * TempFrame.this.getContentPane().removeAll(); viewPanel =
                     * controller.getView(); controllingPanel = new JPanel(); JLabel
                     * controllingLabel = new JLabel("Controlling here:");
                     * controllingPanel.add(controllingLabel);
                     * TempFrame.this.getContentPane().add(viewPanel);
                     * TempFrame.this.getContentPane().add(controllingPanel);
                     * TempFrame.this.repaint(); TempFrame.this.revalidate();
                     */

                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    private AbstractAction actionOpenMakePDAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Automaton projects (.amproj)", "amproj");
                fileChooser.setFileFilter(filter);
                fileChooser.setDialogTitle("Open PDA");
                int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    controller.addNewPDAutomaton(filePath);
                    System.out.println(controller.getAutomaton());

                    TempFrame.this.makeWindow(false);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

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
                    fileChooser.setDialogTitle("Save As");
                    int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File filePath = fileChooser.getSelectedFile();
                        if (filePath.exists()) {
                            int n = JOptionPane.showConfirmDialog(TempFrame.this, "Do You Want to Overwrite File?",
                                    "Confirm Overwrite", JOptionPane.YES_NO_OPTION);

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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    private AbstractAction actionSaveAs = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Automaton projects (.amproj)", "amproj");
                fileChooser.setFileFilter(filter);
                fileChooser.setSelectedFile(new File("untitled.amproj"));
                fileChooser.setDialogTitle("Save As");
                int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File filePath = fileChooser.getSelectedFile();
                    if (filePath.exists()) {
                        int n = JOptionPane.showConfirmDialog(TempFrame.this, "Do You Want to Overwrite File?",
                                "Confirm Overwrite", JOptionPane.YES_NO_OPTION);

                        if (n == JOptionPane.YES_OPTION) {
                            TempFrame.this.controller.saveAs(filePath.getAbsolutePath());
                        }
                    } else {
                        TempFrame.this.controller.saveAs(filePath.getAbsolutePath());
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    };

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
                    //draw 
                } catch (StateAlreadyExistsException e1) {
                    JOptionPane.showMessageDialog(TempFrame.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }  
            }
        }
    };

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
                    //draw 
                } catch (StateAlreadyExistsException e1) {
                    JOptionPane.showMessageDialog(TempFrame.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }  
            }
        }
    };

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
                    //draw 
                } catch (StateAlreadyExistsException e1) {
                    JOptionPane.showMessageDialog(TempFrame.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }  catch (StartStateAlreadyExistsException e2) {
                    JOptionPane.showMessageDialog(TempFrame.this, e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(TempFrame.this, "States can't interect each other!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    
    private AbstractAction actionSetToAcceptState = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            clickedState.setAccepState(true);
            
            getContentPane().repaint();
  
        }
    };

    private AbstractAction actionMakeTransition = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) { 
            transitionMaking = true;
        }
    };

    private AbstractAction actionAddWord = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) { 
            String input = JOptionPane.showInputDialog("Input word:");
            controller.addWordToRead(input);
            wordLabel.setText(controller.getColoredInputWord());
            controller.reset();
        }
    };

    private AbstractAction actionRead = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) { 
            
            try {
                controller.nextStepInReading();
                System.out.println(controller.getAutomaton().getCurrentState());
                if(controller.isCurrentStateRejectState()) {
                    JOptionPane.showMessageDialog(TempFrame.this, "Reject state! Word is not accepted", "Error", JOptionPane.ERROR_MESSAGE);
                    
                    controller.reset();
                    wordLabel.setText(controller.getColoredInputWord());
                } 
                wordLabel.setText(controller.getColoredInputWord());
                getContentPane().repaint();
                if( controller.isLastLetter() ) {
                    if(controller.isCurrentStateAcceptState()) {
                        JOptionPane.showMessageDialog(TempFrame.this, "Given word is accepted", "Accepted", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(TempFrame.this, "Given word is not accepted", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                    controller.reset();
                    wordLabel.setText(controller.getColoredInputWord());
                }
                getContentPane().repaint();
               

            } catch (MissingStartStateException e1) {

                e1.printStackTrace();
            }

        }
    };

    private AbstractAction actionRun = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) { 
            t = speedSlider.getValue();
            startTime = System.currentTimeMillis();
            inputReadRunning = true;
            
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                   
                      
                    try{
                        if(!inputReadRunning) {
                            timer.cancel();
                            timer.purge();
                        } else {
                            controller.nextStepInReading();
                            System.out.println(controller.getAutomaton().getCurrentState());
                            if(controller.isCurrentStateRejectState()) {
                                JOptionPane.showMessageDialog(TempFrame.this, "Reject state! Word is not accepted", "Error", JOptionPane.ERROR_MESSAGE);
                                
                                controller.reset();
                                wordLabel.setText(controller.getColoredInputWord());
                                inputReadRunning = false;
                                
                            } 
                            wordLabel.setText(controller.getColoredInputWord());
                            getContentPane().repaint();
                            if( controller.isLastLetter() ) {
                                if(controller.isCurrentStateAcceptState()) {
                                    JOptionPane.showMessageDialog(TempFrame.this, "Given word is accepted", "Accepted", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(TempFrame.this, "Given word is not accepted", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                                inputReadRunning = false;
                                
                                controller.reset();
                                wordLabel.setText(controller.getColoredInputWord());
                                
                                
                            }
                                getContentPane().repaint();
                            
    
                        }
                      
                    }catch(Exception e1) {

                    }
                    
                    }
            }, 0, t*1000); 
            
           
        }
    };

    private AbstractAction actionStopRun = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) { 
            inputReadRunning = false;
        }
    };
    
   

    

}

