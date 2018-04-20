package gameobjects.pickups;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import gameclient.Resources;
import gameobjects.GameObject;
import gameobjects.Pickup;
import gameobjects.Player;

/**
 * Pickup that when activated/used slows down the player for a short duration.
 * 
 * @author Erik Lundow
 *
 */
public class SelfSlowPickup extends Pickup {
    private static final long serialVersionUID = 1;

    private int timer;
    private Player player;
    private ConcurrentLinkedQueue<GameObject> gameObjects;

    public SelfSlowPickup() {
        this(0, 0, 60);
    }

    public SelfSlowPickup(int x, int y, int timer) {
        super(x, y);
        this.timer = timer;
    }

    public void tick() {

        if (!taken || !used)

            return;

        timer--;
        if (timer == 0) {
            player.setSpeed(player.getSpeed() * 2);
            player.setPickUp(null);
            gameObjects.remove(this);

        }
    }

    public void render(Graphics2D g) {
        if (taken) {
            return;
        }
        BufferedImage image = Resources.getImage("SlowSelfPickup.png");
        g.drawImage(image, x, y, width, height, null);

    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
        if (used) {
            return;
        }
        this.gameObjects = gameObjects;
        this.player = player;
        int speed = player.getSpeed();
        player.setSpeed((int) (speed * 0.5));
        used = true;
    }
}
