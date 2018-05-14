package gameobjects;

import common.PickupState;

import java.util.Collection;

public abstract class InstantPickup extends Pickup {
    private static final long serialVersionUID = 1;

    public InstantPickup() {
        this(0, 0);
    }

    public InstantPickup(int x, int y) {
        super(x, y);
    }

    public void tick() {
        if (state != PickupState.InUse) return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= activeTime) {
            done();
        }
    }

    /**
     * InstantPickups don't reset the players slot so we need to override the done() method
     */
    public void done() {
        if (state == PickupState.InUse) complete();
        state = PickupState.Used;
        gameObjects.remove(this);
    }

    /**
     * InstantPickups cant be taken - this method does nothing
     */
    public void take(Player player, Collection<GameObject> gameObjects) {}

    /**
     * Instant pickups can be used by a player if and only if the pickup is in NotTaken state
     *
     * @param player      The player that uses the pickup
     * @param gameObjects Collection of all GameObjects
     */
    public void use(Player player, Collection<GameObject> gameObjects) {
        if (state != PickupState.NotTaken) return;
        setState(PickupState.InUse);

        this.player = player;
        this.gameObjects = gameObjects;

        startTime = System.currentTimeMillis();
        start();
    }
}
