package common.messages;

import java.awt.Color;

import gameobjects.Player;

public class PlayerMessage extends Message{

    /**
     * author @Erik Lundow
     */
    private static final long serialVersionUID = 1L;
    private Event event;
    private Player player;
    
    public enum Event{
        Connected,
        Disconnected,
        Ready,
        Unready,
        ColorChange,
        Crashed
    }
    public PlayerMessage(Event event,Player player) {
        this.event=event;
        this.player=player;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public Color getPlayerColor() {
        return player.getColor();
    }
    

    
    
}
