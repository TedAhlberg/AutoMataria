package gameobjects.pickups;

import gameobjects.*;

/**
 * Pickup that increases all opponents speed for a short duration.
 *
 * @author Erik Lundow
 */
public class SpeedEnemiesPickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    public SpeedEnemiesPickup() {
        this(0, 0, 4000);
    }

    public SpeedEnemiesPickup(int x, int y, int activeTime) {
        super(x, y);
        setActiveTime(activeTime);
    }

    public SpeedEnemiesPickup(SpeedEnemiesPickup object) {
        this(object.getX(), object.getY(), object.getActiveTime());
    }

    public void start() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && !gameObject.equals(player)) {
                int speed = gameObject.getSpeed();
                gameObject.setSpeed(speed * 2);
                ((Player) gameObject).setImage("SpeedPickupTransparent.png");
            }
        }
    }

    public void complete() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && !gameObject.equals(player)) {
                gameObject.setSpeed((gameObject.getSpeed() / 2));
                ((Player) gameObject).setImage(null);
            }
        }
    }
}
