package common;

import gameclient.Game;
import gameobjects.*;

import java.awt.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Johannes Blüml
 */
public class GameMap implements Serializable {
    private String name = "default";
    private String musicTrack;
    private Dimension grid = new Dimension(50,50);
    private int players = 4, gridMultiplier = Game.GRID_PIXEL_SIZE, playerSpeed = (grid.width * gridMultiplier) / 5;
    private ConcurrentLinkedQueue<GameObject> gameObjects = new ConcurrentLinkedQueue<>();
    private String background = "resources/Stars.png";
    private int[][] startPositions;
    private Color[] playerColors = new Color[]{
            new Color(0xff148c),
            new Color(0xc8ff32),
            new Color(0x1e96ff),
            new Color(0xff6400)
    };
    private int currentPlayers, serverTickrate;

    public GameMap(String name) {
        this.name = name;
        generateStartPositions();
    }

    public void generateStartPositions() {
        int p1x = (grid.width / 2) / 2;
        int p1y = (grid.height / 2) / 2;

        int p2x = p1x;
        int p2y = (grid.height / 2) + p1y;

        int p3x = (grid.width / 2) + p1x;
        int p3y = p1y;

        int p4x = (grid.width / 2) + p1x;
        int p4y = p2y;

        startPositions = new int[players][2];
        startPositions[0][0] = p1x;
        startPositions[0][1] = p1y;
        startPositions[1][0] = p2x;
        startPositions[1][1] = p2y;
        startPositions[2][0] = p3x;
        startPositions[2][1] = p3y;
        startPositions[3][0] = p4x;
        startPositions[3][1] = p4y;
    }

    public void setEdgeWalls(Color color) {
        gameObjects.add(new Wall(0, 0, gridMultiplier, getWidth(), color));
        gameObjects.add(new Wall(getHeight() - gridMultiplier, 0, gridMultiplier, getWidth(), color));
        gameObjects.add(new Wall(0, 0, getHeight(), gridMultiplier, color));
        gameObjects.add(new Wall(0, getHeight() - gridMultiplier, getHeight(), gridMultiplier, color));
    }

    public Collection<GameObject> getGameObjects() {
        return gameObjects;
    }

    public Player newPlayer(String name) {
        if (currentPlayers >= players) return null;
        int[] pos = startPositions[currentPlayers];
        Player player = new Player(pos[0] * gridMultiplier, pos[1] * gridMultiplier, name, playerColors[currentPlayers], this);
        currentPlayers += 1;
        player.setSpeed(playerSpeed);
        gameObjects.add(player);
        return player;
    }

    public String getName() {
        return name;
    }

    public void setGrid(Dimension grid) {
        this.grid = grid;
    }

    public Dimension getGrid() {
        return grid;
    }

    public int getWidth() {
        return grid.width * gridMultiplier;
    }

    public int getHeight() {
        return grid.height * gridMultiplier;
    }

    public String getBackground() {
        return background;
    }

    public void add(GameObject object) {
        gameObjects.add(object);
    }

    /**
     * @param playerSpeed How many grid-positions to move each tick
     */
    public void setPlayerSpeed(double playerSpeed) {
        this.playerSpeed = (int) Math.round(gridMultiplier * playerSpeed);
    }

    public void setServerTickRate(int tickRate) {
        this.serverTickrate = tickRate;
    }

    public int getPlayerSpeedPerSecond() {
        return (1000 / serverTickrate) * playerSpeed;
    }

    public int getPlayerSpeed() {
        return playerSpeed;
    }

    public int getGridMultiplier() {
        return gridMultiplier;
    }
}
