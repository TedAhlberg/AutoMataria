package gameobjects.pickups;

import gameobjects.*;

/**
 * @author Johannes Blüml
 */
public class SwissCheesePickup extends Pickup {
    private static final long serialVersionUID = 1;

    public SwissCheesePickup() {
        this(0, 0, 6000);
    }

    public SwissCheesePickup(int x, int y, int activeTime) {
        super(x, y);
        setActiveTime(activeTime);
    }

    public SwissCheesePickup(SwissCheesePickup object) {
        this(object.getX(), object.getY(), object.getActiveTime());
    }

    protected void start() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && !gameObject.equals(player)) {
                ((Player) gameObject).setCheese(true);
                ((Player) gameObject).setImage("TransparentSwissCheesePickup.png");
            }
        }
    }

    protected void complete() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                if (!gameObject.equals(player)) {
                    ((Player) gameObject).setCheese(false);
                    ((Player) gameObject).setImage(null);
                }
            }
        }
    }
}
