package common;

import gameclient.Game;
import gameobjects.*;

import java.awt.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author Johannes Blüml
 */
public class GameMap implements Serializable {
    private String name = "default", background = "resources/Stars.png", musicTrack = "AM-trck1.mp3";
    private Dimension grid = new Dimension(50, 50);
    private int players = 5, gridMultiplier = Game.GRID_PIXEL_SIZE, playerSpeed = (grid.width * gridMultiplier) / 5;
    private LinkedList<GameObject> gameObjects = new LinkedList<>();
    private LinkedList<Point> startingPositions = new LinkedList<>();
    private Color[] playerColors = new Color[]{
            new Color(0xc8ff32),
            new Color(0xff148c),
            new Color(0x1e96ff),
            new Color(0xff6400),
            new Color(0x1cb874),
            new Color(0x8147ff),
            new Color(0xd3d439),
            new Color(0x23D820)
    };
    private int currentPlayers, serverTickrate;

    public GameMap() {
        generateStartPositions();
    }

    public GameMap(String name) {
        this.name = name;
        generateStartPositions();
    }

    public void generateStartPositions() {
        if (players > 5) {
            generateRandomStartPositions();
            return;
        }

        int xLeft = (grid.width / 2) / 2;
        int xRight = (grid.width / 2) + xLeft;
        int yTop = (grid.height / 2) / 2;
        int yBottom = (grid.height / 2) + yTop;
        int xCenter = grid.width / 2;
        int yCenter = grid.height / 2;

        startingPositions.add(new Point(xLeft, yTop));
        startingPositions.add(new Point(xRight, yBottom));
        startingPositions.add(new Point(xRight, yTop));
        startingPositions.add(new Point(xLeft, yBottom));
        startingPositions.add(new Point(xCenter, yCenter));
    }

    public void generateRandomStartPositions() {
        Random random = new Random();

        while (startingPositions.size() < players) {
            int x = random.nextInt(grid.width - 2) + 1;
            int y = random.nextInt(grid.height - 2) + 1;
            Point point = new Point(x, y);
            if (!startingPositions.contains(point)) {
                startingPositions.add(point);
            }
        }
    }

    public void addEdgeWalls(Color color) {
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
        Point position = startingPositions.get(currentPlayers);
        Player player = new Player(position.x * gridMultiplier, position.y * gridMultiplier, name, playerColors[currentPlayers], this);
        currentPlayers += 1;
        player.setSpeed(playerSpeed);
        gameObjects.add(player);
        return player;
    }

    public void add(GameObject object) {
        gameObjects.add(object);
    }

    public String getName() {
        return name;
    }

    public Dimension getGrid() {
        return grid;
    }

    public void setGrid(Dimension grid) {
        this.grid = grid;
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

    public void setServerTickRate(int tickRate) {
        this.serverTickrate = tickRate;
    }

    /**
     * @return Player speed per second represented as pixels
     */
    public int getPlayerSpeedPerSecond() {
        return (1000 / serverTickrate) * playerSpeed;
    }

    /**
     * @param playerSpeed How many grid-positions to move each tick
     */
    public void setPlayerSpeed(double playerSpeed) {
        this.playerSpeed = (int) Math.round(gridMultiplier * playerSpeed);
    }

    public int getGridMultiplier() {
        return gridMultiplier;
    }
}
