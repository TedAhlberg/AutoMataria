package gameobjects.pickups;

import common.PickupState;
import gameobjects.*;

import java.util.Collection;

/**
 * Pickup that slows opponents for a short duration.
 *
 * @author Dante Håkansson
 * @author Johannes Blüml
 */
public class SlowEnemiesPickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    transient private Collection<GameObject> gameObjects;
    transient private long startTime;

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

    public void tick() {
        if (state != PickupState.InUse) return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= activeTime) {
           done();
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (state != PickupState.NotTaken) return;
        setState(PickupState.InUse);

        startTime = System.currentTimeMillis();

        this.player = player;
        this.gameObjects = gameObjects;

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                if (!gameObject.equals(player)) {
                    int speed = gameObject.getSpeed();
                    gameObject.setSpeed((int) (speed * 0.5));
                    ((Player) gameObject).setImage("SlowPickupTransparent.png");
                }
            }
        }
    }

    /**
     * Returns the enemies speed to normal, sets the pickups state to used
     * and removes the pickup from the collection of GameObjects.
     */
    public void done() {
        setState(PickupState.Used);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                if (!gameObject.equals(player)) {
                    int speed = gameObject.getSpeed();
                    gameObject.setSpeed(speed * 2);
                    ((Player) gameObject).setImage(null);
                }
            }
        }
        gameObjects.remove(this);
        
    }
}
