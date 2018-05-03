package gameobjects.pickups;

import common.PickupState;
import gameclient.Resources;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Pickup that when acquired sets the players state to invincible. When
 * invincible the player cannot crash and the players' trail will not be drawn
 * during the duration. This effect lasts for a couple of seconds.
 *
 * @author Dante Håkansson
 */

public class InvinciblePickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    private int initialTimerTime;
    transient private int timer;
    transient private Collection<GameObject> gameObjects;

    public InvinciblePickup() {
        this(0, 0, 60);
    }

    public InvinciblePickup(int x, int y, int initialTimerTime) {
        super(x, y);
        this.initialTimerTime = initialTimerTime;
        this.timer = initialTimerTime;
    }

    public InvinciblePickup(InvinciblePickup object) {
        this(object.getX(), object.getY(), object.getInitialTimerTime());
    }

    public void tick() {
        if (getState() != PickupState.InUse) return;

        timer--;
        if (timer == 0) {
            player.setInvincible(false);
            setState(PickupState.Used);
            gameObjects.remove(this);
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (getState() != PickupState.NotTaken) return;

        this.player = player;
        this.gameObjects = gameObjects;

        player.setInvincible(true);
        setState(PickupState.InUse);
    }

    public int getInitialTimerTime() {
        return initialTimerTime;
    }
}
