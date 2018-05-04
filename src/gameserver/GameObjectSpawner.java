package gameserver;

import common.*;
import gameclient.Game;
import gameobjects.GameObject;
import gameobjects.Pickup;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Spawns game objects from the map into the gameObjects collection.
 *
 * @author Dante Håkansson
 * @author Johannes Blüml
 */
public class GameObjectSpawner {
    private HashMap<SpecialGameObject, GameObject> spawnedObjects = new HashMap<>();
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
        if (currentMap.getGameMapObjects() == null) return;

        // Removes removed gameobject from spawnedObjects HashMap so it can be placed om map again
        spawnedObjects.values().removeIf(gameObject -> !gameObjects.contains(gameObject));

        for (SpecialGameObject specialObject : currentMap.getGameMapObjects()) {
            if (specialObject.getSpawnInterval() == 0) {
                if (!spawnedObjects.containsKey(specialObject)) {
                    GameObject gameObject = getCopyOfGameObject(specialObject.getGameObject());
                    if (gameObject == null) {
                        System.out.println("ERROR: Can't find GameObject " + specialObject.getGameObject().getClass().getSimpleName());
                        continue;
                    }
                    gameObject.setId(ID.getNext());
                    if (gameObject instanceof Pickup) {
                        int quarterGridPixel = Game.GRID_PIXEL_SIZE / 4;
                        gameObject.setX(gameObject.getX() - quarterGridPixel);
                        gameObject.setY(gameObject.getY() - quarterGridPixel);
                    }
                    gameObjects.add(gameObject);
                    spawnedObjects.put(specialObject, gameObject);
                    System.out.println("Placing on map (Instant): " + gameObject + " Position: "
                            + new Point(gameObject.getX(), gameObject.getY()));
                }
                continue;
            }
            if (specialObject.getTimer() <= 0) {
                if (spawnedObjects.containsKey(specialObject)) {
                    GameObject gameObject = spawnedObjects.get(specialObject);
                    if (gameObject instanceof Pickup) {
                        PickupState state = ((Pickup) gameObject).getState();
                        if (state == PickupState.NotTaken) {
                            System.out.println("Removing from map (Timed out): " + gameObject + " Position: "
                                    + new Point(gameObject.getX(), gameObject.getY()));
                            gameObjects.remove(gameObject);
                            spawnedObjects.remove(specialObject);
                            specialObject.setTimer(specialObject.getSpawnInterval());
                        }
                    } else {
                        gameObjects.remove(gameObject);
                        spawnedObjects.remove(specialObject);
                        specialObject.setTimer(specialObject.getSpawnInterval());
                    }
                } else {
                    GameObject gameObject = getCopyOfGameObject(specialObject.getGameObject());
                    if (gameObject == null) {
                        System.out.println("ERROR: Can't find copy constructor in GameObject " + specialObject.getGameObject().getClass().getSimpleName());
                        continue;
                    }
                    if (specialObject.isSpawnRandom()) {
                        Point point = Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects);
                        int quarterGridPixel = Game.GRID_PIXEL_SIZE / 4;
                        gameObject.setX(point.x - quarterGridPixel);
                        gameObject.setY(point.y - quarterGridPixel);
                    }
                    gameObject.setId(ID.getNext());
                    gameObjects.add(gameObject);
                    spawnedObjects.put(specialObject, gameObject);
                    specialObject.setTimer(specialObject.getVisibleTime());
                    System.out.println("Placing on map (Spawn time): " + gameObject + " Position: "
                            + new Point(gameObject.getX(), gameObject.getY()));
                }
            } else {
                specialObject.setTimer(specialObject.getTimer() - tickRate);
            }
        }
    }

    /**
     * Creates a copy of the provided GameObject
     *
     * @param gameObject The GameObject you want to copy
     * @return A copy of the provided GameObject
     */
    private GameObject getCopyOfGameObject(GameObject gameObject) {
        try {
            return gameObject.getClass().getConstructor(gameObject.getClass()).newInstance(gameObject);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException
                | IllegalAccessException e) {
            return null;
        }
    }
}
