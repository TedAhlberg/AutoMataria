package common.messages;

import gameobjects.Player;

public class PlayerPickupMessage extends Message {
    
    private Player player;
    private Event event;
    public enum Event{
        PickUpTaken,
        PickUpUsed
    }
    
    public PlayerPickupMessage(Event event, Player player) {
        this.event=event;
        this.player=player;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Event getEvent() {
        return event;
    }

}
