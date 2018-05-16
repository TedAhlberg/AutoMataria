package gameserver;

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
    private HashMap<Trail, Collection<Point>> updatedTrailPoints = new HashMap<>();

    public UpdateManager(Collection<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }

    public Collection<TrailState> getTrailStates() {
        ArrayList<TrailState> result = new ArrayList<>();

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Trail) {
                Trail trail = (Trail) gameObject;

                TrailState trailState = new TrailState();
                trailState.id = trail.getId();
                trailState.color = trail.getColor();
                trailState.borderColor = trail.getBorderColor();

                if (updatedTrailPoints.containsKey(trail)) {
                    // Remove already sent trailpoints
                    for (Point point : trail.getTrailPoints()) {
                        if (!updatedTrailPoints.get(trail).contains(point)) {
                            trailState.trailPoints.add(point);
                        }
                    }
                    updatedTrailPoints.get(trail).addAll(trailState.trailPoints);
                } else {
                    // Add gameobject the first update
                    trailState.trail = trail;
                    updatedTrailPoints.put(trail, trailState.trailPoints);
                }

                result.add(trailState);
            }
        }

        return result;
    }
}
