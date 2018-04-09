package common;

import gameobjects.GameObject;
import gameserver.GameState;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Johannes Blüml
 */
public class GameServerUpdate implements Serializable {
    public GameState state;
    public double readyPercentage;
    public Collection<GameObject> gameObjects;

    public GameServerUpdate(GameState state, double readyPercentage, Collection<GameObject> gameObjects) {
        this.state = state;
        this.readyPercentage = readyPercentage;
        this.gameObjects = gameObjects;
    }
}
