package gameserver;

import common.messages.GameServerUpdate;
import common.messages.WallUpdate;
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

    public GameServerUpdate getNewUpdate() {
        GameServerUpdate message = new GameServerUpdate();

        for (GameObject gameObject : gameObjects) {
            int id = gameObject.getId();
            if (!updatedGameObjects.contains(id)) {
                message.added.add(gameObject);
            } else if (!(gameObject instanceof Wall)) {
                message.updated.add(gameObject);
            }
            message.existingObjects.add(id);
            updatedGameObjects.add(id);
        }

        Iterator<Integer> iterator = updatedGameObjects.iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            if (!message.existingObjects.contains(id)) {
                updatedWallPoints.remove(id);
                iterator.remove();
            }
        }

        message.wallStates.putAll(getWallStates());

        return message;
    }

    private Map<Integer, WallUpdate> getWallStates() {
        HashMap<Integer, WallUpdate> result = new HashMap<>();

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Wall) {
                Wall wall = (Wall) gameObject;
                int id = wall.getId();

                WallUpdate wallUpdate = new WallUpdate();
                wallUpdate.id = wall.getId();
                wallUpdate.color = wall.getColor();
                wallUpdate.borderColor = wall.getBorderColor();
                wallUpdate.direction = wall.getDirection();

                if (updatedWallPoints.containsKey(id)) {
                    Collection<Point> clientPoints = updatedWallPoints.get(id);

                    // Remove already sent wallpoints
                    for (Point point : clientPoints) {
                        if (!wall.getGridPoints().contains(point)) {
                            wallUpdate.removedPoints.add(point);
                        }
                    }
                    clientPoints.removeAll(wallUpdate.removedPoints);

                    // Add new wallpoints
                    for (Point point : wall.getGridPoints()) {
                        if (!clientPoints.contains(point)) {
                            wallUpdate.addedPoints.add(point);
                        }
                    }
                    clientPoints.addAll(wallUpdate.addedPoints);
                } else {
                    // Add new wallpoints for a new Wall
                    HashSet<Point> initialPoints = new HashSet<>(wall.getGridPoints());
                    updatedWallPoints.put(id, initialPoints);
                    wallUpdate.addedPoints.addAll(initialPoints);
                }

                result.put(id, wallUpdate);
            }
        }

        return result;
    }
}
