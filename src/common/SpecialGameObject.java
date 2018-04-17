package common;

import java.io.Serializable;

import gameobjects.GameObject;

/**
 *
 * @author Dante Håkansson
 * @author Johannes Bluml
 *
 */

public class SpecialGameObject implements Serializable {
    public static final long serialVersionUID = 1;
    private GameObject gameObject;
    private int spawnInterval;
    private int spawnLimit;
    private boolean spawnRandom;
    private int visibleTime;
    private int timer;

    public SpecialGameObject(GameObject gameObject) {
        this(gameObject, 0, 0, false, 0);
    }

    public SpecialGameObject(GameObject gameObject, int spawnInterval, int spawnLimit, boolean spawnRandom, int visibleTime) {
        this.gameObject = gameObject;
        this.spawnInterval = spawnInterval;
        this.spawnLimit = spawnLimit;
        this.spawnRandom = spawnRandom;
        this.visibleTime = visibleTime;
    }

    public GameObject getGameObject() {
        return gameObject;
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

    public int getVisibleTime() {
        return visibleTime;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public String toString() {
        return gameObject.getClass().getName();
    }
}
