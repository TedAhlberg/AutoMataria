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
        map.setPlayerSpeed(0.25);
        map.addEdgeWalls();

        // Start a game server
        new GameServer(32000, 50, 150, 4, map);

        // Start a game client
        new Game("127.0.0.1", 32000, new Dimension(1000, 1000), 60);
//        new Game("127.0.0.1", 32000, new Dimension(1000, 1000), 30);
        //new Game("127.0.0.1", 32000, 100);
    }
}
