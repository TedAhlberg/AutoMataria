package gameobjects.pickups;

import common.PickupState;
import gameobjects.*;

import java.util.Collection;

/**
 * Pickup that when activated/used slows down the player for a short duration,
 * after a few seconds the player goes back to their normal speed.
 *
 * @author Erik Lundow
 * @author Dante Håkansson
 */
public class SelfSlowPickup extends Pickup {
    private static final long serialVersionUID = 1;

    transient private Collection<GameObject> gameObjects;
    transient private long startTime;

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

    public void tick() {
        if (state != PickupState.InUse) return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= activeTime) {
           done();
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (state != PickupState.Taken) return;
        setState(PickupState.InUse);

        startTime = System.currentTimeMillis();

        this.gameObjects = gameObjects;
        this.player = player;
        int speed = player.getSpeed();
        player.setSpeed((int) (speed * 0.5));
    }

    /**
     * Sets the player speed back to normal, sets the pickupSlot to null, sets the pickups state to used
     * and removes the pickup from the collection of GameObjects.
     */
    public void done() {
        setState(PickupState.Used);
        player.setSpeed(player.getSpeed() * 2);
        player.setPickUp(null);
        gameObjects.remove(this);
    }
}
