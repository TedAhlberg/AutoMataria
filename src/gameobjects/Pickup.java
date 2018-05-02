package gameobjects;

import gameclient.Game;

import java.util.Collection;

/*
 * @author Dante Håkansson
 */
public abstract class Pickup extends GameObject {
    private static final long serialVersionUID = 1;
    protected boolean used, taken;
    protected Player player;

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
        if (taken) {
            return;
        }
        player.setPickUp(this);
        this.player = player;
        taken = true;
    }

    public abstract void use(Player player, Collection<GameObject> gameObjects);

}
