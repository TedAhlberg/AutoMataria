package common;

import java.awt.Rectangle;
import java.io.Serializable;

import gameobjects.GameObject;

/**
 *
 * @author Dante H�kansson
 * @author Johannes Bl�ml
 *
 */

public class SpecialGameObject implements Serializable {

    private GameObject pickup;
    private int spawnInterval;
    private int spawnLimit;
    private boolean spawnRandom;
    private int visibletime;
    private int timer;

    public SpecialGameObject(GameObject pickup, int spawnInterval, int spawnLimit,
            boolean spawnRandom, int visibletime) {

        this.pickup = pickup;
        this.spawnInterval = spawnInterval;
        this.spawnLimit = spawnLimit;
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
