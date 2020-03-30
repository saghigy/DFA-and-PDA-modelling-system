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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Polygon;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Path2D.Double;
import java.awt.geom.Path2D;
import java.awt.geom.Arc2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.AffineTransform;

public class AutomatonVisualisationPanel extends JPanel {

    private BaseAutomaton automaton;
    private double radius;
    private HashMap<String,Point2D> textBoxForString;

    public AutomatonVisualisationPanel(BaseAutomaton automaton, double radius) {
        super();
        this.automaton = automaton;
        this.radius = radius;
        this.textBoxForString = new HashMap<String,Point2D>();


    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (State state : automaton.getStates()) {
            drawState(g2d, state);
        }
        g2d.setColor(Color.black);
        if (automaton instanceof DFAutomaton) {
            DFAutomaton currentAutomaton = (DFAutomaton) automaton;
            for (Map.Entry<DFATransitionKey, Integer> entry : currentAutomaton.getTransitionFunction().entrySet()) {
                try {
                    State from = currentAutomaton.getStateById(entry.getKey().getStateID());
                    State to = currentAutomaton.getStateById(entry.getValue());
                    if (to.equals(automaton.getCurrentState()) && from.equals(automaton.getPreviousState()) && entry.getKey().getLetter() == automaton.getLastReadLetter()) {
                        g2d.setColor(Color.red);
                    } else {
                        g2d.setColor(Color.black);
                    }

                    if(from.equals(to)){
                        drawDFALoopTransition(g2d, from, entry.getKey().getLetter());
                    } else {
                        drawDFATransition(g2d, from, entry.getKey().getLetter(), to);
                    }
                    
                } catch (StateNotFoundException e) {
                    System.out.println("ERROR");
                }

            }
        }
        if (automaton instanceof PDAutomaton) {
            PDAutomaton currentAutomaton = (PDAutomaton) automaton;
            for (Entry<PDATransitionKey, PDATransitionValue> entry : currentAutomaton.getTransitionFunction()
                    .entrySet()) {
                try {

                    State from = currentAutomaton.getStateById(entry.getKey().getStateID());
                    State to = currentAutomaton.getStateById(entry.getValue().getStateID());
                    if (to.equals(automaton.getCurrentState()) && from.equals(automaton.getPreviousState() ) && entry.getKey().getLetter() == automaton.getLastReadLetter() ) {
                        g2d.setColor(Color.red);
                    } else {
                        g2d.setColor(Color.black);
                    }
                    if(from.equals(to)){
                        drawPDALoopTransition(g2d, from, entry.getKey().getLetter(), entry.getKey().getStackItem(), entry.getValue().getStackItems());
                    } else {
                        drawPDATransition(g2d, from, entry.getKey().getLetter(), entry.getKey().getStackItem(), to, entry.getValue().getStackItems());
                    }
                } catch (StateNotFoundException e) {
                    System.out.println("ERROR");
                }

            }
            
        }
        textBoxForString.clear();
    }

    public void drawState(Graphics2D g2d, State state) {
        // set color by type
        if (state.equals(automaton.getCurrentState())) {
            g2d.setColor(Color.red);
        } else {
            g2d.setColor(Color.black);
        }
        // draw circle
        Ellipse2D.Double circle = new Ellipse2D.Double(state.getX() - radius, state.getY() - radius, radius * 2,
                radius * 2);
        //
        Font font = new Font("TimesRoman", Font.PLAIN, 11);
        FontMetrics metrics = g2d.getFontMetrics(font);
        int x = (int) ((state.getX() - radius) + (radius * 2 - metrics.stringWidth(state.getName())) / 2);
        int y = (int) ((state.getY() - radius) + (((radius * 2) - metrics.getHeight()) / 2) + metrics.getAscent());
        g2d.setFont(font);
        g2d.draw(circle);
        g2d.drawString(state.getName(), x, y);

        if (state.isAcceptState()) {
            Ellipse2D.Double insideCircle = new Ellipse2D.Double(state.getX() - (radius * 0.9),
                    state.getY() - (radius * 0.9), (radius * 2) * 0.9, (radius * 2) * 0.9);
            g2d.draw(insideCircle);
        }
    }

    public void drawDFATransition(Graphics2D g2d, State from, char with, State to) {
        Point2D fromPoint = new Point2D.Double(from.getX(), from.getY());
        Point2D toPoint = new Point2D.Double(to.getX(), to.getY());
        String key = from.getName()+to.getName();
        
        Point2D textEndPosition = textBoxForString.get(key);
        
        if( textEndPosition == null){
        drawBaseOfTransition(g2d, fromPoint, toPoint);
        
        //draw string
       
            g2d.drawString(String.valueOf(with), (int) ((fromPoint.getX() + toPoint.getX()) / 2),
            (int) ((fromPoint.getY() + toPoint.getY()) / 2));

            int width = g2d.getFontMetrics().stringWidth(String.valueOf(with));
            textBoxForString.put(key, new Point2D.Double( (int) ((fromPoint.getX() + toPoint.getX()) / 2) + width,(int) ((fromPoint.getY() + toPoint.getY()) / 2)));
        } else {
            g2d.drawString("," + String.valueOf(with),(int) textEndPosition.getX(),(int)textEndPosition.getY());
            int width = g2d.getFontMetrics().stringWidth("," + String.valueOf(with));
            textBoxForString.put(key, new Point2D.Double((int) (textEndPosition.getX()) + width, (int)textEndPosition.getY()));
        }
        
          
    }

    public void drawPDATransition(Graphics2D g2d, State from, char with, char stackItem, State to, ArrayList<Character> stackString) {
        Point2D fromPoint = new Point2D.Double(from.getX(), from.getY());
        Point2D toPoint = new Point2D.Double(to.getX(), to.getY());
        String writableString =  with + "/" + stackItem + " ->" + stackString;
        drawBaseOfTransition(g2d, fromPoint, toPoint);
        String key = from.getName()+to.getName();
        Point2D textEndPosition = textBoxForString.get(key);
        if( textEndPosition == null){
            g2d.drawString(writableString, (int) ((fromPoint.getX() + toPoint.getX()) / 2),
            (int) ((fromPoint.getY() + toPoint.getY()) / 2));

            int width = g2d.getFontMetrics().stringWidth(writableString+ "  " );
            textBoxForString.put(key, new Point2D.Double( (int) ((fromPoint.getX() + toPoint.getX()) / 2) + width,(int) ((fromPoint.getY() + toPoint.getY()) / 2)));
        } else {
            g2d.drawString(", " + writableString,(int) textEndPosition.getX(),(int)textEndPosition.getY());
            int width = g2d.getFontMetrics().stringWidth(", " + writableString + "  ");
            textBoxForString.put(key, new Point2D.Double((int) (textEndPosition.getX()) + width, (int)textEndPosition.getY()));
        }
        
          
    }


    public void drawDFALoopTransition(Graphics2D g2d, State from, char with){
        Point2D fromPoint = new Point2D.Double(from.getX(), from.getY());
        String key = from.getName() + from.getName();
        drawTransitionToItself(g2d,fromPoint,String.valueOf(with),key);  
    }

    public void drawPDALoopTransition(Graphics2D g2d, State from, char with, char stackItem, ArrayList<Character> stackString){
        Point2D fromPoint = new Point2D.Double(from.getX(), from.getY());
        String key = from.getName() + from.getName();
        String writableString =  with + "/" + stackItem + " ->" + stackString;
        drawTransitionToItself(g2d,fromPoint,writableString,key);
    }

    public void drawTransitionWithArc(Graphics2D g2d, Point2D fromPoint, Point2D toPoint, String writableString) {
        double fromAngle = angleBetween(fromPoint, toPoint);
        double toAngle = angleBetween(toPoint, fromPoint);

        Point2D pointFrom = getPointOnCircle(fromPoint, fromAngle);
        Point2D pointTo = getPointOnCircle(toPoint, toAngle);
        QuadCurve2D q = new QuadCurve2D.Float();

        Point2D ctrPoint = new Point2D.Double(
            1,2
        );
        //TODO

        q.setCurve(pointFrom,ctrPoint, pointTo);
        g2d.draw(q);
  

    }

    private void drawTransitionToItself(Graphics2D g2d, Point2D point,String writableString, String key) {
       
        Point2D textEndPosition = textBoxForString.get(key);
        if( textEndPosition == null){
            double fromAngle = angleBetween(point, new Point2D.Double(0,0));
            double toAngle = angleBetween(point, new Point2D.Double(this.getWidth(),0));
            Point2D pointFrom = getPointOnCircle(point, fromAngle);
            Point2D pointTo = getPointOnCircle(point, toAngle);

            Point2D ctrlPoint = new Point2D.Double();
            QuadCurve2D q = new QuadCurve2D.Float();
            Point2D ctrPoint = new Point2D.Double(
                (pointFrom.getX() + pointTo.getX())/2 ,
                ((pointFrom.getY() + pointTo.getY())/2) - 100
            );
            ArrowHead arrowHead = new ArrowHead();
            AffineTransform at = AffineTransform.getTranslateInstance(
                            pointTo.getX() - (arrowHead.getBounds2D().getWidth() / 2d), 
                            pointTo.getY());
            at.rotate(Math.toRadians(180.0), arrowHead.getBounds2D().getCenterX(), 0);
            arrowHead.transform(at);
            g2d.draw(arrowHead);

            q.setCurve(pointFrom,ctrPoint, pointTo);
            g2d.draw(q);

            g2d.drawString(writableString, (int)ctrPoint.getX(),(int)ctrPoint.getY()+50);
            int width = g2d.getFontMetrics().stringWidth(String.valueOf(writableString));
            textBoxForString.put(key, new Point2D.Double( (int)ctrPoint.getX() + width,(int)ctrPoint.getY()+50));

            } else {
                g2d.drawString("," + writableString,(int) textEndPosition.getX(),(int)textEndPosition.getY());
                int width = g2d.getFontMetrics().stringWidth("," + writableString);
                textBoxForString.put(key, new Point2D.Double((int) (textEndPosition.getX()) + width, (int)textEndPosition.getY()));
            }
       

    }
   
    private void drawBaseOfTransition(Graphics2D g2d, Point2D fromPoint, Point2D toPoint) {

        double fromAngle = angleBetween(fromPoint, toPoint);
        double toAngle = angleBetween(toPoint, fromPoint);

        Point2D pointFrom = getPointOnCircle(fromPoint, fromAngle);
        Point2D pointTo = getPointOnCircle(toPoint, toAngle);

        Line2D line = new Line2D.Double(pointFrom, pointTo);
        g2d.draw(line);
        ArrowHead arrowHead = new ArrowHead();
        AffineTransform at = AffineTransform.getTranslateInstance(
                        pointTo.getX() - (arrowHead.getBounds2D().getWidth() / 2d), 
                        pointTo.getY());
        at.rotate(fromAngle, arrowHead.getBounds2D().getCenterX(), 0);
        arrowHead.transform(at);
        g2d.draw(arrowHead);
        
    }

    private Point2D getPointOnCircle(Point2D center, double radians) {

        double x = center.getX();
        double y = center.getY();
    
        radians = radians - Math.toRadians(90.0); // 0 becomes the top
        // Calculate the outter point of the line
        double xPosy = Math.round((float) (x + Math.cos(radians) * radius));
        double yPosy = Math.round((float) (y + Math.sin(radians) * radius));
    
        return new Point2D.Double(xPosy, yPosy);
    
    }

    private double angleBetween(Point2D from, Point2D to) {
        double x = from.getX();
        double y = from.getY();
        double deltaX = to.getX() - x;
        double deltaY = to.getY() - y;
    
        double rotation = -Math.atan2(deltaX, deltaY);
        rotation = Math.toRadians(Math.toDegrees(rotation) + 180);
    
        return rotation;
    }
}