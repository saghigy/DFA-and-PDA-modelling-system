package main.view.automaton;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.model.automaton.BaseAutomaton;
import main.model.automaton.dfautomaton.DFAutomaton;
import main.model.automaton.pdautomaton.PDATransitionKey;
import main.model.automaton.pdautomaton.PDATransitionValue;
import main.model.automaton.pdautomaton.PDAutomaton;
import main.model.automaton.State;
import main.model.exceptions.StateNotFoundException;
import main.model.automaton.dfautomaton.DFATransitionKey;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Polygon;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Path2D.Double;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Arc2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.AffineTransform;

public class AutomatonVisualisationPanel extends JPanel {

   
    private static final long serialVersionUID = 860415913645882906L;
    private BaseAutomaton automaton;
    private double radius;
    private HashMap<String,Point2D> textBoxForString;

    public AutomatonVisualisationPanel(BaseAutomaton automaton, double radius) {
        super();
        this.automaton = automaton;
        this.radius = radius;
        this.textBoxForString = new HashMap<String,Point2D>();
    }

    /**
     * Change the radius' size.
     * @param radius The new radius.
     */
    public void changeRadius(double radius) {
        this.radius = radius;
        repaint();
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
        if(state.isStartState()) {
            Point2D pointTo = new Point2D.Double(state.getX(),state.getY());
            Point2D arrowFrom = new Point2D.Double(state.getX()-radius*2.8,state.getY());
            drawBaseOfTransition(g2d, arrowFrom, pointTo);
        }
    }

    /**
     * Draws a transition from a state to another for a DFA.
     * @param g2d The graphics to paint on.
     * @param from The start state of the transition.
     * @param with The key for transition.
     * @param to The end state of the transition.
     */
    public void drawDFATransition(Graphics2D g2d, State from, char with, State to) {
        Point2D fromPoint = new Point2D.Double(from.getX(), from.getY());
        Point2D toPoint = new Point2D.Double(to.getX(), to.getY());
        String key = from.getName()+to.getName();
        String reverseKey = to.getName()+from.getName();

        Point2D textEndPosition = textBoxForString.get(key);
        Point2D reversedTextEndPosition = textBoxForString.get(reverseKey);
        
        // if there's no same transition yet
        if( textEndPosition == null){
            //  if there's a transition from the reversed direction
            if(reversedTextEndPosition != null) {
                drawTransitionWithArc(g2d, fromPoint, toPoint, String.valueOf(with), key); // draw a arc transition
                
            } else{
                drawBaseOfTransition(g2d, fromPoint, toPoint); // draw simple transition
                //draw string
                g2d.drawString(String.valueOf(with), (int) ((fromPoint.getX() + toPoint.getX()) / 2),
                (int) ((fromPoint.getY() + toPoint.getY()) / 2));
    
                int width = g2d.getFontMetrics().stringWidth(String.valueOf(with));
                textBoxForString.put(key, new Point2D.Double( (int) ((fromPoint.getX() + toPoint.getX()) / 2) + width,(int) ((fromPoint.getY() + toPoint.getY()) / 2)));
            }
        } else {
            // if there's a transition between the same points
            // draw the next charachter nex to the previous
            g2d.drawString("," + String.valueOf(with),(int) textEndPosition.getX(),(int)textEndPosition.getY());
            int width = g2d.getFontMetrics().stringWidth("," + String.valueOf(with));
            textBoxForString.put(key, new Point2D.Double((int) (textEndPosition.getX()) + width, (int)textEndPosition.getY()));
        } 
    }

    /**
     * Draws a transition from a state to another for a PDA.
     * @param g2d The graphics to paint on.
     * @param from The start state of the transition.
     * @param with The key for transition.
     * @param stackItem The item needs to be poped from the stack.
     * @param to The end state of the transition.
     * @param stackString The items needs to be pushed in the stack.
     */
    public void drawPDATransition(Graphics2D g2d, State from, char with, char stackItem, State to, ArrayList<Character> stackString) {
        Point2D fromPoint = new Point2D.Double(from.getX(), from.getY());
        Point2D toPoint = new Point2D.Double(to.getX(), to.getY());
        String writableString =  with + "/" + stackItem + " ->" + stackString;
        
        String key = from.getName()+to.getName();
        String reverseKey = to.getName()+from.getName();
        Point2D textEndPosition = textBoxForString.get(key);
        Point2D reversedTextEndPosition = textBoxForString.get(reverseKey);
        if( textEndPosition == null){
            if(reversedTextEndPosition != null) {
                drawTransitionWithArc(g2d, fromPoint, toPoint, writableString, key); // draw a arc transition
                
            } else{
                drawBaseOfTransition(g2d, fromPoint, toPoint);
                g2d.drawString(writableString, (int) ((fromPoint.getX() + toPoint.getX()) / 2),
                (int) ((fromPoint.getY() + toPoint.getY()) / 2));

                int width = g2d.getFontMetrics().stringWidth(writableString+ "  " );
                textBoxForString.put(key, new Point2D.Double( (int) ((fromPoint.getX() + toPoint.getX()) / 2) + width,(int) ((fromPoint.getY() + toPoint.getY()) / 2)));
            }
        } else {
            g2d.drawString(", " + writableString,(int) textEndPosition.getX(),(int)textEndPosition.getY());
            int width = g2d.getFontMetrics().stringWidth(", " + writableString + "  ");
            textBoxForString.put(key, new Point2D.Double((int) (textEndPosition.getX()) + width, (int)textEndPosition.getY()));
        }
        
          
    }

    /**
     * Draws a transition to itself for  DFAutomaton.
     * @param g2d The graphics to paint on.
     * @param from The state to make transition with.
     * @param with The key for transition.
     */
    public void drawDFALoopTransition(Graphics2D g2d, State from, char with){
        Point2D fromPoint = new Point2D.Double(from.getX(), from.getY());
        String key = from.getName() + from.getName();
        drawTransitionToItself(g2d,fromPoint,String.valueOf(with),key);  
    }

    /**
     * Draws a transition to itself for  PDAutomaton.
     * @param g2d The graphics to paint on.
     * @param from The state to make transition with.
     * @param with The key for transition.
     * @param stackItem The item needs to be poped from the stack.
     * @param stackString The items needs to be pushed in the stack.
     */
    public void drawPDALoopTransition(Graphics2D g2d, State from, char with, char stackItem, ArrayList<Character> stackString){
        Point2D fromPoint = new Point2D.Double(from.getX(), from.getY());
        String key = from.getName() + from.getName();
        String writableString =  with + "/" + stackItem + " ->" + stackString;
        drawTransitionToItself(g2d,fromPoint,writableString,key);
    }

    /**
     * Draws a transition between two point with an arc.
     * @param g2d The graphics to paint on.
     * @param fromPoint The starting state's coordinates.
     * @param toPoint The ending state's coordinates.
     * @param writableString The transition's key's string.
     * @param key The key represents the starting and the ending point.
     */
    public void drawTransitionWithArc(Graphics2D g2d, Point2D fromPoint, Point2D toPoint, String writableString, String key) {
        double fromAngle = angleBetween(fromPoint, toPoint);
        double toAngle = angleBetween(toPoint, fromPoint);

        Point2D pointFrom = getPointOnCircle(fromPoint, fromAngle);
        Point2D pointTo = getPointOnCircle(toPoint, toAngle);
        
        // count the ctrl point
        Point2D half = new Point2D.Double(
            (fromPoint.getX() + toPoint.getX())/2,
            (fromPoint.getY() + toPoint.getY())/2
        );
        //  sheer line

        // make line from the start point to the end point
        Line2D line = new Line2D.Double(pointFrom,pointTo);
        // rotate it with 90° on the half point
        Path2D p = new Path2D.Double();
        p.append(line, false);
        AffineTransform t = new AffineTransform();
        t.rotate(Math.toRadians(90),half.getX(),half.getY());
        t.createTransformedShape(line);
        p.transform(t);
        //  draw circle and find the point of the intersection of the circle and the line
        double r=pointFrom.distance(pointTo) / 2;
        
        Point2D start = new Point2D.Double(0,0);
        Point2D end = new Point2D.Double(0,0);
        PathIterator pathIterator = p.getPathIterator(new AffineTransform());
        float[] coords = new float[6];
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    start = new Point2D.Double(coords[0],coords[1]); // start coordinates of the line
                    break;
                case PathIterator.SEG_LINETO:
                    end = new Point2D.Double(coords[0],coords[1]); // end coordinates of the line
                    break;
                }
            pathIterator.next();
        }    
        // set the point with the lower y to the control point
        List<Point2D> points = getCircleLineIntersectionPoint(start, end, new Point2D.Double(half.getX()-(r/2),half.getY()-(r/2)), r);
        List<Point2D> stringPoints = getCircleLineIntersectionPoint(start, end, new Point2D.Double(half.getX()-(r/2),half.getY()-(r/2)), r*0.8);
        Point2D ctrPoint = points.get(0).getY() < points.get(1).getY() ? points.get(0) : points.get(1);
        Point2D ctrStringPoint = stringPoints.get(0).getY() < stringPoints.get(1).getY() ? stringPoints.get(0) : stringPoints.get(1);
        QuadCurve2D q = new QuadCurve2D.Float();
        
        q.setCurve(pointFrom,ctrPoint, pointTo);
        g2d.draw(q);
        //draw arrow
        double arrowAngle = angleBetween(ctrPoint, toPoint);
        ArrowHead arrowHead = new ArrowHead();
        AffineTransform at = AffineTransform.getTranslateInstance(
                            pointTo.getX() - (arrowHead.getBounds2D().getWidth() / 2d), 
                            pointTo.getY());
        at.rotate(arrowAngle, arrowHead.getBounds2D().getCenterX(), 0);
        arrowHead.transform(at);
        g2d.draw(arrowHead);

        

       // draw string
        g2d.drawString(String.valueOf(writableString), (int) (ctrStringPoint.getX()),
        (int) (ctrStringPoint.getY()));
        int width = g2d.getFontMetrics().stringWidth(String.valueOf(writableString));
        textBoxForString.put(key, new Point2D.Double(ctrStringPoint.getX() + width, ctrStringPoint.getY()));

        
  

    }

    /**
     * Draws a transition from a given point to itself.
     * @param g2d The graphics to paint on.
     * @param point The state's coordinates.
     * @param writableString The transition's key's string.
     * @param key The key represents the starting and the ending point.
     */
    private void drawTransitionToItself(Graphics2D g2d, Point2D point,String writableString, String key) {
       
        Point2D textEndPosition = textBoxForString.get(key);
        if( textEndPosition == null){
            double fromAngle = angleBetween(point, new Point2D.Double(0,0));
            double toAngle = angleBetween(point, new Point2D.Double(this.getWidth(),0));
            Point2D pointFrom = getPointOnCircle(point, fromAngle);
            Point2D pointTo = getPointOnCircle(point, toAngle);

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
   
    /**
     * Draws a transition (line) between two given points.
     * @param g2d The graphics to paint on.
     * @param fromPoint The starting state's coordinates.
     * @param toPoint The ending state's coordinates.
     */
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

    /**
     * Get the point on a circle looking towards to another circle with the given angle.
     * @param center The center of the circle.
     * @param radians The degree between this and the other circle.
     * @return The point on the circle looking towards to another circle with the given angle.
     */
    private Point2D getPointOnCircle(Point2D center, double radians) {

        double x = center.getX();
        double y = center.getY();
    
        radians = radians - Math.toRadians(90.0); // 0 becomes the top
        // Calculate the outter point of the line
        double xPosy = Math.round((float) (x + Math.cos(radians) * radius));
        double yPosy = Math.round((float) (y + Math.sin(radians) * radius));
    
        return new Point2D.Double(xPosy, yPosy);
    
    }

    /**
     * The angle between a given start point to an other point.
     * @param from The start point coordinates.
     * @param to The end point's coordinates.
     * @return The angle between the two points
     */
    private double angleBetween(Point2D from, Point2D to) {
        double x = from.getX();
        double y = from.getY();
        double deltaX = to.getX() - x;
        double deltaY = to.getY() - y;
    
        double rotation = -Math.atan2(deltaX, deltaY);
        rotation = Math.toRadians(Math.toDegrees(rotation) + 180);
    
        return rotation;
    }

    /**
     * Calculates the intersection points beween a line and a circle.
     * @param pointA The start position of the line.
     * @param pointB The end position of the line.
     * @param center The center of the circle.
     * @param radius The radius of the circle.
     * @return
     */
    public static List<Point2D> getCircleLineIntersectionPoint(Point2D pointA, Point2D pointB, Point2D center, double radius) {
        double baX = pointB.getX() - pointA.getX();
        double baY = pointB.getY() - pointA.getY();
        double caX = center.getX() - pointA.getX();
        double caY = center.getY() - pointA.getY();

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point2D p1 = new Point2D.Double(pointA.getX() - baX * abScalingFactor1, pointA.getY()
                - baY * abScalingFactor1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        Point2D p2 = new Point2D.Double(pointA.getX() - baX * abScalingFactor2, pointA.getY()
                - baY * abScalingFactor2);
        return Arrays.asList(p1, p2);
    }




  
}