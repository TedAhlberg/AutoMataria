package gameobjects;

import common.Game;
import common.PickupState;
import gameclient.Resources;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * @author Dante Håkansson
 * @author Johannes Bluml
 */
public abstract class Pickup extends GameObject {
    private static final long serialVersionUID = 1;

    transient protected Player player;
    transient protected Collection<GameObject> gameObjects;
    transient protected long startTime;
    protected int activeTime;
    protected PickupState state;

    public Pickup() {
        this(0, 0);
    }

    public Pickup(int x, int y) {
        super(x, y);
        width = height = (int) (Game.GRID_PIXEL_SIZE * 1.5);
        activeTime = 0;
        state = PickupState.NotTaken;
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

    public void tick() {
        if (state != PickupState.InUse) return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= activeTime) {
            done();
        }
    }

    /**
     * A Player can take a Pickup with this method
     * after this the player will have this Pickup in its slot
     */
    public void take(Player player, Collection<GameObject> gameObjects) {
        if (state != PickupState.NotTaken) return;
        state = PickupState.Taken;
        player.setPickUp(this);
        this.player = player;
        this.gameObjects = gameObjects;
    }

    /**
     * Starts this pickup and starts the timer (time until complete() is called)
     */
    public void use(Player player, Collection<GameObject> gameObjects) {
        if (state != PickupState.Taken) return;
        setState(PickupState.InUse);

        this.player = player;
        this.gameObjects = gameObjects;

        startTime = System.currentTimeMillis();
        start();
    }

    /**
     * Called when this pickup is used
     */
    protected abstract void start();

    /**
     * Called when activeTime is up or if done() is called from outside (for example to reset the state)
     */
    protected abstract void complete();

    /**
     * Force this pickup to complete/restore the changed values
     */
    public void done() {
        if (state == PickupState.InUse) complete();
        state = PickupState.Used;
        player.setPickUp(null);
        gameObjects.remove(this);
    }

    public PickupState getState() {
        return state;
    }

    public void setState(PickupState state) {
        this.state = state;
    }

    public int getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(int activeTime) {
        this.activeTime = activeTime;
    }
}

