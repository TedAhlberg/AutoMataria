package test;

import common.*;
import gameclient.Game;
import gameobjects.pickups.*;
import gameserver.GameServer;

import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class StartTestGame {
    public static void main(String[] args) {
        // Create a map
        GameMap map = Maps.getInstance().get("Small Map 1");
        System.out.println(map);
        SpecialGameObject[] gameMapObjects = new SpecialGameObject[4];
        gameMapObjects[0] = new SpecialGameObject(new SlowEnemiesPickup(200, 200, 65), 10000, 0, true, 15000);
        gameMapObjects[1] = new SpecialGameObject(new SpeedEnemiesPickup(200, 200, 65), 10000, 0, true, 15000);
        gameMapObjects[2] = new SpecialGameObject(new SelfSlowPickup(200, 200, 60), 10000, 0, true, 15000);
        gameMapObjects[3] = new SpecialGameObject(new SelfSpeedPickup(200, 200, 60), 1000, 0, true, 15000);
        map.setGameMapObjects(gameMapObjects);

        // Start a game server
        new GameServer("AM-test-server", 32000, 100, 2, 50, map);

        // Start a game client
        new Game("127.0.0.1", 32000, new Dimension(1000, 1000), 60);
        // new Game("127.0.0.1", 32000, new Dimension(1000, 1000), 30);
        // new Game("127.0.0.1", 32000, 100);
    }
}
