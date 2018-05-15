package gameobjects.pickups;

import gameobjects.Pickup;

/**
 * Pickup that speeds the player up for a short duration when activated/used.
 *
 * @author Dante Håkansson
 */

public class SelfSpeedPickup extends Pickup {
    private static final long serialVersionUID = 1;

    public SelfSpeedPickup() {
        this(0, 0, 4000);
    }

    public SelfSpeedPickup(int x, int y, int activeTime) {
        super(x, y);
        setActiveTime(activeTime);
    }

    public SelfSpeedPickup(SelfSpeedPickup object) {
        this(object.getX(), object.getY(), object.getActiveTime());
    }

    public void start() {
        int speed = player.getSpeed();
        player.setSpeed(speed * 2);
        player.setImage("SpeedPickupTransparent.png");
    }

    /**
     * returns the players speed to normal, sets the pickupSlot to null
     * and removes the pickup from the collection of GameObjects.
     */
    public void complete() {
        player.setSpeed(player.getSpeed() / 2);
        player.setImage(null);
    }
}
