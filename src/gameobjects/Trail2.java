package gameobjects;

import common.Direction;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.LinkedList;

/**
 * @author Johannes Blüml
 */
public class Trail2 extends Wall {
    private static final long serialVersionUID = 2;
    private Player player;
    private LinkedList<Point> trailPoints = new LinkedList<>();
    private LinkedList<Point> trailPoints2 = new LinkedList<>();
    private GeneralPath path = new GeneralPath();

    Trail2(Player player) {
        super(new Color(player.getColor().getRed(), player.getColor().getGreen(), player.getColor().getBlue(), 50),
                new Color(player.getColor().getRed(), player.getColor().getGreen(), player.getColor().getBlue(), 150));
        this.player = player;
        width = player.getWidth();
        height = player.getHeight();
    }

    public void tick() {
        if (trailPoints.size() < 2) return;
        if (trailPoints.size() % 100 == 0)
            System.out.println("TRAILPOINTS: " + (trailPoints.size() + trailPoints2.size()));
        path = new GeneralPath(Path2D.WIND_NON_ZERO, trailPoints.size() + trailPoints2.size());
        Point first = trailPoints.getFirst();
        path.moveTo(first.x, first.y);
        Point lastPoint = new Point();
        for (Point point : trailPoints) {
            if (lastPoint.equals(point)) continue;
            path.lineTo(point.x, point.y);
            lastPoint = point;
        }
        Point last2 = trailPoints2.getLast();
        path.lineTo(last2.x, last2.y);
        for (int i = trailPoints2.size() - 1; i >= 0; i--) {
            Point point = trailPoints2.get(i);
            if (lastPoint.equals(point)) continue;
            path.lineTo(point.x, point.y);
            lastPoint = point;
        }
        path.lineTo(first.x, first.y);
        if (true) return;
        if (trailPoints.size() > 4) {
            add(path);
            trailPoints.clear();
            trailPoints2.clear();
        }
    }

    public void render(Graphics2D g) {
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(getColor());
        g.fill(path);
        g.setColor(getBorderColor());
        g.draw(path);
        //super.render(g);
    }

    public void grow(Point previousPosition, Point playerPosition) {
                /*
        if (trailPoints.size() == 0) {
            trail.moveTo(playerPosition.x, playerPosition.y);
            trailPoints.add(playerPosition);
            return;
        }

        boolean equalX = previousPosition.x == playerPosition.x;
        boolean equalY = previousPosition.y == playerPosition.y;
        if (equalX && equalY) {
            System.out.println("STATIC");
            return;
        }

        if (equalX && previousPosition.y > playerPosition.y) {
            System.out.println("UP");
        }
        else if (equalX) {
            System.out.println("DOWN");
        }
        else if (previousPosition.x > playerPosition.x) {
            System.out.println("LEFT");
        }
        else {
            System.out.println("RIGHT");
        }

        trail.lineTo(playerPosition.x, playerPosition.y);
        trailPoints.add(playerPosition);
        */
        Direction currentDirection = player.getDirection(),
                previousDirection = player.getPreviousDirection();

        boolean sameDirection = previousDirection == currentDirection,
                playerMovingLeft = currentDirection == Direction.Left && sameDirection,
                playerMovingRight = currentDirection == Direction.Right && sameDirection,
                playerMovingUp = currentDirection == Direction.Up && sameDirection,
                playerMovingDown = currentDirection == Direction.Down && sameDirection;

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

        if (playerMovingLeft) {
            if (trailPoints.size() > 1) {
                trailPoints.removeLast();
                trailPoints2.removeLast();
            }

            trailPoints2.add(new Point(playerPosition.x + 100, playerPosition.y));
            trailPoints.add(new Point(playerPosition.x + 100, playerPosition.y + 100));

        } else if (playerMovingRight) {
            if (trailPoints.size() > 1) {
                trailPoints.removeLast();
                trailPoints2.removeLast();
            }

            trailPoints.add(playerPosition);
            trailPoints2.add(new Point(playerPosition.x, playerPosition.y + 100));

        } else if (playerMovingDown) {
            if (trailPoints.size() > 1) {
                trailPoints.removeLast();
                trailPoints2.removeLast();
            }

            trailPoints.add(new Point(playerPosition.x + 100, playerPosition.y));
            trailPoints2.add(playerPosition);

        } else if (playerMovingUp) {
            if (trailPoints.size() > 1) {
                trailPoints.removeLast();
                trailPoints2.removeLast();
            }

            trailPoints.add(new Point(playerPosition.x, playerPosition.y + 100));
            trailPoints2.add(new Point(playerPosition.x + 100, playerPosition.y + 100));

        } else if (playerMovingRightUp) {
            trailPoints2.add(new Point(previousPosition.x + 100, previousPosition.y + 100));
            trailPoints.add(playerPosition);
            trailPoints.add(playerPosition);
            trailPoints2.add(new Point(playerPosition.x + 100, playerPosition.y + 100));
            trailPoints2.add(new Point(playerPosition.x + 100, playerPosition.y));
        } else if (playerMovingRightDown) {
            trailPoints.add(new Point(previousPosition.x + 100, previousPosition.y));

            trailPoints.add(new Point(playerPosition.x + 100, playerPosition.y));
            trailPoints.add(new Point(playerPosition.x + 100, playerPosition.y + 100));
            trailPoints2.add(new Point(playerPosition.x, playerPosition.y + 100));
            trailPoints2.add(new Point(playerPosition.x, playerPosition.y + 100));
        } else if (playerMovingDownLeft) {
            trailPoints.add(new Point(previousPosition.x + 100, previousPosition.y + 100));

            trailPoints.add(new Point(playerPosition.x + 100, playerPosition.y + 100));
            trailPoints.add(new Point(playerPosition.x, playerPosition.y + 100));

            trailPoints2.add(new Point(playerPosition.x, playerPosition.y));
            trailPoints2.add(new Point(playerPosition.x, playerPosition.y));
        } else if (playerMovingDownRight) {
            trailPoints2.add(new Point(previousPosition.x, previousPosition.y + 100));

            trailPoints2.add(new Point(playerPosition.x, playerPosition.y + 100));
            trailPoints2.add(new Point(playerPosition.x + 100, playerPosition.y + 100));

            trailPoints.add(new Point(playerPosition.x + 100, playerPosition.y));
            trailPoints.add(new Point(playerPosition.x + 100, playerPosition.y));
        } else if (playerMovingUpLeft) {
            trailPoints2.add(new Point(previousPosition.x + 100, previousPosition.y));

            trailPoints2.add(new Point(playerPosition.x + 100, playerPosition.y));
            trailPoints2.add(new Point(playerPosition.x, playerPosition.y));

            trailPoints.add(new Point(playerPosition.x, playerPosition.y + 100));
            trailPoints.add(new Point(playerPosition.x, playerPosition.y + 100));
        } else if (playerMovingUpRight) {
            trailPoints.add(new Point(previousPosition.x, previousPosition.y));

            trailPoints.add(new Point(playerPosition.x, playerPosition.y));
            trailPoints.add(new Point(playerPosition.x + 100, playerPosition.y));

            trailPoints2.add(new Point(playerPosition.x + 100, playerPosition.y + 100));
            trailPoints2.add(new Point(playerPosition.x + 100, playerPosition.y + 100));
        } else if (playerMovingLeftDown) {
            trailPoints2.add(new Point(previousPosition.x, previousPosition.y));

            trailPoints2.add(new Point(playerPosition.x, playerPosition.y));
            trailPoints2.add(new Point(playerPosition.x, playerPosition.y + 100));

            trailPoints.add(new Point(playerPosition.x + 100, playerPosition.y + 100));
            trailPoints.add(new Point(playerPosition.x + 100, playerPosition.y + 100));
        } else if (playerMovingLeftUp) {
            trailPoints.add(new Point(previousPosition.x, previousPosition.y + 100));

            trailPoints.add(new Point(playerPosition.x, playerPosition.y + 100));
            trailPoints.add(new Point(playerPosition.x, playerPosition.y));

            trailPoints2.add(new Point(playerPosition.x + 100, playerPosition.y));
            trailPoints2.add(new Point(playerPosition.x + 100, playerPosition.y));
        }
    }

    public void remove(Rectangle rectangle) {
        trailPoints2.clear();
        trailPoints.clear();
    }

    public Player getPlayer() {
        return player;
    }

    public boolean equals(Object obj) {
        return super.equals(obj) && player.equals(((Trail) obj).getPlayer());
    }
}
