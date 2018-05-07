package test;

import common.*;
import gameclient.Game;
import gameclient.interfaces.UserInterface;
import gameobjects.pickups.*;
import gameserver.GameServer;

import javax.swing.*;

/**
 * @author Johannes Blüml
 */
public class StartTestGame {
    public static void main(String[] args) {
        GameMap map = Maps.getInstance().get("Normal Map 1");
        // Change GameObjects on the map
        SpecialGameObject[] gameMapObjects = {
                new SpecialGameObject(new SlowEnemiesPickup(200, 200, 4000), 10000, 0, true, 15000),
                new SpecialGameObject(new SpeedEnemiesPickup(200, 200, 4000), 10000, 0, true, 15000),
                new SpecialGameObject(new ReversePickup(200, 200, 4000), 10000, 0, true, 15000),
                new SpecialGameObject(new SelfSlowPickup(200, 200, 4000), 10000, 0, true, 15000),
                new SpecialGameObject(new SelfSpeedPickup(200, 200, 4000), 1000, 0, true, 15000),
                new SpecialGameObject(new InvinciblePickup(200, 200, 4000), 1000, 0, true, 15000)
        };
        map.setGameMapObjects(gameMapObjects);

        // Start a game server
        GameServer server = new GameServer("StartTestGameServer", 32000, 75, 2, Game.GRID_PIXEL_SIZE / 2, map);
        server.start();

        // Start a game client
        SwingUtilities.invokeLater(() -> {
            UserInterface userInterface = new UserInterface();
            userInterface.changeScreen("GameScreen");
            userInterface.startGame("127.0.0.1", 32000);
        });
    }
}
