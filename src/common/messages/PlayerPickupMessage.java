package common.messages;

import gameobjects.Pickup;
import gameobjects.Player;

/**
 * @author eriklundow
 */
public class PlayerPickupMessage extends Message {
    private static final long serialVersionUID = 1L;

    private Player player;
    private Pickup pickup;
    private Event event;

    public PlayerPickupMessage(Event event, Player player, Pickup pickup) {
        this.event = event;
        this.player = player;
        this.pickup = pickup;
    }

    public Player getPlayer() {
        return player;
    }

    public Event getEvent() {
        return event;
    }

    public Pickup getPickup() {
        return pickup;
    }

    public enum Event {
        PickupTaken,
        PickupInUse,
        PickupUsed
    }
}
