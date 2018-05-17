package gameobjects;

import common.Utility;
import gameclient.Game;

import java.awt.*;
import java.util.LinkedList;

/**
 * @author Johannes Blüml
 */
public class Trail extends Wall {
    private static final long serialVersionUID = 2;
    transient private Player player;

    public Trail(Player player) {
        color = new Color(player.getColor().getRed(), player.getColor().getGreen(), player.getColor().getBlue(), 50);
        borderColor = new Color(player.getColor().getRed(), player.getColor().getGreen(), player.getColor().getBlue(), 125);
        this.player = player;
        width = Game.GRID_PIXEL_SIZE;
        height = Game.GRID_PIXEL_SIZE;
    }

    public void tick() {}

    public void render(Graphics2D g) {
        LinkedList<Point> points = new LinkedList<>(gridPoints);
        if (gridPoints.size() > 4) {
            switch (direction) {
                case Left:
                case Up:
                    points.removeLast();
            }
        }
        for (Point gridPoint : points) {
            Point point = Utility.convertFromGrid(gridPoint);
            g.setColor(color);
            g.fillRect(point.x, point.y, width, height);
        }
/*
        if (gridPoints.size() < 2) return;

        Point lastTrail = Utility.convertFromGrid(gridPoints.get(gridPoints.size() - 2));
        Point playerPoint = player.getPoint();
        g.setColor(Color.RED);
        g.fillRect(lastTrail.x, lastTrail.y, width, height);

        System.out.println("TRAIL RENDER PLAYER: " + player);
        */
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

    public Player getPlayer() {
        return player;
    }

    public boolean equals(Object obj) {
        return super.equals(obj) && player.equals(((Trail) obj).getPlayer());
    }
}
