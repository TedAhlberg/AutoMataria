package common;

import gameobjects.GameObject;
import gameobjects.Player;

import java.io.Serializable;
import java.util.Collection;

/**
 * Represents a message that is send on every update from the GameServer.
 *
 * @author Johannes Blüml
 */
public class GameServerUpdate implements Serializable {
    public GameState state;
    public Collection<GameObject> gameObjects;
    public Player player;

    public GameServerUpdate(GameState state, Collection<GameObject> gameObjects) {
        this.state = state;
        this.gameObjects = gameObjects;
    }
}
