package common;

import gameobjects.Player;

import java.io.Serializable;

/**
 * @author Johannes Blüml
 */
public class ConnectionMessage implements Serializable {
    public boolean success;
    public GameMap currentMap;
    public int tickRate, updateRate;
    public Player player;

    public ConnectionMessage(GameMap currentMap, int tickRate, int updateRate, Player player) {
        this.currentMap = currentMap;
        this.tickRate = tickRate;
        this.updateRate = updateRate;
        this.player = player;
        this.success = true;
    }

    public ConnectionMessage() {
        this.success = false;
    }
}
