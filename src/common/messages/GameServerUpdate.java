package common.messages;

import common.GameState;
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
    public GameObjectState gameObjectState;
    public Player player;
    public Collection<TrailState> trailStates;

    public GameServerUpdate(GameState state, GameObjectState gameObjectState, Collection<TrailState> trailStates) {
        this.state = state;
        this.gameObjectState = gameObjectState;
        this.trailStates = trailStates;
    }
}
