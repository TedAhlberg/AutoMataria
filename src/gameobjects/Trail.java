package gameobjects;

import common.Utility;
import gameclient.Game;

import java.awt.*;
import java.awt.geom.Area;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author Johannes Blüml
 */
public class Trail extends Wall {
    private static final long serialVersionUID = 2;
    transient private Player player;

    public Trail(Player player) {
        this.player = player;
        width = Game.GRID_PIXEL_SIZE;
        height = Game.GRID_PIXEL_SIZE;
    }

    public void tick() {}

    public void render(Graphics2D g) {
        if (area == null) {
            area = new Area();
            pointsInArea = new HashSet<>();
        }
        LinkedList<Point> remainingPoints = new LinkedList<>(gridPoints);

        if (remainingPoints.size() > 1) {
            // Some magic calculation to hide the trail end behind the player
            remainingPoints.removeLast();
            Point lastPoint;
            switch (direction) {
                case Up:
                    lastPoint = Utility.convertFromGrid(remainingPoints.removeLast());
                    area.add(new Area(new Rectangle(lastPoint.x, lastPoint.y + (height / 2), width, (height / 2))));
                    break;
                case Left:
                    lastPoint = Utility.convertFromGrid(remainingPoints.removeLast());
                    area.add(new Area(new Rectangle(lastPoint.x + (width / 2), lastPoint.y, (width / 2), height)));
                    break;
            }
        }

        remainingPoints.removeAll(pointsInArea);
        for (Point gridPoint : remainingPoints) {
            Point point = Utility.convertFromGrid(gridPoint);
            area.add(new Area(new Rectangle(point.x, point.y, width, height)));
            pointsInArea.add(gridPoint);
        }

        g.setColor(color);
        g.fill(area);
        g.setColor(borderColor);
        g.draw(area);
    }

    public void grow() {
        setDirection(player.getDirection());
        Point playerPoint = player.getPoint();

        Point newPoint = Utility.convertToGrid(playerPoint);
        if (!gridPoints.contains(newPoint)) {
            gridPoints.add(newPoint);
        }
    }

    public boolean intersects(Point gridPoint) {
        if (gridPoints.isEmpty()) return false;

        Point playerPoint = Utility.convertToGrid(player.getPoint());
        Point lastTrailPoint = gridPoints.getLast();
        if (playerPoint.equals(gridPoint) && playerPoint.equals(lastTrailPoint)) return false;

        return gridPoints.contains(gridPoint);
    }

    public void clear() {
        gridPoints.clear();
    }
}
