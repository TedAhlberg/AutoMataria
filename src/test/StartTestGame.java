package test;

import common.*;
import gameclient.interfaces.UserInterface;
import gameobjects.pickups.*;
import gameserver.GameServer;

import javax.swing.*;

/**
 * @author Johannes Blüml
 */
public class StartTestGame {
    public static void main(String[] args) {
        GameMap map = Maps.getInstance().get("CLEAN");
        // Change GameObjects on the map
        SpecialGameObject[] gameMapObjects = {
                new SpecialGameObject(new SlowEnemiesPickup(200, 200, 4000), 1000, 0, true, 25000),
                new SpecialGameObject(new SpeedEnemiesPickup(200, 200, 4000), 1000, 0, true, 25000),
                new SpecialGameObject(new ReversePickup(200, 200, 4000), 1000, 0, true, 25000),
                new SpecialGameObject(new SelfSlowPickup(200, 200, 4000), 1000, 0, true, 25000),
                new SpecialGameObject(new SelfSpeedPickup(200, 200, 4000), 1000, 0, true, 25000),
                new SpecialGameObject(new InvinciblePickup(200, 200, 4000), 1000, 0, true, 25000)
        };
        map.setGameMapObjects(gameMapObjects);

        // Start a game server
        GameServerSettings settings = new GameServerSettings();
        settings.name = "StartTestGameServer";
        settings.port = 32000;
        settings.tickRate = 50;
        settings.amountOfTickBetweenUpdates = 2;
        settings.playerSpeed = Game.GRID_PIXEL_SIZE / 2;
        settings.mapPool = new String[]{map.getName()};

        GameServer server = new GameServer(settings, event -> {
            if (event == GameServer.Event.Stopped) {
                System.exit(0);
            } else if (event == GameServer.Event.Started) {
                // Start a game client
                SwingUtilities.invokeLater(() -> {
                    UserInterface userInterface = new UserInterface();
                    userInterface.changeScreen("GameScreen");
                    userInterface.startGame("127.0.0.1", 32000);
                });
            }
        });
        server.start();
        server.changeMap(map);
    }
}
