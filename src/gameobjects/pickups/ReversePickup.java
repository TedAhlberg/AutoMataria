package gameobjects.pickups;

import gameobjects.*;

/**
 * Pickup that reverses all movements for a short amount of time,
 * but only for opponents.
 *
 * @author Dante Håkansson
 */

public class ReversePickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    public ReversePickup() {
        this(0, 0, 4000);
    }

    public ReversePickup(int x, int y, int activeTime) {
        super(x, y);
        setActiveTime(activeTime);
    }

    public ReversePickup(ReversePickup object) {
        this(object.getX(), object.getY(), object.getActiveTime());
    }

    public void start() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && !gameObject.equals(player)) {
                ((Player) gameObject).setReversed(true);
            }
        }
    }

    /**
     * sets the affected players' movements back to normal,
     * sets the pickups' state to used, removes the pickup from the collection of GameObjects
     */
    public void complete() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                if (!gameObject.equals(player)) {
                    ((Player) gameObject).setReversed(false);
                }
            }
        }
    }
}
