package gameobjects.pickups;

import common.PickupState;
import gameclient.Game;
import gameobjects.*;

import java.awt.*;
import java.util.Collection;

/**
 * Pickup that erases all drawn trails.
 *
 * @author Dante Håkansson
 */
public class EraserPickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    public EraserPickup() {
        this(0, 0);
    }

    public EraserPickup(int x, int y) {
        super(x, y);
    }

    public EraserPickup(EraserPickup object) {
        this(object.getX(), object.getY());
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (getState() != PickupState.NotTaken) return;

        Dimension gridSize = player.getCurrentMap().getGrid();
        gridSize.setSize(gridSize.width * Game.GRID_PIXEL_SIZE, gridSize.height * Game.GRID_PIXEL_SIZE);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Trail) {
                ((Trail) gameObject).remove(new Rectangle(gridSize));
            }
        }

        setState(PickupState.Used);
        gameObjects.remove(this);
    }

    
    public void done() {
        
    }
}
