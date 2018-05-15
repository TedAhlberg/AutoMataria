package gameobjects;

import common.Direction;

import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class Trail extends Wall {
    private static final long serialVersionUID = 2;

    transient private Player player;
    transient private Point previousPosition = new Point();

    Trail(Player player) {
        super(new Color(player.getColor().getRed(), player.getColor().getGreen(), player.getColor().getBlue(), 50),
                new Color(player.getColor().getRed(), player.getColor().getGreen(), player.getColor().getBlue(), 100));
        this.player = player;
        width = player.getWidth();
        height = player.getHeight();
    }

    public void grow(Point previousPosition, Point newPosition) {
        if (previousPosition == null) {
            previousPosition = this.previousPosition;
        }
        Direction currentDirection = player.getDirection(), previousDirection = player.getPreviousDirection();

        boolean sameDirection = true || previousDirection == currentDirection,
                playerMovingLeft = currentDirection == Direction.Left && sameDirection,
                playerMovingRight = currentDirection == Direction.Right && sameDirection,
                playerMovingUp = currentDirection == Direction.Up && sameDirection,
                playerMovingDown = currentDirection == Direction.Down && sameDirection;
        /*
         * boolean playerMovingStaticUp = previousDirection == Direction.Static &&
         * currentDirection == Direction.Up, playerMovingStaticDown = previousDirection
         * == Direction.Static && currentDirection == Direction.Down,
         * playerMovingStaticLeft = previousDirection == Direction.Static &&
         * currentDirection == Direction.Left, playerMovingStaticRight =
         * previousDirection == Direction.Static && currentDirection == Direction.Right,
         * playerMovingLeftUp = previousDirection == Direction.Left && currentDirection
         * == Direction.Up, playerMovingLeftDown = previousDirection == Direction.Left
         * && currentDirection == Direction.Down, playerMovingRightUp =
         * previousDirection == Direction.Right && currentDirection == Direction.Up,
         * playerMovingRightDown = previousDirection == Direction.Right &&
         * currentDirection == Direction.Down, playerMovingUpLeft = previousDirection ==
         * Direction.Up && currentDirection == Direction.Left, playerMovingUpRight =
         * previousDirection == Direction.Up && currentDirection == Direction.Right,
         * playerMovingDownLeft = previousDirection == Direction.Down &&
         * currentDirection == Direction.Left, playerMovingDownRight = previousDirection
         * == Direction.Down && currentDirection == Direction.Right;
         */

        if (player.isInvincible() == true) {
            return;
        } else if (playerMovingLeft) {
            newPosition.x = newPosition.x + player.getWidth();
            previousPosition.x = previousPosition.x + player.getWidth();
            Polygon rectangle = new Polygon();
            rectangle.addPoint(previousPosition.x, previousPosition.y);
            rectangle.addPoint(previousPosition.x, previousPosition.y + player.getHeight());
            rectangle.addPoint(newPosition.x, newPosition.y + player.getHeight());
            rectangle.addPoint(newPosition.x, newPosition.y);
            add(rectangle);
        } else if (playerMovingRight) {
            Polygon rectangle = new Polygon();
            rectangle.addPoint(previousPosition.x, previousPosition.y);
            rectangle.addPoint(previousPosition.x, previousPosition.y + player.getHeight());
            rectangle.addPoint(newPosition.x, newPosition.y + player.getHeight());
            rectangle.addPoint(newPosition.x, newPosition.y);
            add(rectangle);
        } else if (playerMovingDown) {
            Polygon rectangle = new Polygon();
            rectangle.addPoint(previousPosition.x, previousPosition.y);
            rectangle.addPoint(previousPosition.x + player.getWidth(), previousPosition.y);
            rectangle.addPoint(newPosition.x + player.getWidth(), newPosition.y);
            rectangle.addPoint(newPosition.x, newPosition.y);
            add(rectangle);
        } else if (playerMovingUp) {
            newPosition.y = newPosition.y + player.getHeight();
            previousPosition.y = previousPosition.y + player.getHeight();
            Polygon rectangle = new Polygon();
            rectangle.addPoint(previousPosition.x, previousPosition.y);
            rectangle.addPoint(previousPosition.x + player.getWidth(), previousPosition.y);
            rectangle.addPoint(newPosition.x + player.getWidth(), newPosition.y);
            rectangle.addPoint(newPosition.x, newPosition.y);
            add(rectangle);
        }

        this.previousPosition = newPosition;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean equals(Object obj) {
        return super.equals(obj) && player.equals(((Trail) obj).getPlayer());
    }
}
