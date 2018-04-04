package gameobjects;

import common.Direction;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * @author Johannes Blüml
 */
public class Trail extends GameObject {
    private Player player;
    private Shape trail;

    Trail(Player player) {
        super(player.getX(), player.getY());
        this.player = player;
        width = player.getWidth();
        height = player.getHeight();
    }

    public void tick() {
    }

    public void render(Graphics2D g) {
        if (trail == null) return;
        Color c = player.getColor();
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
        g.fill(trail);
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 150));
        g.draw(trail);
    }

    public void grow(Point previousPosition, Point newPosition) {
        Direction currentDirection = player.getDirection(),
                previousDirection = player.getPreviousDirection();

        boolean sameDirection = true || previousDirection == currentDirection,
                playerMovingLeft = currentDirection == Direction.Left && sameDirection,
                playerMovingRight = currentDirection == Direction.Right && sameDirection,
                playerMovingUp = currentDirection == Direction.Up && sameDirection,
                playerMovingDown = currentDirection == Direction.Down && sameDirection;
/*
        boolean playerMovingStaticUp = previousDirection == Direction.Static && currentDirection == Direction.Up,
                playerMovingStaticDown = previousDirection == Direction.Static && currentDirection == Direction.Down,
                playerMovingStaticLeft = previousDirection == Direction.Static && currentDirection == Direction.Left,
                playerMovingStaticRight = previousDirection == Direction.Static && currentDirection == Direction.Right,
                playerMovingLeftUp = previousDirection == Direction.Left && currentDirection == Direction.Up,
                playerMovingLeftDown = previousDirection == Direction.Left && currentDirection == Direction.Down,
                playerMovingRightUp = previousDirection == Direction.Right && currentDirection == Direction.Up,
                playerMovingRightDown = previousDirection == Direction.Right && currentDirection == Direction.Down,
                playerMovingUpLeft = previousDirection == Direction.Up && currentDirection == Direction.Left,
                playerMovingUpRight = previousDirection == Direction.Up && currentDirection == Direction.Right,
                playerMovingDownLeft = previousDirection == Direction.Down && currentDirection == Direction.Left,
                playerMovingDownRight = previousDirection == Direction.Down && currentDirection == Direction.Right;
*/
        if (playerMovingLeft) {
            newPosition.x = newPosition.x + player.getWidth();
            previousPosition.x = previousPosition.x + player.getWidth();
            Polygon rectangle = new Polygon();
            rectangle.addPoint(previousPosition.x, previousPosition.y);
            rectangle.addPoint(previousPosition.x, previousPosition.y + player.getHeight());
            rectangle.addPoint(newPosition.x, newPosition.y + player.getHeight());
            rectangle.addPoint(newPosition.x, newPosition.y);
            add(rectangle);
        }
        if (playerMovingRight) {
            Polygon rectangle = new Polygon();
            rectangle.addPoint(previousPosition.x, previousPosition.y);
            rectangle.addPoint(previousPosition.x, previousPosition.y + player.getHeight());
            rectangle.addPoint(newPosition.x, newPosition.y + player.getHeight());
            rectangle.addPoint(newPosition.x, newPosition.y);
            add(rectangle);
        }
        if (playerMovingDown) {
            Polygon rectangle = new Polygon();
            rectangle.addPoint(previousPosition.x, previousPosition.y);
            rectangle.addPoint(previousPosition.x + player.getWidth(), previousPosition.y);
            rectangle.addPoint(newPosition.x + player.getWidth(), newPosition.y);
            rectangle.addPoint(newPosition.x, newPosition.y);
            add(rectangle);
        }
        if (playerMovingUp) {
            newPosition.y = newPosition.y + player.getHeight();
            previousPosition.y = previousPosition.y + player.getHeight();
            Polygon rectangle = new Polygon();
            rectangle.addPoint(previousPosition.x, previousPosition.y);
            rectangle.addPoint(previousPosition.x + player.getWidth(), previousPosition.y);
            rectangle.addPoint(newPosition.x + player.getWidth(), newPosition.y);
            rectangle.addPoint(newPosition.x, newPosition.y);
            add(rectangle);
        }
    }

    private void add(Polygon rectangle) {
        Area area = new Area();
        if (trail != null) {
            area.add(new Area(trail));
        }
        area.add(new Area(rectangle));
        trail = AffineTransform.getTranslateInstance(0, 0).createTransformedShape(area);
    }

    public boolean intersects(Rectangle thing) {
        if (trail == null) return false;
        return trail.intersects(thing);
    }
}
