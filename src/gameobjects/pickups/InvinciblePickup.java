package gameobjects.pickups;

import gameobjects.InstantPickup;

/**
 * Pickup that when acquired sets the players state to invincible. When
 * invincible the player cannot crash and the players' trail will not be drawn
 * during the duration. This effect lasts for a couple of seconds.
 *
 * @author Dante Håkansson
 */
public class InvinciblePickup extends InstantPickup {
    private static final long serialVersionUID = 1;

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

    public void start() {
        player.setInvincible(true);
    }

    public void complete() {
        player.setInvincible(false);
    }
}
