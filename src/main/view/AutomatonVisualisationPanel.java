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
    private double radius;

    public AutomatonVisualisationPanel(BaseAutomaton automaton,double radius) {
        super();
        this.automaton = automaton;
        this.radius = radius;

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (State state : automaton.getStates()) {
            Ellipse2D.Double circle = new Ellipse2D.Double(state.getX() - radius, state.getY() - radius, radius*2, radius*2);
            g2d.drawString(state.getName(),(int)state.getX(), (int)state.getY() );
            g2d.draw(circle);
            if (state.isAcceptState()){
                Ellipse2D.Double insideCircle = new Ellipse2D.Double (state.getX() - (radius*0.9), state.getY() - (radius*0.9), (radius*2)*0.9, (radius*2)*0.9);
                g2d.draw(insideCircle);
            }

        }
        if (automaton instanceof DFAutomaton) {
            DFAutomaton currentAutomaton = (DFAutomaton) automaton;
            for (Map.Entry<DFATransitionKey, State> entry : currentAutomaton.getTransitionFunction().entrySet()) {
                double fromX = entry.getKey().getState().getX() ;
                double fromY = entry.getKey().getState().getY();
                double toX = entry.getValue().getX();
                double toY = entry.getValue().getY();
                g2d.draw(new Line2D.Double(fromX, fromY, toX, toY));
                g2d.drawString(String.valueOf(entry.getKey().getLetter()), (int)((fromX + toX)/2),(int)((fromY + toY)/2));

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
                g2d.drawString(entry.getKey().getLetter() + "/" + entry .getKey().getStackItem() + " ->" + entry.getValue().getStackItems(), (int)((fromX + toX)/2),(int)((fromY + toY)/2));
            
             }
        }
        
       
        

  }
    
    
}