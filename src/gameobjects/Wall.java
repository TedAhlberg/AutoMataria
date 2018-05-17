package gameobjects;

import common.Utility;
import gameclient.Game;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * A Wall is GameObject that has a shape that can be changed into any shape by adding or removing Rectangles
 *
 * @author Johannes Blüml
 */
public class Wall extends GameObject {
    private static final long serialVersionUID = 2;

    protected Color color, borderColor;
    protected LinkedList<Point> gridPoints = new LinkedList<>();

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
        for (Point gridPoint : gridPoints) {
            Point point = Utility.convertFromGrid(gridPoint);
            g.setColor(color);
            g.fillRect(point.x, point.y, Game.GRID_PIXEL_SIZE, Game.GRID_PIXEL_SIZE);
        }
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
    }

    public void addGridPoint(Point gridPoint) {
        gridPoints.add(gridPoint);
    }

    public void removeGridPoint(Point gridPoint) {
        gridPoints.remove(gridPoint);
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
