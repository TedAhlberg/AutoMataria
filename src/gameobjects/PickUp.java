package gameobjects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.ImageIcon;

import com.sun.prism.Image;

import common.Direction;
import common.GameMap;
import gameclient.Game;
import gameclient.GamePanel;
import gameobjects.GameObject;
import gameobjects.Player;

/*
 * @author Dante Håkansson
 */
public abstract class PickUp extends GameObject {

    private ImageIcon imageicon;
    private long visibleTime = 450;
    private boolean pickedUp;
    protected ConcurrentLinkedQueue<GameObject> gameObjects;
    private GameMap currentMap;
    protected boolean isUsed;

    public PickUp(int x, int y, int visibleTime, ConcurrentLinkedQueue<GameObject> gameObjects) {
        super(x, y);
        width = Game.GRID_PIXEL_SIZE;
        height = Game.GRID_PIXEL_SIZE;
        this.visibleTime = visibleTime;
        this.gameObjects = gameObjects;
    }

    public void tick() {
        pickedUp();
        visibleTime--;
    }

    public abstract void use(Player player);

    /*
     * If a player intersects this pickup they put it in their slot. Sets the value
     * of pickedUp to true and the value for visible to false.
     */
    public void pickedUp() {

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                Player player = ((Player) gameObject);
                if (player.getBounds().intersects(this.getBounds())) {
                    player.setPickUp(this);
                    gameObjects.remove(this);
                }
            }
        }
    }
}
