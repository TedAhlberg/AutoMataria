package common.messages;

/**
 * @author Johannes Blüml
 */
public class ReadyPlayersMessage extends Message {
    private static final long serialVersionUID = 1L;

    private int ready, totalPlayers;

    public ReadyPlayersMessage(int ready, int totalPlayers) {
        this.ready = ready;
        this.totalPlayers = totalPlayers;
    }

    public int getReadyPlayerCount() {
        return ready;
    }

    public int getPlayerCount() {
        return totalPlayers;
    }
}
