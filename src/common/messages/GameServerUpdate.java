package common.messages;

import common.GameState;
import gameobjects.GameObject;
import gameobjects.Player;

import java.util.*;

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
    public Map<Integer, WallUpdate> wallStates = new HashMap<>();
}
