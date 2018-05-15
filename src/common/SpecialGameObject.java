package common;

import gameobjects.GameObject;

import java.io.Serializable;

/**
 * Contains a GameObject and adds extra settings for it.
 * So it can spawn in intervals and only be visible for a limited time.
 *
 * @author Dante Håkansson
 * @author Johannes Bluml
 */
public class SpecialGameObject implements Serializable {
    public static final long serialVersionUID = 1;
    private GameObject gameObject;
    private int spawnInterval;
    private int spawnLimit;
    private boolean spawnRandom;
    private int visibleTime;
    transient private int timer;
    transient private int timesSpawned;

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

    public void incrementTimesSpawned() {
        this.timesSpawned += 1;
    }

    public int getTimesSpawned() {
        return timesSpawned;
    }

    public String toString() {
        return gameObject.getClass().getName();
    }

    public void reset() {
        timesSpawned = 0;
        timer = 0;
    }
}
