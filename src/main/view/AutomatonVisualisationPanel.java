package main.view;

import javax.swing.JButton;
import javax.swing.JPanel;

import main.model.BaseAutomaton;
import main.model.DFAutomaton;
import main.model.PDATransitionKey;
import main.model.PDATransitionValue;
import main.model.PDAutomaton;
import main.model.State;
import main.model.DFATransitionKey;

import java.awt.Graphics;
import java.awt.geom.Ellipse2D;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;


public class AutomatonVisualisationPanel extends JPanel {

    private BaseAutomaton automaton;

    public AutomatonVisualisationPanel(BaseAutomaton automaton) {
        super();
        this.automaton = automaton;

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (State state : automaton.getStates()) {
            Ellipse2D.Double circle = new Ellipse2D.Double(state.getX(), state.getY(), 40, 40);
            g2d.draw(circle);

        }
        if (automaton instanceof DFAutomaton) {
            DFAutomaton currentAutomaton = (DFAutomaton) automaton;
            for (Map.Entry<DFATransitionKey, State> entry : currentAutomaton.getTransitionFunction().entrySet()) {
                double fromX = entry.getKey().getState().getX();
                double fromY = entry.getKey().getState().getY();
                double toX = entry.getValue().getX();
                double toY = entry.getValue().getY();
                g2d.draw(new Line2D.Double(fromX, fromY, toX, toY));

            }
        }
        if (automaton instanceof PDAutomaton) {
            PDAutomaton currentAutomaton = (PDAutomaton) automaton;
            for (Entry<PDATransitionKey, PDATransitionValue> entry : currentAutomaton.getTransitionFunction().entrySet()) {
                double fromX = entry.getKey().getState().getX();
                double fromY = entry.getKey().getState().getY();
                double toX = entry.getValue().getState().getX();
                double toY = entry.getValue().getState().getY();
                g2d.draw(new Line2D.Double(fromX, fromY, toX, toY));
            
             }
        }
        
       
        

  }
    
    
}