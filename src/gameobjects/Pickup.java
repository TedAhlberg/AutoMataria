package gameobjects;

import gameclient.Game;
import gameobjects.GameObject;
import gameobjects.Player;

/*
 * @author Dante Håkansson
 */
public abstract class Pickup extends GameObject {

    public Pickup(int x, int y) {
        super(x, y);
        width = Game.GRID_PIXEL_SIZE;
        height = Game.GRID_PIXEL_SIZE;
    }

    public void tick() {
    }

    public abstract void use(Player player);
    
}
