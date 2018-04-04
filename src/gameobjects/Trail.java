package gameobjects;

import common.GameMap;

import java.awt.*;
import java.util.*;

/**
 * @author Johannes Blüml
 */
public class Trail extends GameObject {
    private Random random = new Random();
    private HashSet<Point> positions = new HashSet<>();
    private Color color;
    private GameMap map;

    Trail(Color color, GameMap map) {
        super(0, 0);
        this.color = color;
        this.map = map;
    }

    public void add(int x, int y) {
        positions.add(new Point(x, y));
    }

    public boolean contains(int x, int y) {
        return positions.contains(new Point(x, y));
    }

    public void removeAll(Collection<Point> points) {
        positions.removeAll(points);
    }

    public void removeAll() {
        positions.clear();
    }

    public Collection<Point> getAll() {
        return positions;
    }

    public void tick() {}

    public void render(Graphics2D g) {
        g.setColor(color);
        for (Point position : positions) {
            g.fillRect(position.x * map.getGridMultiplier(), position.y * map.getGridMultiplier(), map.getGridMultiplier(), map.getGridMultiplier());
        }
    }
}
