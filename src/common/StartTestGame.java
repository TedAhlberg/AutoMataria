package common;

import gameclient.*;
import gameserver.*;

import java.awt.*;

/**
 * @author Johannes Bluml
 */
public class StartTestGame {
    public static void main(String[] args) {
        // Create a map
        GameMap map = new GameMap("default");
        map.setEdgeWalls(Color.CYAN.darker().darker());

        // Start a game server
        new GameServer(32000, 40, 100, 4, map);

        // Start a game client
        new Game();
    }
}
