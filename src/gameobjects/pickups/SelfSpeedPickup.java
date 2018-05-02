package gameobjects.pickups;

import gameclient.Resources;
import gameclient.SoundFx;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.PickupState;

/**
 * Pickup that speeds the player up for a short duration when activated/used.
 * 
 * @author Dante Håkansson
 * 
 */

public class SelfSpeedPickup extends Pickup {
    private static final long serialVersionUID = 1;

    private int timer = 60;
    private Player player;
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
        if (getState() == PickupState.NotTaken || getState() != PickupState.Used) {
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
        if(getState() != PickupState.NotTaken) {
            return;
        }
        BufferedImage image = Resources.getImage("SelfSpeedUp2.png");
        g.drawImage(image, x, y, width, height, null);

    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
        if(getState() == PickupState.Used) {
            return;
        }
        this.player = player;
        this.gameObjects = gameObjects;
        int speed = player.getSpeed();
        player.setSpeed((int) (speed * 2));
        SoundFx.getInstance().SelfSpeedPickup();
        
        setState(PickupState.Used);
    }

    public int getTimer() {
        return timer;
        
    }
}
