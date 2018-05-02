package gameobjects.pickups;

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

    private int timer = 60;
    private Player player;
    private Collection<GameObject> gameObjects;

    public ReversePickup() {
        this(0, 0, 60);

    }

    public ReversePickup(int x, int y, int timer) {
        super(x, y);
        this.timer = timer;
    }

    public void tick() {
        if (!taken || used) {
            return;
        }
        timer--;
        if (timer == 0) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Player) {
                    if (!gameObject.equals(player)) {
                        ((Player) gameObject).setReversed(false);
                    }
                }
            }
            used = true;
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (taken) {
            return;
        }

        this.player = player;
        this.gameObjects = gameObjects;

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                if (!gameObject.equals(player)) {
                    ((Player) gameObject).setReversed(true);

                }
            }
        }
        taken = true;
    }

    public void render(Graphics2D g) {
        if (taken) {
            return;
        }
        BufferedImage image = Resources.getImage("ReversePickup.png");
        g.drawImage(image, x, y, width, height, null);

    }

}
