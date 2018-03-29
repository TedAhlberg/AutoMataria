package common;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Johannes Blüml
 */
public class Trail extends GameObject {
    HashSet<Point> positions = new HashSet<>();
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

    public Collection<Point> getAll() {
        return positions;
    }

    public void tick() {}

    public void render(Graphics2D g) {
        g.setColor(color.brighter());
        for (Point position : positions) {
            g.drawRect(position.x * map.getGridSize(), position.y * map.getGridSize(), map.getGridSize(), map.getGridSize());
        }
    }
}
