package gameobjects.pickups;

import gameclient.Resources;
import gameobjects.*;
import common.PickupState;

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

    private int timer = 30;
    private Collection<GameObject> gameObjects;
    private Player player;

    public SpeedEnemiesPickup() {
        this(0, 0, 60);
    }

    public SpeedEnemiesPickup(int x, int y, int timer) {
        super(x, y);
        this.timer = timer;
    }

    public void tick() {

        if (getState() == PickupState.NotTaken || getState() == PickupState.InUse)
            return;

        timer--;
        if (timer == 0) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Player && !gameObject.equals(player)) {
                    gameObject.setSpeed((gameObject.getSpeed() / 2));
                    player.setPickUp(null);
                    gameObjects.remove(this);
                    System.out.println("blablabla");
                }
            }

            player.setPickUp(null);
            // player = null;

        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (getState() == PickupState.Taken) {
            return;
        }
        this.player = player;
        this.gameObjects = gameObjects;

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && !gameObject.equals(player)) {
                int speed = gameObject.getSpeed();
                gameObject.setSpeed(speed * 2);
            }
        }

        setState(PickupState.Used);
    }

    public void render(Graphics2D g) {
        if (getState() != PickupState.NotTaken) {
            return;
        }
        BufferedImage image = Resources.getImage("EnemiesSpeedUp.png");
        g.drawImage(image, x, y, width, height, null);
    }
}
