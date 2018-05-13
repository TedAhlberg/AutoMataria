package gameobjects.pickups;

import common.PickupState;
import gameobjects.*;

import java.awt.*;
import java.util.Collection;

/**
 * Pickup that turns the player black for a short duration when activated/used.
 *
 * @author Johannes Bluml
 */

public class SelfGhostPickup extends Pickup {
    private static final long serialVersionUID = 1;

    transient private Collection<GameObject> gameObjects;
    transient private long startTime;
    transient private Color playerColor, trailColor, trailBorderColor;

    public SelfGhostPickup() {
        this(0, 0, 10000);
    }

    public SelfGhostPickup(int x, int y, int activeTime) {
        super(x, y);
        setActiveTime(activeTime);
    }

    public SelfGhostPickup(SelfGhostPickup object) {
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
        if (state != PickupState.Taken) return;
        this.gameObjects = gameObjects;
        activate();
    }

    protected void activate() {
        setState(PickupState.InUse);

        startTime = System.currentTimeMillis();

        Trail trail = player.getTrail();
        playerColor = player.getColor();
        trailColor = trail.getColor();
        trailBorderColor = trail.getBorderColor();

        player.setColor(new Color(0, 0, 0, 200));
        trail.setColor(new Color(0, 0, 0, 100));
        trail.setBorderColor(new Color(0, 0, 0, 200));
    }

    /**
     * Returns the players color to normal
     * sets the pickupSlot to null
     * and removes the pickup from the collection of GameObjects.
     */
    public void done() {
        setState(PickupState.Used);
        player.setColor(playerColor);
        player.getTrail().setColor(trailColor);
        player.getTrail().setBorderColor(trailBorderColor);
        player.setPickUp(null);
        gameObjects.remove(this);
    }
}
