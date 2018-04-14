package common;

import gameclient.Game;

import java.awt.*;
import java.io.Serializable;

/**
 * @author Johannes Blüml
 */
public class GameMap implements Serializable {
    private static final long serialVersionUID = 1;
    private String name, background, musicTrack;
    private int players, playerSpeed;
    private Dimension grid;
    private Point[] startingPositions;
    private SpecialGameObject[] gameMapObjects;

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

    public int getPlayerSpeed() {
        return playerSpeed;
    }

    public void setPlayerSpeed(int playerSpeed) {
        this.playerSpeed = playerSpeed;
    }

    public void setPlayerSpeed(double playerSpeed) {
        this.playerSpeed = (int) Math.round(Game.GRID_PIXEL_SIZE * playerSpeed);
    }

    public Dimension getGrid() {
        return grid;
    }

    public void setGrid(Dimension grid) {
        this.grid = grid;
    }

    public Point[] getStartingPositions() {
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
