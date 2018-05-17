package gameobjects;

import common.Utility;
import gameclient.Game;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Johannes Blüml
 */
public class Trail extends GameObject {
    private static final long serialVersionUID = 2;
    private Color color, borderColor;
    private Player player;
    private LinkedList<Point> trailPoints = new LinkedList<>();

    public Trail(Player player) {
        color = new Color(player.getColor().getRed(), player.getColor().getGreen(), player.getColor().getBlue(), 50);
        borderColor = new Color(player.getColor().getRed(), player.getColor().getGreen(), player.getColor().getBlue(), 150);
        this.player = player;
        width = player.getWidth();
        height = player.getHeight();
    }

    public void tick() {
    }

    public void render(Graphics2D g) {
        LinkedList<Point> points = new LinkedList<>();
        points.addAll(trailPoints);
        points.removeLast();
        for (Point gridPoint : points) {
            Point point = Utility.convertFromGrid(gridPoint);
            g.setColor(borderColor);
            g.fillRect(point.x, point.y, Game.GRID_PIXEL_SIZE, Game.GRID_PIXEL_SIZE);
        }
    }

    public void grow(Point previousPosition, Point playerPosition) {
        Point newPoint = Utility.convertToGrid(player.getPoint());
        if (!trailPoints.contains(newPoint)) {
            trailPoints.add(newPoint);
        }
    }

    public void clear() {
        trailPoints.clear();
    }

    public Player getPlayer() {
        return player;
    }

    public boolean equals(Object obj) {
        return super.equals(obj) && player.equals(((Trail) obj).getPlayer());
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

    public List<Point> getTrailPoints() {
        return trailPoints;
    }

    public void addTrailPoints(Collection<Point> trailPoints) {
        this.trailPoints.addAll(trailPoints);
    }

    public void removeTrailPoints(Collection<Point> removedPoints) {
        this.trailPoints.removeAll(removedPoints);
    }
}
