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
        GameServerSettings settings = new GameServerSettings();
        settings.name = "StartTestGameServer";
        settings.port = 32000;
        settings.tickRate = 75;
        settings.amountOfTickBetweenUpdates = 2;
        settings.playerSpeed = Game.GRID_PIXEL_SIZE / 2;
        settings.mapPool = new String[]{map.getName()};

        GameServer server = new GameServer(settings);
        server.start();
        server.changeMap(map);

        // Start a game client
        SwingUtilities.invokeLater(() -> {
            UserInterface userInterface = new UserInterface();
            userInterface.changeScreen("GameScreen");
            userInterface.startGame("127.0.0.1", 32000);
        });
    }
}
