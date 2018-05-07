package gameobjects.pickups;

import common.PickupState;
import gameobjects.*;

import java.util.Collection;

/**
 * Pickup that speeds the player up for a short duration when activated/used.
 *
 * @author Dante Håkansson
 */

public class SelfSpeedPickup extends Pickup {
    private static final long serialVersionUID = 1;

    transient private Collection<GameObject> gameObjects;
    transient private long startTime;

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

    public void tick() {
        if (getState() != PickupState.InUse) return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= activeTime) {
            done();
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (getState() != PickupState.Taken) return;

        startTime = System.currentTimeMillis();

        this.player = player;
        this.gameObjects = gameObjects;
        int speed = player.getSpeed();
        player.setSpeed(speed * 2);

        setState(PickupState.InUse);
    }

    /**
     * returns the players speed to normal, sets the pickupSlot to null
     * and removes the pickup from the collection of GameObjects.
     * 
     */
    public void done() {
        player.setSpeed(player.getSpeed() / 2);
        player.setPickUp(null);
        setState(PickupState.Used);
        gameObjects.remove(this);
        
    }
}
