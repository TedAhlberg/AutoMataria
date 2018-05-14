package gameobjects.pickups;

import gameobjects.*;

/**
 * Pickup that erases all drawn trails.
 *
 * @author Dante Håkansson
 */
public class EraserPickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    public EraserPickup() {
        this(0, 0);
    }

    public EraserPickup(int x, int y) {
        super(x, y);
    }

    public EraserPickup(EraserPickup object) {
        this(object.getX(), object.getY());
    }

    public void start() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Trail) {
                ((Trail) gameObject).clear();
            }
        }
    }

    public void complete() {}
}
