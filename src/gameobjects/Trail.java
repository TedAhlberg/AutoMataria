package gameobjects;

import common.Utility;
import gameclient.Game;

import java.awt.*;

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
        int size = gridPoints.size();
        for (Point gridPoint : gridPoints) {
            Point point = Utility.convertFromGrid(gridPoint);
            g.setColor(color);
            g.fillRect(point.x, point.y, width, height);
            size -= 1;
            if (size < 2) break;
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
}
