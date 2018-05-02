package gameobjects.pickups;

import gameclient.Resources;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Pickup that when acquired sets the players state to invincible. When
 * invincible the player cannot crash and the players' trail will not be drawn
 * during the duration. This effect lasts for a couple of seconds.
 *
 * @author Dante Håkansson
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
        if (!taken) {
            return;
        }
        timer--;

        if (timer == 0) {
            player.setInvincible(false);
        }
        used = true;
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (taken) {
            return;
        }
        this.player = player;

        player.setInvincible(true);

        taken = true;
    }

    public void render(Graphics2D g) {
        if (taken) {
            return;
        }
        BufferedImage image = Resources.getImage("InvinciblePickup.png");
        g.drawImage(image, x, y, width, height, null);

    }

}
