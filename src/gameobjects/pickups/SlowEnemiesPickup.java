package gameobjects.pickups;

import gameobjects.*;

/**
 * Pickup that slows opponents for a short duration.
 *
 * @author Dante Håkansson
 * @author Johannes Blüml
 */
public class SlowEnemiesPickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    public SlowEnemiesPickup() {
        this(0, 0, 4000);
    }

    public SlowEnemiesPickup(int x, int y, int activeTime) {
        super(x, y);
        setActiveTime(activeTime);
    }

    public SlowEnemiesPickup(SlowEnemiesPickup object) {
        this(object.getX(), object.getY(), object.getActiveTime());

    }

    public void start() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && !gameObject.equals(player)) {
                int speed = gameObject.getSpeed();
                gameObject.setSpeed((int) (speed * 0.5));
                ((Player) gameObject).setImage("TransparentSelfSlowPickup.png");
            }
        }
    }

    /**
     * Returns the enemies speed to normal, sets the pickups state to used
     * and removes the pickup from the collection of GameObjects.
     */
    public void complete() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && !gameObject.equals(player)) {
                int speed = gameObject.getSpeed();
                gameObject.setSpeed(speed * 2);
                ((Player) gameObject).setImage(null);
            }
        }
    }
}
