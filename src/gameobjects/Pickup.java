package gameobjects;

import java.util.concurrent.ConcurrentLinkedQueue;

import gameclient.Game;
import gameobjects.GameObject;
import gameobjects.Player;

/*
 * @author Dante Håkansson
 */
public abstract class Pickup extends GameObject {
   

    public Pickup(int x, int y) {
        super(x, y);
        width = (int) (Game.GRID_PIXEL_SIZE * 1.5);
        height = width;
        
    }

    public void tick() {
    }

    public abstract void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects);
    
}
