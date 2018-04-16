package gameobjects.pickups;

import gameclient.Game;
import gameclient.Resources;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Dante Håkansson
 */

public class EraserPickup extends InstantPickup {

    public EraserPickup() {
        this(0, 0);
    }

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
