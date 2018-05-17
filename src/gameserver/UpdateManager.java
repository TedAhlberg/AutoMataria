package gameserver;

import common.messages.GameObjectState;
import common.messages.WallState;
import gameobjects.GameObject;
import gameobjects.Wall;

import java.awt.*;
import java.util.*;

/**
 * Manages updates to clients so they only are sent changes instead of all GameObjects on every update
 *
 * @author Johannes Blüml
 */
public class UpdateManager {
    private Collection<GameObject> gameObjects;
    private HashMap<Integer, Collection<Point>> updatedWallPoints = new HashMap<>();
    private HashSet<Integer> updatedGameObjects = new HashSet<>();

    public UpdateManager(Collection<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }

    public GameObjectState getGameObjectStates() {
        GameObjectState gameObjectState = new GameObjectState();
        gameObjectState.existingObjects = new HashSet<>();

        for (GameObject gameObject : gameObjects) {
            if (!updatedGameObjects.contains(gameObject.getId())) {
                gameObjectState.added.add(gameObject);
            } else if (!(gameObject instanceof Wall)) {
                gameObjectState.updated.add(gameObject);
            }
            gameObjectState.existingObjects.add(gameObject.getId());
            updatedGameObjects.add(gameObject.getId());
        }

        Iterator<Integer> iterator = updatedGameObjects.iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            if (!gameObjectState.existingObjects.contains(id)) {
                updatedWallPoints.remove(id);
                iterator.remove();
            }
        }

        return gameObjectState;
    }

    public Collection<WallState> getWallStates() {
        ArrayList<WallState> result = new ArrayList<>();

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Wall) {
                Wall wall = (Wall) gameObject;
                int id = wall.getId();

                WallState wallState = new WallState();
                wallState.id = wall.getId();
                wallState.color = wall.getColor();
                wallState.borderColor = wall.getBorderColor();

                if (updatedWallPoints.containsKey(id)) {
                    Collection<Point> clientPoints = updatedWallPoints.get(id);

                    // Remove already sent trailpoints
                    for (Point point : clientPoints) {
                        if (!wall.getGridPoints().contains(point)) {
                            wallState.removedPoints.add(point);
                        }
                    }
                    clientPoints.removeAll(wallState.removedPoints);

                    // Add new wallpoints
                    for (Point point : wall.getGridPoints()) {
                        if (!clientPoints.contains(point)) {
                            wallState.addedPoints.add(point);
                        }
                    }
                    clientPoints.addAll(wallState.addedPoints);
                } else {
                    // Add new wallpoints for a new Wall
                    HashSet<Point> initialPoints = new HashSet<>(wall.getGridPoints());
                    updatedWallPoints.put(id, initialPoints);
                    wallState.addedPoints.addAll(initialPoints);
                }
                result.add(wallState);
            }
        }

        return result;
    }
}
