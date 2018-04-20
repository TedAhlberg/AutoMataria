package gameobjects.pickups;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import gameclient.Resources;
import gameobjects.GameObject;
import gameobjects.Pickup;
import gameobjects.Player;
/**
 * 
 * @author Erik Lundow
 *
 */
public class SelfSlowPickup extends Pickup {
    private static final long serialVersionUID = 1;

    private int timer = 60;
    private Player player;
    private boolean pickedUp = false;

    public SelfSlowPickup() {
        this(0, 0);
    }

    public SelfSlowPickup(int x, int y) {
        super(x, y);
    }

    public void tick() {
        if(!taken||used)
            return;
        
        timer--;
        if (timer <= 0) {
            player.setSpeed(player.getSpeed()*2);
            player.setPickUp(null);
        }
    }

    public void render(Graphics2D g) {
        BufferedImage image = Resources.getImage("Blank.png");
        g.drawImage(image, x, y, width, height, null);

    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
        this.player = player;
        int speed = player.getSpeed();
        player.setSpeed((int) (speed/2));
    }
}
