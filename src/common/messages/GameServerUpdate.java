package common.messages;

import common.GameState;
import gameobjects.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a message that is send on every update from the GameServer.
 *
 * @author Johannes Blüml
 */
public class GameServerUpdate extends Message {
    private static final long serialVersionUID = 1L;

    public GameState state;
    public Collection<GameObject> gameObjects;
    public Player player;
    public Collection<TrailState> trailStates;

    public GameServerUpdate(GameState state, Collection<GameObject> gameObjects, Collection<TrailState> trailStates) {
        this.state = state;
        this.gameObjects = new ArrayList<>();
        for (GameObject gameObject : gameObjects) {
            if (!(gameObject instanceof Trail)) {
                this.gameObjects.add(gameObject);
            }
        }
        this.trailStates = trailStates;
    }
}
