package test;

import common.*;
import gameclient.Game;
import gameclient.Window;
import gameclient.interfaces.GameScreen;
import gameclient.interfaces.UserInterface;
import gameobjects.pickups.*;
import gameserver.GameServer;

import javax.swing.*;
import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class StartTestGame {
    public static void main(String[] args) {
        // Create a map
        GameMap map = Maps.getInstance().get("Small Map 1");
        System.out.println(map);
        SpecialGameObject[] gameMapObjects = new SpecialGameObject[6];
        gameMapObjects[0] = new SpecialGameObject(new SlowEnemiesPickup(200, 200, 65), 10000, 0, true, 15000);
        gameMapObjects[1] = new SpecialGameObject(new SpeedEnemiesPickup(200, 200, 65), 10000, 0, true, 15000);
        gameMapObjects[2] = new SpecialGameObject(new ReversePickup(200, 200, 65), 10000, 0, true, 15000);
        gameMapObjects[3] = new SpecialGameObject(new SelfSlowPickup(200, 200, 60), 10000, 0, true, 15000);
        gameMapObjects[4] = new SpecialGameObject(new SelfSpeedPickup(200, 200, 60), 1000, 0, true, 15000);
        gameMapObjects[5] = new SpecialGameObject(new InvinciblePickup(200, 200, 65), 1000, 0, true, 15000);

        map.setGameMapObjects(gameMapObjects);

        // Start a game server
        GameServer server = new GameServer("AM-test-server 1", 32000, 100, 2, Game.GRID_PIXEL_SIZE / 2, map);
        server.start();

        // Start a game client
        SwingUtilities.invokeLater(() -> {
            UserInterface userInterface = new UserInterface();
            userInterface.changeScreen("GameScreen");
            userInterface.startGame("127.0.0.1", 32000);
        });
    }
}
