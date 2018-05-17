package gameserver;

import common.messages.GameObjectState;
import common.messages.TrailState;
import gameobjects.GameObject;
import gameobjects.Trail;

import java.awt.*;
import java.util.*;

/**
 * Manages updates to clients so they only are sent changes instead of all GameObjects on every update
 *
 * @author Johannes Blüml
 */
public class UpdateManager {
    private Collection<GameObject> gameObjects;
    private HashMap<Integer, Collection<Point>> updatedTrailPoints = new HashMap<>();
    private HashSet<Integer> updatedGameObjects = new HashSet<>();

    public UpdateManager(Collection<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }

    public GameObjectState getGameObjectStates() {
        GameObjectState gameObjectState = new GameObjectState();
        HashSet<Integer> gameObjectIDs = new HashSet<>();

        for (GameObject gameObject : gameObjects) {
            if (!updatedGameObjects.contains(gameObject.getId())) {
                gameObjectState.added.add(gameObject);
            } else if (!(gameObject instanceof Trail)) {
                gameObjectState.updated.add(gameObject);
            }
            gameObjectIDs.add(gameObject.getId());
            updatedGameObjects.add(gameObject.getId());
        }

        Iterator<Integer> iterator = updatedGameObjects.iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            if (!gameObjectIDs.contains(id)) {
                gameObjectState.removed.add(id);
                updatedTrailPoints.remove(id);
                iterator.remove();
            }
        }

        return gameObjectState;
    }

    public Collection<TrailState> getTrailStates() {
        ArrayList<TrailState> result = new ArrayList<>();

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Trail) {
                Trail trail = (Trail) gameObject;
                int id = trail.getId();

                TrailState trailState = new TrailState();
                trailState.id = trail.getId();
                trailState.color = trail.getColor();
                trailState.borderColor = trail.getBorderColor();

                if (updatedTrailPoints.containsKey(id)) {
                    Collection<Point> clientPoints = updatedTrailPoints.get(id);

                    // Remove already sent trailpoints
                    for (Point point : clientPoints) {
                        if (!trail.getTrailPoints().contains(point)) {
                            trailState.removedPoints.add(point);
                            System.out.println("ADDING POINT " + point);
                        }
                    }
                    clientPoints.removeAll(trailState.removedPoints);

                    // Add new trailpoints
                    for (Point point : trail.getTrailPoints()) {
                        if (!clientPoints.contains(point)) {
                            trailState.addedPoints.add(point);
                        }
                    }
                    clientPoints.addAll(trailState.addedPoints);
                } else {
                    // Add new trailpoints for a new trail
                    HashSet<Point> initialpoints = new HashSet<>(trail.getTrailPoints());
                    updatedTrailPoints.put(id, initialpoints);
                    trailState.addedPoints.addAll(initialpoints);
                }
                result.add(trailState);
            }
        }

        return result;
    }
}
