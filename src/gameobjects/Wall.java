package gameobjects;

import common.Game;
import common.Utility;

import java.awt.*;
import java.awt.geom.Area;
import java.util.*;

/**
 * A Wall is GameObject that has a shape that can be changed into any shape by adding or removing Rectangles
 *
 * @author Johannes Blüml
 */
public class Wall extends GameObject {
    private static final long serialVersionUID = 2;

    protected Color color, borderColor;
    protected LinkedList<Point> gridPoints = new LinkedList<>();
    transient protected Area area;
    transient protected HashSet<Point> pointsInArea;

    public Wall() {
        this(Color.CYAN.darker().darker(), null);
    }

    public Wall(Color color) {
        this(color, null);
    }

    public Wall(Wall object) {
        this(object.getColor(), object.getBorderColor());
        this.gridPoints.addAll(object.getGridPoints());
    }

    public Wall(Color color, Color borderColor) {
        this.color = color;
        this.borderColor = borderColor;
        this.width = Game.GRID_PIXEL_SIZE;
        this.height = Game.GRID_PIXEL_SIZE;
    }

    /**
     * Removes the wall completely
     */
    public void clear() {
        gridPoints.clear();
    }

    /**
     * Check if provided gridpoint collides with this Wall
     *
     * @param gridPoint the point in the grid you want to chech for collisions with
     * @return true if point collides will any points of this Wall
     */
    public boolean intersects(Point gridPoint) {
        if (gridPoints.isEmpty()) return false;
        return gridPoints.contains(gridPoint);
    }

    public boolean intersects(Rectangle rectangle) {
        Point p1 = new Point(rectangle.getLocation());
        if (intersects(Utility.convertToGrid(p1))) return true;

        Point p2 = new Point(p1.x + rectangle.width, p1.y);
        if (intersects(Utility.convertToGrid(p2))) return true;

        Point p3 = new Point(p1.x + rectangle.width, p1.y + rectangle.height);
        if (intersects(Utility.convertToGrid(p3))) return true;

        Point p4 = new Point(p1.x, p1.y + rectangle.height);
        return intersects(Utility.convertToGrid(p4));
    }

    public void tick() {}

    /**
     * Paints the Wall shape to the provided Graphics object
     *
     * @param g Graphics2D object to paint on
     */
    public void render(Graphics2D g) {
        if (area == null) {
            area = new Area();
            pointsInArea = new HashSet<>();
        }
        LinkedList<Point> remainingPoints = new LinkedList<>(gridPoints);
        remainingPoints.removeAll(pointsInArea);
        if (remainingPoints.size() > 0) {
            for (Point gridPoint : remainingPoints) {
                Point point = Utility.convertFromGrid(gridPoint);
                area.add(new Area(new Rectangle(point.x, point.y, width, height)));
                pointsInArea.add(gridPoint);
            }
        }
        g.setColor(color);
        g.fill(area);
        g.setColor(borderColor);
        g.draw(area);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void addGridPoints(Collection<Point> gridPoints) {
        this.gridPoints.addAll(gridPoints);
    }

    public void removeGridPoints(Collection<Point> removedPoints) {
        this.gridPoints.removeAll(removedPoints);
        area = null;
    }

    public void addGridPoint(Point gridPoint) {
        gridPoints.add(gridPoint);
    }

    public void removeGridPoint(Point gridPoint) {
        gridPoints.remove(gridPoint);
        area = null;
    }

    public Collection<Point> getGridPoints() {
        return gridPoints;
    }

    public String toString() {
        return "Wall{" +
                "color=" + color +
                ", borderColor=" + borderColor +
                ", gridPoints=" + gridPoints +
                '}';
    }
}
