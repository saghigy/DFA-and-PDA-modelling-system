package main.view;

import javax.swing.JButton;
import javax.swing.JPanel;

import main.model.BaseAutomaton;
import main.model.DFAutomaton;
import main.model.PDATransitionKey;
import main.model.PDATransitionValue;
import main.model.PDAutomaton;
import main.model.State;
import main.model.exceptions.StateNotFoundException;
import main.model.DFATransitionKey;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class AutomatonVisualisationPanel extends JPanel {

    private BaseAutomaton automaton;
    private double radius;

    public AutomatonVisualisationPanel(BaseAutomaton automaton, double radius) {
        super();
        this.automaton = automaton;
        this.radius = radius;

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (State state : automaton.getStates()) {
            if (state.equals(automaton.getCurrentState())) {
                g2d.setColor(Color.red);
            } else {
                g2d.setColor(Color.black);
            }
            Ellipse2D.Double circle = new Ellipse2D.Double(state.getX() - radius, state.getY() - radius, radius * 2,
                    radius * 2);
            g2d.drawString(state.getName(), (int) state.getX(), (int) state.getY());
            g2d.draw(circle);
            if (state.isAcceptState()) {
                Ellipse2D.Double insideCircle = new Ellipse2D.Double(state.getX() - (radius * 0.9),
                        state.getY() - (radius * 0.9), (radius * 2) * 0.9, (radius * 2) * 0.9);
                g2d.draw(insideCircle);
            }

        }
        g2d.setColor(Color.black);
        if (automaton instanceof DFAutomaton) {
            DFAutomaton currentAutomaton = (DFAutomaton) automaton;
            for (Map.Entry<DFATransitionKey, Integer> entry : currentAutomaton.getTransitionFunction().entrySet()) { 
                try {
                    double fromX = currentAutomaton.getStateById(entry.getKey().getStateID()).getX();
                    double fromY = currentAutomaton.getStateById(entry.getKey().getStateID()).getY();
                    double toX = currentAutomaton.getStateById(entry.getValue()).getX();
                    double toY = currentAutomaton.getStateById(entry.getValue()).getY();
                    g2d.draw(new Line2D.Double(fromX, fromY, toX, toY));
                    g2d.drawString(String.valueOf(entry.getKey().getLetter()), (int)((fromX + toX)/2),(int)((fromY + toY)/2));
                } catch (StateNotFoundException e) {
                    System.out.println("ERROR");
                }

            }
        }
        if (automaton instanceof PDAutomaton) {
            PDAutomaton currentAutomaton = (PDAutomaton) automaton;
            for (Entry<PDATransitionKey, PDATransitionValue> entry : currentAutomaton.getTransitionFunction().entrySet()) {
                try {
                    double fromX = currentAutomaton.getStateById(entry.getKey().getStateID()).getX();
                    double fromY = currentAutomaton.getStateById(entry.getKey().getStateID()).getY();
                    double toX = currentAutomaton.getStateById(entry.getValue().getStateID()).getX();
                    double toY = currentAutomaton.getStateById(entry.getValue().getStateID()).getY();
                    g2d.draw(new Line2D.Double(fromX, fromY, toX, toY));
                    g2d.drawString(entry.getKey().getLetter() + "/" + entry .getKey().getStackItem() + " ->" + entry.getValue().getStackItems(), (int)((fromX + toX)/2),(int)((fromY + toY)/2));
                }catch(StateNotFoundException e) {
                    System.out.println("ERROR");
                }
            
             }
        }
        
       
        

  }
    
    
}