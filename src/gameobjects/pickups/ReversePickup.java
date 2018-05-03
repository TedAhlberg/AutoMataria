package gameobjects.pickups;

import common.PickupState;
import gameclient.Resources;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Pickup that reverses all movements for a short amount of time for all
 * opponents.
 *
 * @author Dante Håkansson
 */

public class ReversePickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    private int initialTimerTime;
    transient private int timer;
    transient private Collection<GameObject> gameObjects;

    public ReversePickup() {
        this(0, 0, 60);
    }

    public ReversePickup(int x, int y, int initialTimerTime) {
        super(x, y);
        this.initialTimerTime = initialTimerTime;
        this.timer = initialTimerTime;
    }

    public ReversePickup(ReversePickup object) {
        this(object.getX(), object.getY(), object.getInitialTimerTime());
    }

    public void tick() {
        if (getState() != PickupState.InUse) return;

        timer--;
        if (timer == 0) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Player) {
                    if (!gameObject.equals(player)) {
                        ((Player) gameObject).setReversed(false);
                    }
                }
            }
            setState(PickupState.Used);
            gameObjects.remove(this);
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (getState() != PickupState.NotTaken) return;

        this.player = player;
        this.gameObjects = gameObjects;

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                if (!gameObject.equals(player)) {
                    ((Player) gameObject).setReversed(true);
                }
            }
        }

        setState(PickupState.InUse);
    }

    public int getInitialTimerTime() {
        return initialTimerTime;
    }
}
