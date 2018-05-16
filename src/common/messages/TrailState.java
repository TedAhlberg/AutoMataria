package common.messages;

import gameobjects.Trail;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;

/**
 * @author Johannes Blüml
 */
public class TrailState implements Serializable {
    private static final long serialVersionUID = 1;
    public int id;
    public Color color, borderColor;
    public HashSet<Point> trailPoints = new HashSet<>();
    public Trail trail;

    @Override
    public String toString() {
        return "TrailState{" +
                "id=" + id +
                ", color=" + color +
                ", borderColor=" + borderColor +
                ", trailPoints=" + trailPoints +
                ", trail=" + trail +
                '}';
    }
}
