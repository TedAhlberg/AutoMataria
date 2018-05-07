package gameobjects.pickups;

import common.PickupState;
import gameobjects.*;

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
        if (state != PickupState.NotTaken) return;
        setState(PickupState.Used);

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Trail) {
                ((Trail) gameObject).clear();
            }
        }

        gameObjects.remove(this);
    }


    public void done() {}
}
