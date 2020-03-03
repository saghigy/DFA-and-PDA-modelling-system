package main.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.Toolkit;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
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
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.controller.AutomatonController;
import main.model.PDAutomaton;
import main.model.State;
import main.model.exceptions.MissingStartStateException;

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

        menuItemAddNewDFA.addActionListener(actionOpenNewDFAWindow);
        menuItemAddNewPDA.addActionListener(actionOpenNewPDAWindow);
        menuItemOpenNewDFA.addActionListener(actionOpenMakeDFAWindow);
        menuItemOpenNewPDA.addActionListener(actionOpenMakePDAWindow);
        menuItemSave.addActionListener(actionSave);
        menuItemSaveAs.addActionListener(actionSaveAs);

        menuItemAddNewDFA.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        menuItemAddNewPDA.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        menuItemOpenNewDFA.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        menuItemOpenNewPDA.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        menuItemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK+ ActionEvent.SHIFT_MASK));

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

        //main panel
        JLabel welcomeLabel = new JLabel("Welcome");
        
        
        this.getContentPane().add(welcomeLabel);

        //  view panel
        
        
        
        setVisible(true);

    }

    private void makeDFAWindow(){
        this.getContentPane().setLayout(new GridLayout(0,2));
        this.getContentPane().removeAll();
        viewPanel = controller.getView();
        controllingPanel = new JPanel();
        JLabel controllingLabel = new JLabel("Controlling here:");
        controllingPanel.add(controllingLabel);
        this.getContentPane().add(viewPanel);
        this.getContentPane().add(controllingPanel);
        this.repaint();
        this.revalidate();
    }

    private void makePDAWindow(){ 
        this.getContentPane().setLayout(new GridLayout(0,2));
        this.getContentPane().removeAll();
        viewPanel = controller.getView();
        controllingPanel = new JPanel();
        JLabel controllingLabel = new JLabel("Controlling here:");
        controllingPanel.add(controllingLabel);
        this.getContentPane().add(viewPanel);
        this.getContentPane().add(controllingPanel);
        this.repaint();
        this.revalidate();
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

                        TempFrame.this.makeDFAWindow();

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

                        TempFrame.this.makePDAWindow();

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

    private  AbstractAction actionOpenMakeDFAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter( "Automaton projects (.amproj)", "amproj");
                fileChooser.setFileFilter(filter);
                fileChooser.setDialogTitle("Open DFA");
                int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION){
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    controller.addNewDFAutomaton(filePath);
                    System.out.println(controller.getAutomaton());

                    TempFrame.this.makeDFAWindow();
                    /*
                    TempFrame.this.getContentPane().setLayout(new GridLayout(0,2));
                    TempFrame.this.getContentPane().removeAll();
                    viewPanel = controller.getView();
                    controllingPanel = new JPanel();
                    JLabel controllingLabel = new JLabel("Controlling here:");
                    controllingPanel.add(controllingLabel);
                    TempFrame.this.getContentPane().add(viewPanel);
                    TempFrame.this.getContentPane().add(controllingPanel);
                    TempFrame.this.repaint();
                    TempFrame.this.revalidate();
                    */
  
                    
                }
            }catch(Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    private  AbstractAction actionOpenMakePDAWindow = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter( "Automaton projects (.amproj)", "amproj");
                fileChooser.setFileFilter(filter);
                fileChooser.setDialogTitle("Open PDA");
                int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION){
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    controller.addNewPDAutomaton(filePath);
                    System.out.println(controller.getAutomaton());

                    TempFrame.this.makePDAWindow();
                }
            }catch(Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    private  AbstractAction actionSave = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) { 
            try{
                if (!controller.isSavedProject()) {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter( "Automaton projects (.amproj)", "amproj");
                    fileChooser.setFileFilter(filter);
                    fileChooser.setSelectedFile(new File("untitled.amproj"));
                    fileChooser.setDialogTitle("Save As");
                    int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                    if (returnValue == JFileChooser.APPROVE_OPTION){
                        File filePath = fileChooser.getSelectedFile();
                        if( filePath.exists()) {
                            int n = JOptionPane.showConfirmDialog(
                            TempFrame.this,
                            "Do You Want to Overwrite File?",
                            "Confirm Overwrite",
                            JOptionPane.YES_NO_OPTION);

                            if (n == JOptionPane.YES_OPTION) {
                                TempFrame.this.controller.saveAs(filePath.getAbsolutePath());
                            }
                        }else {
                            TempFrame.this.controller.saveAs(filePath.getAbsolutePath());
                        }
                    }
                } else {
                    if(TempFrame.this.controller.getLatestSave()) {
                        TempFrame.this.controller.save();
                    }

                }
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    private  AbstractAction actionSaveAs = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) { 
            try {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter( "Automaton projects (.amproj)", "amproj");
                fileChooser.setFileFilter(filter);
                fileChooser.setSelectedFile(new File("untitled.amproj"));
                fileChooser.setDialogTitle("Save As");
                int returnValue = fileChooser.showOpenDialog(TempFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION){
                    File filePath = fileChooser.getSelectedFile();
                    if( filePath.exists()) {
                        int n = JOptionPane.showConfirmDialog(
                        TempFrame.this,
                        "Do You Want to Overwrite File?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION);

                        if (n == JOptionPane.YES_OPTION) {
                            TempFrame.this.controller.saveAs(filePath.getAbsolutePath());
                        }
                    } else {
                        TempFrame.this.controller.saveAs(filePath.getAbsolutePath());
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TempFrame.this, ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            
        }
    };

    

}

