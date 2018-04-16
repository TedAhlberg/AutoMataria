package gameobjects.pickups;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import gameclient.Game;
import gameclient.Resources;
import gameobjects.GameObject;
import gameobjects.InstantPickup;
import gameobjects.Pickup;
import gameobjects.Player;
import gameobjects.Trail;

/**
 * @author Dante Håkansson
 * 
 */

public class EraserPickup extends InstantPickup {

    public EraserPickup(int x, int y) {
        super(x, y);

    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
        Dimension gridSize = player.getCurrentMap().getGrid();
        gridSize.setSize(gridSize.width * Game.GRID_PIXEL_SIZE, gridSize.height * Game.GRID_PIXEL_SIZE);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Trail) {
                ((Trail) gameObject).remove(new Rectangle(gridSize));
            }
        }
        player.setPickUp(null);
    }

    public void render(Graphics2D g) {
        BufferedImage image = Resources.getImage("EraserPickup.png");
        g.drawImage(image, x, y, width, height, null);

    }
}
