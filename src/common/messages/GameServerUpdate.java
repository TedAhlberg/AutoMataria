package common.messages;

import common.GameState;
import gameobjects.GameObject;
import gameobjects.Player;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a message that is send on every update from the GameServer.
 *
 * @author Johannes Blüml
 */
public class GameServerUpdate extends Message {
    private static final long serialVersionUID = 1L;

    public GameState state;
    public Player player;
    public Collection<Integer> existingObjects = new HashSet<>();
    public Collection<GameObject> added = new HashSet<>();
    public Collection<GameObject> updated = new HashSet<>();
    public Collection<WallState> wallStates = new HashSet<>();
}
