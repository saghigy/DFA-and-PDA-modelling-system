package tests.model.automaton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import main.model.automaton.State;

public class StateTest {

    private State state1;
    private State state2;
    private State state3;

    @Before
    public void beforeTest(){
        state1 = new State("Test1",0,1);
        state2 = new State("Test2",0.4,112.3);
        state3 = new State("Test1",2,11111.2);
    }
    
    @Test
    public void testStateDeclaration() {
        assertEquals("Test1", state1.getName());
        assertEquals(0, state1.getX(),0);
        assertEquals(1, state1.getY(),0);

        assertEquals("Test2", state2.getName());
        assertEquals(0.4, state2.getX(),0);
        assertEquals(112.3, state2.getY(),0);
    }

    @Test
    public void testAcceptState() {
        state1.setAccepState(true);
        assertTrue( state1.isAcceptState());
        state1.setAccepState(false);
        assertFalse(state1.isAcceptState());
    }

    @Test
    public void testStartState() {
        state1.setStartState(true);
        assertTrue( state1.isStartState());
        state1.setStartState(false);
        assertFalse(state1.isStartState());
    }

    @Test
    public void testMoveOfState() {
        state1.setNewPosition(23.44, 4442.3);
        assertEquals(23.44, state1.getX(),0);
        assertEquals(4442.3, state1.getY(),0);
        state2.setNewPosition(1, 1);
        assertEquals(1, state2.getX(),0);
        assertEquals(1, state2.getY(),0);
    }

    @Test
    public void testId(){
        state1.setId(2);
        assertEquals(state1.getID(), 2);
    }

    @Test
    public void testEquals(){
        assertFalse(state1.equals(state2));
        assertTrue(state1.equals(state3));
    }

    @Test
    public void testJSONConvert() {
        String stateInJSON = State.stateToJSON(state1);
        assertEquals(state1, State.JSONtoState(stateInJSON));
    }


}