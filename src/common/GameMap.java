package common;

import java.awt.*;
import java.io.Serializable;

/**
 * Represents a Map in the game.
 *
 * @author Johannes Blüml
 */
public class GameMap implements Serializable {
    private static final long serialVersionUID = 1;
    private String name, background, musicTrack;
    private int players;
    private double playerSpeedMultiplier;
    private Dimension grid;
    private Point[] startingPositions;
    private SpecialGameObject[] gameMapObjects;

    public GameMap() {
    }

    public GameMap(String name, String background, String musicTrack, int players, double playerSpeedMultiplier, Dimension grid, Point[] startingPositions, SpecialGameObject[] gameMapObjects) {
        this.name = name;
        this.background = background;
        this.musicTrack = musicTrack;
        this.players = players;
        this.playerSpeedMultiplier = playerSpeedMultiplier;
        this.grid = grid;
        this.startingPositions = startingPositions;
        this.gameMapObjects = gameMapObjects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getMusicTrack() {
        return musicTrack;
    }

    public void setMusicTrack(String musicTrack) {
        this.musicTrack = musicTrack;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public double getPlayerSpeedMultiplier() {
        return playerSpeedMultiplier;
    }

    public void setPlayerSpeedMultiplier(double playerSpeedMultiplier) {
        this.playerSpeedMultiplier = playerSpeedMultiplier;
    }

    public Dimension getGrid() {
        return new Dimension(grid);
    }

    public void setGrid(Dimension grid) {
        this.grid = grid;
    }

    public Point[] getStartingPositions() {
        if (startingPositions == null) {
            return new Point[0];
        }
        return startingPositions;
    }

    public void setStartingPositions(Point[] startingPositions) {
        this.startingPositions = startingPositions;
    }

    public SpecialGameObject[] getGameMapObjects() {
        if (gameMapObjects == null) {
            return new SpecialGameObject[0];
        }
        return gameMapObjects;
    }

    public void setGameMapObjects(SpecialGameObject[] gameMapObjects) {
        this.gameMapObjects = gameMapObjects;
    }

}
