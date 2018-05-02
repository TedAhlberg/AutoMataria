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
 * Pickup that when acquired sets the players state to invincible. When
 * invincible the player cannot crash and the players' trail will not be drawn
 * during the duration. This effect lasts for a couple of seconds.
 * 
 * @author Dante Håkansson
 *
 */

public class InvinciblePickup extends InstantPickup {

    private static final long serialVersionUID = 1;
    private int timer;
    private Player player;

    public InvinciblePickup() {
        this(0, 0, 60);

    }

    public InvinciblePickup(int x, int y, int timer) {
        super(x, y);
        this.timer = timer;
    }

    public void tick() {
        if (getState() == PickupState.NotTaken) {
            return;
        }
        timer--;
        
        if (timer == 0) {
            player.setInvincible(false);
        }
        setState(PickupState.Used);
    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
        if (getState() == PickupState.Taken) {
            return;
        }
        this.player = player;

        player.setInvincible(true);

        setState(PickupState.Taken);
    }

    public void render(Graphics2D g) {
        if (getState() != PickupState.NotTaken) {
            return;
        }
        BufferedImage image = Resources.getImage("InvinciblePickup.png");
        g.drawImage(image, x, y, width, height, null);

    }

}
