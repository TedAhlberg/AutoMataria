package common;

import java.awt.Rectangle;
import java.io.Serializable;

import gameobjects.GameObject;

/**
 *
 * @author Dante Håkansson
 * @author Johannes Blüml
 *
 */

public class GameMapObject implements Serializable {

    private GameObject pickup;
    private int spawnInterval;
    private int spawnLimit;
    private Rectangle spawnPosition;
    private boolean spawnRandom;
    private int visibletime;
    private int timer;

    public GameMapObject(GameObject pickup, int spawnInterval, int spawnLimit, Rectangle spawnPosition,
            boolean spawnRandom, int visibletime) {

        this.pickup = pickup;
        this.spawnInterval = spawnInterval;
        this.spawnLimit = spawnLimit;
        this.spawnPosition = spawnPosition;
        this.spawnRandom = spawnRandom;
        this.visibletime = visibletime;
    }

    public GameObject getPickup() {
        return pickup;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    public int getSpawnLimit() {
        return spawnLimit;
    }

    public Rectangle getSpawnPosition() {
        return spawnPosition;
    }

    public boolean isSpawnRandom() {
        return spawnRandom;
    }

    public int getVisibletime() {
        return visibletime;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

}
