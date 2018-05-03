package gameobjects;

import common.PickupState;
import gameclient.Game;
import gameclient.Resources;

import javax.imageio.IIOException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/*
 * @author Dante Håkansson
 */
public abstract class Pickup extends GameObject {
    private static final long serialVersionUID = 1;

    protected PickupState state = PickupState.NotTaken;
    transient protected Player player;

    public Pickup() {
        this(0, 0);
    }

    public Pickup(int x, int y) {
        super(x, y);
        width = (int) (Game.GRID_PIXEL_SIZE * 1.5);
        height = width;
    }

    public void tick() {
    }

    public void render(Graphics2D g) {
        if (getState() != PickupState.NotTaken) return;

        String pickupName = this.getClass().getSimpleName();
        BufferedImage image = Resources.getImage(pickupName + ".png");
        if (image == null) {
            image = Resources.getImage("PlaceHolder.png");
        }
        g.drawImage(image, x, y, width, height, null);
    }

    public PickupState getState() {
        return state;
    }

    public void setState(PickupState state) {
        this.state = state;
    }

    public void take(Player player) {
        if (state != PickupState.NotTaken) return;

        state = PickupState.Taken;
        player.setPickUp(this);
        this.player = player;
    }

    public abstract void use(Player player, Collection<GameObject> gameObjects);
}
