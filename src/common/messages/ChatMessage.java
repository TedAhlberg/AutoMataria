package common.messages;

import gameobjects.Player;

/**
 * @author Johannes Blüml
 */
public class ChatMessage extends Message {
    private static final long serialVersionUID = 1L;

    public final String message;
    public final Player player;

    public ChatMessage(String message, Player player) {
        this.message = message;
        this.player = player;
    }
}
