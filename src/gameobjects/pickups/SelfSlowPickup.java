package gameobjects.pickups;

import gameobjects.Pickup;

/**
 * Pickup that when activated/used slows down the player for a short duration,
 * after a few seconds the player goes back to their normal speed.
 *
 * @author Erik Lundow
 * @author Dante Håkansson
 */
public class SelfSlowPickup extends Pickup {
    private static final long serialVersionUID = 1;

    public SelfSlowPickup() {
        this(0, 0, 4000);
    }

    public SelfSlowPickup(SelfSlowPickup object) {
        this(object.getX(), object.getY(), object.getActiveTime());
    }

    public SelfSlowPickup(int x, int y, int activeTime) {
        super(x, y);
        setActiveTime(activeTime);
    }

    public void start() {
        int speed = player.getSpeed();
        player.setSpeed((int) (speed * 0.5));
        player.setImage("TransparentSelfSlowPickup.png");
    }

    /**
     * Sets the player speed back to normal, sets the pickupSlot to null, sets the pickups state to used
     * and removes the pickup from the collection of GameObjects.
     */
    public void complete() {
        player.setSpeed(player.getSpeed() * 2);
        player.setImage(null);
    }
}
