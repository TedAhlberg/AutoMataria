package gameserver;

import common.*;
import gameclient.Game;
import gameobjects.GameObject;
import gameobjects.Pickup;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Spawns game objects from the map into the gameObjects collection.
 *
 * @author Dante Håkansson
 * @author Johannes Blüml
 */
public class GameObjectSpawner {
    private final HashMap<SpecialGameObject, GameObject> spawnedObjects = new HashMap<>();
    private final Collection<GameObject> gameObjects;
    private final int tickRate;
    private GameMap currentMap;

    public GameObjectSpawner(Collection<GameObject> gameObjects, GameMap map, int tickRate) {
        this.gameObjects = gameObjects;
        this.currentMap = map;
        this.tickRate = tickRate;
    }

    /**
     * Clears all spawned GameObjects and changes the map
     * so the next tick it will spawn the GameObject from the new map
     *
     * @param map The GameMap to change to
     */
    public void changeMap(GameMap map) {
        currentMap = map;
        reset();
    }

    /**
     * Resets the spawned objects (Usually at start of new round)
     */
    public void reset() {
        spawnedObjects.forEach((specialObject, gameObject) -> {
            if (gameObject instanceof Pickup && ((Pickup) gameObject).getState() == PickupState.InUse) {
                ((Pickup) gameObject).done();
            }
            gameObjects.remove(gameObject);
        });
        SpecialGameObject[] gameMapObjects = currentMap.getGameMapObjects();
        Arrays.stream(gameMapObjects).forEach(SpecialGameObject::reset);
        spawnedObjects.clear();
    }

    /**
     * Iterate over all gameobjects from the current map
     * if the gameobject should be added it has to be instantiated and have a new ID set
     */
    public void tick() {
        if (currentMap.getGameMapObjects() == null) return;

        // Removes removed gameobject from spawnedObjects HashMap so they can be placed om map again
        spawnedObjects.values().removeIf(gameObject -> !gameObjects.contains(gameObject));

        for (SpecialGameObject specialObject : currentMap.getGameMapObjects()) {
            boolean isSpawnRandom = specialObject.isSpawnRandom();
            boolean isSpawnInstantly = !isSpawnRandom && specialObject.getSpawnInterval() == 0;
            boolean isTimerExpired = !isSpawnInstantly && specialObject.getTimer() <= 0;
            boolean isSpawned = spawnedObjects.containsKey(specialObject);
            boolean hasSpawnLimit = specialObject.getSpawnLimit() > 0;

            if (!isSpawned && hasSpawnLimit) {
                int spawnsLeft = specialObject.getSpawnLimit() - specialObject.getTimesSpawned();
                if (spawnsLeft <= 0) continue;
            }

            GameObject gameObject = isSpawned ? spawnedObjects.get(specialObject) : getCopyOfGameObject(specialObject.getGameObject());

            if (gameObject == null) continue;

            if (!isSpawned && (isSpawnInstantly || isTimerExpired)) {
                spawn(specialObject, gameObject, isSpawnRandom);
                continue;
            }

            if (isSpawned && isTimerExpired) {
                remove(specialObject, gameObject);
                continue;
            }

            specialObject.setTimer(specialObject.getTimer() - tickRate);
        }
    }

    /**
     * Removed a spawned GameObject from the current map
     *
     * @param specialObject Settings Object for the GameObject
     * @param gameObject    The already spawned GameObject to remove
     */
    private void remove(SpecialGameObject specialObject, GameObject gameObject) {

        if (gameObject instanceof Pickup) {
            // Only remove pickups if they are not taken or in use
            switch (((Pickup) gameObject).getState()) {
                case InUse:
                case Taken:
                    return;
            }
        }

        gameObjects.remove(gameObject);
        spawnedObjects.remove(specialObject);

        specialObject.setTimer(specialObject.getSpawnInterval());
    }

    /**
     * Spawns a GameObject on the current map
     *
     * @param specialObject Settings Object for the GameObject
     * @param gameObject    The new GameObject to spawn
     * @param isSpawnRandom If true the GameObject will be spawned at a random position on the Map
     */
    private void spawn(SpecialGameObject specialObject, GameObject gameObject, boolean isSpawnRandom) {

        if (isSpawnRandom) {
            Point point = Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects);
            gameObject.setPoint(point);
        }

        if (gameObject instanceof Pickup) {
            int quarterGridPixel = Game.GRID_PIXEL_SIZE / 4;
            gameObject.setX(gameObject.getX() - quarterGridPixel);
            gameObject.setY(gameObject.getY() - quarterGridPixel);
        }

        gameObject.setId(ID.getNext());

        gameObjects.add(gameObject);
        spawnedObjects.put(specialObject, gameObject);

        specialObject.setTimer(specialObject.getVisibleTime());
        specialObject.incrementTimesSpawned();
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
