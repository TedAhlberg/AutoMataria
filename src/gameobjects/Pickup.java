package gameobjects;

import gameclient.Game;

import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * @author Dante Håkansson
 */
public abstract class Pickup extends GameObject {
    private static final long serialVersionUID = 1;
    protected boolean used, taken;

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

    public boolean isTaken() {
        return taken;
    }

    public boolean isUsed() {
        return used;
    }
    public void take(Player player) {
        if(taken) {
            return;
        }
        player.setPickUp(this);
        taken = true;
    }

    public abstract void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects);

}
