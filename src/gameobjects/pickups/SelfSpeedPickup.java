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
    private ConcurrentLinkedQueue<GameObject> gameObjects;

    public SelfSpeedPickup() {
        this(0, 0, 60);
        
    }

    public SelfSpeedPickup(int x, int y, int timer) {
        super(x, y);
        this.timer = timer;
    }
    public SelfSpeedPickup(SelfSpeedPickup object) {
       this(object.getX(), object.getY(), object.getTimer());
    }

    public void tick() {
        if (!taken || !used) {
            return;
        }
        timer--;
        if (timer == 0) {
            player.setSpeed(player.getSpeed() / 2);
            player.setPickUp(null);
            gameObjects.remove(this);
        }
    }

    public void render(Graphics2D g) {
        if(taken) {
            return;
        }
        BufferedImage image = Resources.getImage("SelfSpeedUp2.png");
        g.drawImage(image, x, y, width, height, null);

    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
        if(used) {
            return;
        }
        this.player = player;
        this.gameObjects = gameObjects;
        int speed = player.getSpeed();
        player.setSpeed((int) (speed * 2));
        used = true;
    }

    public int getTimer() {
        return timer;
        
    }
}
