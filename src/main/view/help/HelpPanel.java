package main.view.help;

import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

import main.Languages;

public class HelpPanel extends JDialog {
    
    private static final long serialVersionUID = 5477619663529907162L;
    private Dimension windowSize;

    public HelpPanel(JFrame parent) throws IOException {
        super(parent, Languages.msg("Help"), true);
        windowSize = new Dimension(1350,900);
        createUI(this);
        this.setSize(windowSize);      
        this.setLocationRelativeTo(null);  
        this.setVisible(true);
        /*
        FlowLayout layout = new FlowLayout();
        mainPanel = new JPanel();
        mainPanel.setLayout(layout);
        help = new JEditorPane();

        File f;
        if (Languages.language.equals("en")) {
            f = new File("resources/help/en/index.html");
        } else {
            f = new File("resources/help/hu/index.html");
        }

        help.setPage(f.toURI().toURL());
        help.setContentType("text/html");
        help.setEditable(false);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.add(help);
        scrollPane.add(new JButton("asd"));
        mainPanel.add(scrollPane);
        setResizable(false);
        setSize(960, 600);
        mainPanel.setSize(960, 600);
        mainPanel.setVisible(true);
        setContentPane(mainPanel);
        setVisible(true);
        */

    }

    private void createUI(JDialog frame) throws  IOException {
        JPanel panel = new JPanel();
        FlowLayout layout = new FlowLayout();
        panel.setLayout(layout);       
  
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);   
        jEditorPane.setContentType("text/html;charset=UTF-8");
        File f;
        if (Languages.language.equals("en")) {
            f = new File("resources/help/en/index.html");
        } else {
            f = new File("resources/help/hu/index.html");
        }
        
        jEditorPane.setPage(f.toURI().toURL());
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
                        String reference = e.getDescription();
                        if (reference != null && reference.startsWith("#")) { // link must start with # to be internal reference
                            reference = reference.substring(1);
                            System.out.println(reference);
                            jEditorPane.scrollToReference(reference);
                        }
                    }
                }
            }
        });

        
  
        JScrollPane jScrollPane = new JScrollPane(jEditorPane);
        jScrollPane.setPreferredSize(windowSize);      
  
        panel.add(jScrollPane);
        frame.getContentPane().add(jScrollPane, BorderLayout.CENTER);    
     }  

}