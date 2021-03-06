package common.messages;

import gameobjects.Player;

/**
 * @author eriklundow
 */
public class PlayerMessage extends Message {
    private static final long serialVersionUID = 1L;

    private Event event;
    private Player player;

    public PlayerMessage(Event event, Player player) {
        this.event = event;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Event getEvent() {
        return event;
    }

    public enum Event {
        Connected,
        Disconnected,
        Ready,
        Unready,
        ColorChange,
        Crashed,
        Moved
    }
}
