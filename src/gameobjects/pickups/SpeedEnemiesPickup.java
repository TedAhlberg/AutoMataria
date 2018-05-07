package gameobjects.pickups;

import common.PickupState;
import gameobjects.*;

import java.util.Collection;

/**
 * Pickup that increases all opponents speed for a short duration.
 *
 * @author Erik Lundow
 */
public class SpeedEnemiesPickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    transient private Collection<GameObject> gameObjects;
    transient private long startTime;

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
            if (gameObject instanceof Player && !gameObject.equals(player)) {
                int speed = gameObject.getSpeed();
                gameObject.setSpeed(speed * 2);
            }
        }
    }

   
    public void done() {
        setState(PickupState.Used);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && !gameObject.equals(player)) {
                gameObject.setSpeed((gameObject.getSpeed() / 2));
            }
        }
        player.setPickUp(null);
        gameObjects.remove(this);
    }
}
