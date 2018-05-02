package common.messages;

import common.GameState;
import gameobjects.GameObject;
import gameobjects.Player;

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

    public GameServerUpdate(GameState state, Collection<GameObject> gameObjects) {
        this.state = state;
        this.gameObjects = gameObjects;
    }
}
