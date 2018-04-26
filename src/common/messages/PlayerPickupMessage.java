package common.messages;

import gameobjects.Pickup;
import gameobjects.Player;
/**
 * 
 * @author eriklundow
 *
 */
public class PlayerPickupMessage extends Message {
    
    private Player player;
    private Pickup pickup;
    private Event event;
    public enum Event{
        PickUpTaken,
        PickUpUsed
    }
    
    public PlayerPickupMessage(Event event, Player player, Pickup pickup) {
        this.event=event;
        this.player=player;
        this.pickup=pickup;
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

}
