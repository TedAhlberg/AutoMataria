package gameobjects.pickups;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import gameclient.Resources;
import gameobjects.GameObject;
import gameobjects.InstantPickup;
import gameobjects.Player;

/**
 * Pickup that reverses left and right directions for all opponents.
 * 
 * @author Dante Håkansson
 *
 */

public class ReversePickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    private int timer = 60;
    private Player player;
    private ConcurrentLinkedQueue<GameObject> gameObjects;
    private boolean reversed;

    public ReversePickup() {
        this(0, 0, 60);

    }

    public ReversePickup(int x, int y, int timer) {
        super(x, y);
        this.timer = timer;
    }

    public void tick() {
        if (!taken) {
            return;
        }
        timer--;
        System.out.println(timer);
        if (timer == 0) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Player) {
                    if (!gameObject.equals(player)) {
                        ((Player) gameObject).setReversed(false);
                    }
                }
            }
        }
        used = true;
        taken = true;
    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
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
