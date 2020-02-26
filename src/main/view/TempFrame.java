package main.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

        menuItemAddNewDFA.addActionListener(actionOpenNewDFAWindow);
        menuItemAddNewPDA.addActionListener(actionOpenNewPDAWindow);

        menuFile.add(menuItemAddNewDFA);
        menuFile.add(menuItemAddNewPDA);
        menuFile.addSeparator();
        menuFile.add(menuItemOpenNewDFA);
        menuFile.add(menuItemOpenNewPDA);

        menuBar.add(menuFile);
        setJMenuBar(menuBar);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setVisible(true);

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
            JPanel startSymbolPanel = new JPanel( new BorderLayout(8,8));

            JLabel startSymbolLabel = new JLabel("Start symbol:", JLabel.TRAILING);
            startSymbolPanel.add(startSymbolLabel,BorderLayout.WEST);
            JTextField startSymbolText = new JTextField(2);
            startSymbolText.setHorizontalAlignment(JTextField.CENTER);
            startSymbolLabel.setLabelFor(startSymbolText);
            startSymbolPanel.add(startSymbolText,BorderLayout.EAST);

           


            //  generate button
            JButton generateButton = new JButton("Generate");
            generateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if(startSymbolText.getText().length() == 1) {
                        char startSymbol = startSymbolText.getText().charAt(0);
                        controller.addNewPDAutomaton(startSymbol);
                        dialog.dispose();
                        System.out.println(controller.getAutomaton());
                    } else {
                        JOptionPane.showMessageDialog(TempFrame.this, "Start symbol should be one character long!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            mainPanel.add(startSymbolPanel);
            mainPanel.add(generateButton);
            dialog.add(mainPanel);
            
            dialog.setVisible(true);




        }
    } ;

}