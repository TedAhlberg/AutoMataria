package test;

import common.GameMap;
import common.GameMapObject;
import gameclient.Game;
import gameobjects.GameObject;
import gameobjects.Wall;
import gameserver.GameServer;

import java.awt.*;

import PickUp.SelfSpeedUpPickUp;

/**
 * @author Johannes Bluml
 */
public class StartTestGame {
    public static void main(String[] args) {
        // Create a map
        GameMap map = new GameMap();
        map.setBackground("resources/Stars.png");
        map.setMusicTrack("resources/Music/AM-trck1.mp3");
        map.setPlayerSpeed(0.25);
        map.setPlayers(5);
        map.setGrid(new Dimension(50, 50));
        Wall wall = new Wall();
        int width = map.getGrid().width * Game.GRID_PIXEL_SIZE;
        int height = map.getGrid().height * Game.GRID_PIXEL_SIZE;
        wall.add(new Rectangle(0, 0, Game.GRID_PIXEL_SIZE, width));
        wall.add(new Rectangle(height - Game.GRID_PIXEL_SIZE, 0, Game.GRID_PIXEL_SIZE, width));
        wall.add(new Rectangle(0, 0, height, Game.GRID_PIXEL_SIZE));
        wall.add(new Rectangle(0, height - Game.GRID_PIXEL_SIZE, height, Game.GRID_PIXEL_SIZE));
        GameObject[] startingObjects = {wall};
        map.setStartingGameObjects(startingObjects);
        GameMapObject[] gameMapObjects = new GameMapObject[1];
        gameMapObjects[0] = new GameMapObject(new SelfSpeedUpPickUp(200, 200, 0, null), 5000, 0, null, false, 10000);
        map.setGameMapObjects(gameMapObjects);
        // Start a game server
        new GameServer("AM-test-server", 32000, 50, 150, map);

        // Start a game client
        new Game("127.0.0.1", 32000, new Dimension(1000, 1000), 30);
        new Game("127.0.0.1", 32000, new Dimension(1000, 1000), 30);

//        new Game("127.0.0.1", 32000, 100);
    }
}
