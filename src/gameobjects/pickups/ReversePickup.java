package gameobjects.pickups;

import common.PickupState;
import gameobjects.*;

import java.util.Collection;

/**
 * Pickup that reverses all movements for a short amount of time, 
 * but only for opponents.
 *
 * @author Dante Håkansson
 */

public class ReversePickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    transient private Collection<GameObject> gameObjects;
    transient private long startTime;

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

    public void tick() {
        if (state != PickupState.InUse)
            return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= activeTime) {
            done();
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (state != PickupState.NotTaken) {return;}
        setState(PickupState.InUse);

        startTime = System.currentTimeMillis();

        this.player = player;
        this.gameObjects = gameObjects;

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                if (!gameObject.equals(player)) {
                    ((Player) gameObject).setReversed(true);
                }
            }
        }

    }
    
    /**
     * sets the affected players' movements back to normal,
     * sets the pickups' state to used, removes the pickup from the collection of GameObjects
     */
    public void done() {
        setState(PickupState.Used);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                if (!gameObject.equals(player)) {
                    ((Player) gameObject).setReversed(false);
                }
            }
        }
        gameObjects.remove(this);
    }
}
