package gameobjects.pickups;

import common.PickupState;
import gameobjects.*;

import java.util.Collection;

/**
 * Pickup that when activated/used slows down the player for a short duration.
 *
 * @author Erik Lundow
 */
public class SelfSlowPickup extends Pickup {
    private static final long serialVersionUID = 1;

    private int initialTimerTime;
    transient private int timer;
    transient private Collection<GameObject> gameObjects;

    public SelfSlowPickup() {
        this(0, 0, 60);
    }

    public SelfSlowPickup(SelfSlowPickup object) {
        this(object.getX(), object.getY(), object.getInitialTimerTime());
    }

    public SelfSlowPickup(int x, int y, int initialTimerTime) {
        super(x, y);
        this.initialTimerTime = initialTimerTime;
        this.timer = initialTimerTime;
        System.out.println("TIMER: " + timer);
    }

    public void tick() {
        if (getState() != PickupState.InUse) return;

        timer--;
        if (timer == 0) {
            player.setSpeed(player.getSpeed() * 2);
            player.setPickUp(null);
            setState(PickupState.Used);
            gameObjects.remove(this);
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (getState() != PickupState.Taken) {
            return;
        }
        this.gameObjects = gameObjects;
        this.player = player;
        int speed = player.getSpeed();
        player.setSpeed((int) (speed * 0.5));

        setState(PickupState.InUse);
    }

    public int getInitialTimerTime() {
        return initialTimerTime;
    }
}
