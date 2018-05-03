package gameobjects.pickups;

import common.PickupState;
import gameclient.Resources;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Pickup that speeds the player up for a short duration when activated/used.
 *
 * @author Dante Håkansson
 */

public class SelfSpeedPickup extends Pickup {
    private static final long serialVersionUID = 1;

    private int initialTimerTime;
    transient private int timer;
    transient private Player player;
    transient private Collection<GameObject> gameObjects;

    public SelfSpeedPickup() {
        this(0, 0, 60);
    }

    public SelfSpeedPickup(int x, int y, int initialTimerTime) {
        super(x, y);
        this.initialTimerTime = initialTimerTime;
        this.timer = initialTimerTime;
    }

    public SelfSpeedPickup(SelfSpeedPickup object) {
        this(object.getX(), object.getY(), object.getInitialTimerTime());
    }

    public void tick() {
        if (getState() != PickupState.InUse) return;

        timer--;
        if (timer == 0) {
            player.setSpeed(player.getSpeed() / 2);
            player.setPickUp(null);
            setState(PickupState.Used);
            gameObjects.remove(this);
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (getState() != PickupState.Taken) {
            return;
        }
        this.player = player;
        this.gameObjects = gameObjects;
        int speed = player.getSpeed();
        player.setSpeed(speed * 2);

        setState(PickupState.InUse);
    }

    public int getInitialTimerTime() {
        return initialTimerTime;
    }
}
