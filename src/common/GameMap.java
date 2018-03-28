package common;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Johannes Blüml
 */
public class GameMap implements Serializable {
    private String name = "default";
    private String musicTrack;
    private int players = 4, gridSize = 100, width = 50 * gridSize, height = 50 * gridSize, playerSpeed = gridSize / 4;
    private ConcurrentLinkedQueue<GameObject> gameObjects = new ConcurrentLinkedQueue<>();
    private String background = "resources/Stars.png";
    private int[][] startPositions;
    private Color[] playerColors = new Color[]{
            new Color(0xff148c),
            new Color(0xc8ff32),
            new Color(0x1e96ff),
            new Color(0xff6400)
    };
    private int currentPlayers = 0;

    public GameMap(String name) {
        this.name = name;
        generateStartPositions();
    }

    public void generateStartPositions() {
        int p1x = (width/2)/2;
        int p1y = (height/2)/2;

        int p2x = p1x;
        int p2y = (height/2)+p1y;

        int p3x = (width/2)+p1x;
        int p3y = p1y;

        int p4x = (width/2)+p1x;
        int p4y = p2y;

        startPositions = new int[players][2];
        startPositions[0][0] = (p1x / gridSize) * gridSize;
        startPositions[0][1] = (p1y / gridSize) * gridSize;
        startPositions[1][0] = (p2x / gridSize) * gridSize;
        startPositions[1][1] = (p2y / gridSize) * gridSize;
        startPositions[2][0] = (p3x / gridSize) * gridSize;
        startPositions[2][1] = (p3y / gridSize) * gridSize;
        startPositions[3][0] = (p4x / gridSize) * gridSize;
        startPositions[3][1] = (p4y / gridSize) * gridSize;
    }

    public void setEdgeWalls(Color color) {
        gameObjects.add(new Wall(0, 0, gridSize, width, "WALL", color));
        gameObjects.add(new Wall(height - gridSize, 0, gridSize, width, "WALL", color));
        gameObjects.add(new Wall(0, 0, height, gridSize, "WALL", color));
        gameObjects.add(new Wall(0, height - gridSize, height, gridSize, "WALL", color));
    }

    public Collection<GameObject> getGameObjects() {
        return gameObjects;
    }

    public int getPlayerSpeed() {
        return playerSpeed;
    }

    public Player newPlayer(String name) {
        if (currentPlayers >= players) return null;
        int[] pos = startPositions[currentPlayers];
        Player player = new Player(pos[0], pos[1], name, playerColors[currentPlayers], this);
        currentPlayers += 1;
        player.setSpeed(playerSpeed);
        gameObjects.add(player);
        return player;
    }

    public String getName() {
        return name;
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getBackground() {
        return background;
    }
}
