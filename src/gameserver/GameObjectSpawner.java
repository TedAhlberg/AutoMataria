package gameserver;

import common.*;
import gameclient.Game;
import gameobjects.GameObject;
import gameobjects.Pickup;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Spawns game objects from the map into the gameObjects collection.
 *
 * @author Dante Håkansson
 * @author Johannes Blüml
 */

public class GameObjectSpawner {

    private ConcurrentLinkedQueue<GameObject> gameObjects;
    private GameMap currentMap;
    private int tickRate;

    public GameObjectSpawner(ConcurrentLinkedQueue<GameObject> gameObjects, GameMap currentMap, int tickRate) {
        this.gameObjects = gameObjects;
        this.currentMap = currentMap;
        this.tickRate = tickRate;
    }

    /**
     * Iterate over all gameobjects from the current map
     * if the gameobject should be added it has to be instantiated and have a new ID set
     */
    public void tick() {
        if (currentMap.getGameMapObjects() == null)
            return;
        for (SpecialGameObject gameMapObject : currentMap.getGameMapObjects()) {
            GameObject gameObject = gameMapObject.getGameObject();
            if (gameMapObject.getSpawnInterval() == 0) {
                if (!gameObjects.contains(gameObject)) {
                    if (Utility.intersectsNoGameObject(gameObject.getBounds(), gameObjects)) {
                        int quarterGridPixel = Game.GRID_PIXEL_SIZE / 4;
                        gameObject.setX(gameObject.getX() - quarterGridPixel);
                        gameObject.setY(gameObject.getY() - quarterGridPixel);
                        gameObject.setId(ID.getNext());
                        gameObjects.add(gameObject);
                        System.out.println("Placing on map (Instant): " + gameObject + " Position: "
                                + new Point(gameObject.getX(), gameObject.getY()));
                    }
                }
                continue;
            }
            if (gameMapObject.getTimer() <= 0) {
                if (gameObjects.contains(gameObject)) {
                    if (gameObject instanceof Pickup) {
                        if (!((Pickup) gameObject).isTaken()) {
                            System.out.println("Removing from map (Timed out): " + gameObject + " Position: "
                                    + new Point(gameObject.getX(), gameObject.getY()));
                            gameObjects.remove(gameMapObject.getGameObject());
                            gameMapObject.setTimer(gameMapObject.getSpawnInterval());
                        }
                    } else {
                        gameObjects.remove(gameMapObject.getGameObject());
                        gameMapObject.setTimer(gameMapObject.getSpawnInterval());
                    }
                } else {
                    if (gameMapObject.isSpawnRandom()) {
                        Point point = Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects);
                        int quarterGridPixel = Game.GRID_PIXEL_SIZE / 4;
                        gameObject.setX(point.x - quarterGridPixel);
                        gameObject.setY(point.y - quarterGridPixel);
                    }
                    gameObject.setId(ID.getNext());
                    gameObjects.add(gameObject);
                    gameMapObject.setTimer(gameMapObject.getVisibleTime());
                    System.out.println("Placing on map (Spawn time): " + gameObject + " Position: "
                            + new Point(gameObject.getX(), gameObject.getY()));
                }
            } else {
                gameMapObject.setTimer(gameMapObject.getTimer() - tickRate);
            }
        }
    }

    private GameObject getNewGameObject(GameObject gameObject) {
        try {
            return gameObject.getClass().getConstructor(gameObject.getClass()).newInstance(gameObject);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException
                | IllegalAccessException e) {
            return null;
        }
    }
}
