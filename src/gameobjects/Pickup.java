package gameobjects;

import gameclient.Game;

import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * @author Dante Håkansson
 */
public abstract class Pickup extends GameObject {
    private static final long serialVersionUID = 1;

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

    public abstract void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects);

}
