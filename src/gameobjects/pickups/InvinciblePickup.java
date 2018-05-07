package gameobjects.pickups;

import common.PickupState;
import gameobjects.*;

import java.util.Collection;

/**
 * Pickup that when acquired sets the players state to invincible. When
 * invincible the player cannot crash and the players' trail will not be drawn
 * during the duration. This effect lasts for a couple of seconds.
 *
 * @author Dante Håkansson
 */
public class InvinciblePickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    transient private Collection<GameObject> gameObjects;
    transient private long startTime;

    public InvinciblePickup() {
        this(0, 0, 4000);
    }

    public InvinciblePickup(int x, int y, int activeTime) {
        super(x, y);
        setActiveTime(activeTime);
    }

    public InvinciblePickup(InvinciblePickup object) {
        this(object.getX(), object.getY(), object.getActiveTime());
    }

    public void tick() {
        if (getState() != PickupState.InUse) return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= activeTime) {
            done();
        }
    }

    public void use(Player player, Collection<GameObject> gameObjects) {
        if (getState() != PickupState.NotTaken) return;

        startTime = System.currentTimeMillis();

        this.player = player;
        this.gameObjects = gameObjects;

        player.setInvincible(true);
        setState(PickupState.InUse);
    }

    
    public void done() {
        player.setInvincible(false);
        setState(PickupState.Used);
        gameObjects.remove(this);
        
    }
}
