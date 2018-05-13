package common.messages;

import common.GameMap;
import gameobjects.Player;

/**
 * @author Johannes Blüml
 */
public class ConnectionMessage extends Message {
    private static final long serialVersionUID = 1L;

    public boolean success;
    public String serverName;
    public GameMap currentMap;
    public int tickRate, updateRate, roundLimit, scoreLimit;
    public Player player;

    public ConnectionMessage(String serverName, GameMap currentMap, int tickRate, int updateRate, int roundLimit, int scoreLimit, Player player) {
        this.serverName = serverName;
        this.currentMap = currentMap;
        this.tickRate = tickRate;
        this.updateRate = updateRate;
        this.roundLimit = roundLimit;
        this.scoreLimit = scoreLimit;
        this.player = player;
        this.success = true;
    }

    public ConnectionMessage() {
        this.success = false;
    }
}
