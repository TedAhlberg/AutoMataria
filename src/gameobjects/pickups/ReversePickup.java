package gameobjects.pickups;

import common.PickupState;
import gameclient.Resources;
import gameclient.SoundFx;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Pickup that reverses all movements for a short amount of time for all
 * opponents.
 *
 * @author Dante Håkansson
 */

public class ReversePickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    private int timer = 60;
    private Player player;
    private Collection<GameObject> gameObjects;

    public ReversePickup() {
        this(0, 0, 60);

    }

    public ReversePickup(int x, int y, int timer) {
        super(x, y);
        this.timer = timer;
    }

    public void tick() {
        if (getState() == PickupState.NotTaken || getState() == PickupState.Used) {
            return;
        }
        timer--;
        if (timer == 0) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Player) {
                    if (!gameObject.equals(player)) {
                        ((Player) gameObject).setReversed(false);
                    }
                }
            }
            setState(PickupState.Used);
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (getState() == PickupState.Taken) {
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
        setState(PickupState.Taken);
    }

    public void render(Graphics2D g) {
        if (getState() != PickupState.NotTaken) {
            return;
        }
        BufferedImage image = Resources.getImage("ReversePickup.png");
        g.drawImage(image, x, y, width, height, null);

    }

}
