package common.messages;

import gameobjects.GameObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Johannes Blüml
 */
public class GameObjectState implements Serializable {
    public Collection<Integer> existingObjects = new HashSet<>();
    public Collection<GameObject> added = new HashSet<>();
    public Collection<GameObject> updated = new HashSet<>();
}
