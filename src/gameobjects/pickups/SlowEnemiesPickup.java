package gameobjects.pickups;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.PickupState;
import gameclient.Resources;
import gameobjects.GameObject;
import gameobjects.InstantPickup;
import gameobjects.Player;

/**
 * Pickup that slows opponents for a short duration.
 * 
 * @author Dante Håkansson
 * @author Johannes Blüml
 * 
 */

public class SlowEnemiesPickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    private int timer;
    private ConcurrentLinkedQueue<GameObject> gameObjects;
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
        if (getState() == PickupState.NotTaken || getState() == PickupState.InUse) {
            return;
        }

        timer--;
        if (timer == 0) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Player) {
                    if (!gameObject.equals(player)) {
                        int speed = gameObject.getSpeed();
                        gameObject.setSpeed((int) (speed * 2));
                        gameObjects.remove(this);
                        
                        setState(PickupState.Used);
                    }
                }
            }
        }
    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {

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
     * 
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
