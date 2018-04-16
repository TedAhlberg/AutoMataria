package gameobjects.pickups;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import gameclient.Resources;
import gameobjects.*;

/**
 * @author Dante H�kansson
 * 
 */

public class SelfSpeedPickup extends Pickup {

    private int timer;

    public SelfSpeedPickup(int x, int y) {
        super(x, y);
    }

    public void tick() {
    }

    public void render(Graphics2D g) {
       BufferedImage image = Resources.getImage("SelfSpeedUp2.png");
        g.drawImage(image, x, y, width, height, null);

    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
        int speed = player.getSpeed();
        player.setSpeed((int) (speed*2));
    }

}
