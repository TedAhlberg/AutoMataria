package gameobjects.pickups;

import common.PickupState;
import gameclient.Resources;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Pickup that slows opponents for a short duration.
 *
 * @author Dante Håkansson
 * @author Johannes Blüml
 */

public class SlowEnemiesPickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    private int timer;
    private Collection<GameObject> gameObjects;
    private Player player;

    public SlowEnemiesPickup() {
        this(0, 0, 60);
    }

    public SlowEnemiesPickup(int x, int y, int timer) {
        super(x, y);
        this.timer = timer;
    }

    public SlowEnemiesPickup(SelfSpeedPickup object) {
        this(object.getX(), object.getY(), object.getTimer());

    }

    public void tick() {
        if (getState() == PickupState.NotTaken || getState() != PickupState.Used) {
            return;
        }

        timer--;
        if (timer == 0) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Player) {
                    if (!gameObject.equals(player)) {
                        int speed = gameObject.getSpeed();
                        gameObject.setSpeed(speed * 2);
                        gameObjects.remove(this);

                        setState(PickupState.Used);
                    }
                }
            }
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {

        if (getState() == PickupState.Taken) {
            return;
        }

        this.player = player;
        this.gameObjects = gameObjects;

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                if (!gameObject.equals(player)) {
                    int speed = gameObject.getSpeed();
                    gameObject.setSpeed((int) (speed * 0.5));
                }
            }
        }
        setState(PickupState.Taken);
    }

    /**
     * @see gameobjects.GameObject#render(java.awt.Graphics2D)
     */
    public void render(Graphics2D g) {
        if (getState() != PickupState.NotTaken) {
            return;
        }
        BufferedImage image = Resources.getImage("SlowEnemiesPickup.png");
        g.drawImage(image, x, y, width, height, null);
    }
}
