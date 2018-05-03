package gameobjects.pickups;

import common.PickupState;
import gameclient.Resources;
import gameclient.SoundFx;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Pickup that increases all opponents speed for a short duration.
 *
 * @author Erik Lundow
 */
public class SpeedEnemiesPickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    private int initialTimerTime;
    transient private int timer;
    transient private Collection<GameObject> gameObjects;

    public SpeedEnemiesPickup() {
        this(0, 0, 60);
    }

    public SpeedEnemiesPickup(int x, int y, int initialTimerTime) {
        super(x, y);
        this.initialTimerTime = initialTimerTime;
        this.timer = initialTimerTime;
    }

    public SpeedEnemiesPickup(SpeedEnemiesPickup object) {
        this(object.getX(), object.getY(), object.getInitialTimerTime());
    }

    public void tick() {
        if (getState() != PickupState.InUse) return;

        timer--;
        if (timer == 0) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Player && !gameObject.equals(player)) {
                    gameObject.setSpeed((gameObject.getSpeed() / 2));
                }
            }
            player.setPickUp(null);
            gameObjects.remove(this);
            setState(PickupState.Used);
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (getState() != PickupState.NotTaken) return;

        this.player = player;
        this.gameObjects = gameObjects;

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && !gameObject.equals(player)) {
                int speed = gameObject.getSpeed();
                gameObject.setSpeed(speed * 2);
            }
        }

        setState(PickupState.InUse);
    }

    public int getInitialTimerTime() {
        return initialTimerTime;
    }
}
