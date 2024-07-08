package main.view.automaton;

import java.awt.geom.Path2D;

public class ArrowHead extends Path2D.Double {

    private static final long serialVersionUID = -3991370842073877832L;

    public ArrowHead() {
        int size = 10;
        moveTo(0, size);
        lineTo(size / 2, 0);
        lineTo(size, size);
    }

}
