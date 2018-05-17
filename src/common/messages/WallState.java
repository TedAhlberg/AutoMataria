package common.messages;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;

/**
 * @author Johannes Blüml
 */
public class WallState implements Serializable {
    private static final long serialVersionUID = 1;
    public int id;
    public int playerId;
    public Color color, borderColor;
    public HashSet<Point> addedPoints = new HashSet<>();
    public HashSet<Point> removedPoints = new HashSet<>();
}
