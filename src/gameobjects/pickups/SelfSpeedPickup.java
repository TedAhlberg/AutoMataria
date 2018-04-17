package gameobjects.pickups;

import gameclient.Resources;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Dante H�kansson
 */

public class SelfSpeedPickup extends Pickup {
    private static final long serialVersionUID = 1;

    private int timer = 60;
    private Player player;
    private boolean pickedUp = false;

    public SelfSpeedPickup() {
        this(0, 0);
    }

    public SelfSpeedPickup(int x, int y) {
        super(x, y);
    }

    public void tick() {
        if (player == null) {
            return;
        }
        timer--;
        if (timer <= 0) {
            player.setSpeed(player.getSpeed() / 2);
            player.setPickUp(null);
        }
    }

    public void render(Graphics2D g) {
        BufferedImage image = Resources.getImage("SelfSpeedUp2.png");
        g.drawImage(image, x, y, width, height, null);

    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
        this.player = player;
        int speed = player.getSpeed();
        player.setSpeed((int) (speed * 2));
    }
}
