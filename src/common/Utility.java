package common;

import gameclient.Game;
import gameobjects.GameObject;
import gameobjects.Player;
import gameobjects.Wall;

import java.awt.*;
import java.util.Collection;
import java.util.Random;

/**
 * @author Johannes Blüml
 */
public class Utility {
    
    private static Random random =  new Random();

    public static int clamp(int var, int min, int max) {
        if (var >= max)
            return max;
        if (var <= min)
            return min;
        return var;
    }

    public static double getReadyPlayerPercentage(Collection<Player> players) {
        if (players == null) {
            return 0;
        }
        int readyPlayers = 0;
        for (Player player : players) {
            if (player.isReady()) {
                readyPlayers += 1;
            }
        }
        double readyPlayerPercentage = ((double) readyPlayers / (double) players.size());
        readyPlayerPercentage = Math.round(readyPlayerPercentage * 100);
        return readyPlayerPercentage;
    }

    public static Point findRandomMapPosition(Dimension grid) {

        Rectangle rectangle = new Rectangle(getOneRandom(grid));
        rectangle.x *= Game.GRID_PIXEL_SIZE;
        rectangle.y *= Game.GRID_PIXEL_SIZE;
        rectangle.width = Game.GRID_PIXEL_SIZE;
        rectangle.height = Game.GRID_PIXEL_SIZE;

        return rectangle.getLocation();

    }

    public static boolean intersectsAnyGameObject(Rectangle rect, Collection<GameObject> gameObjects) {
        for (GameObject object : gameObjects) {
            if ((object instanceof Player) && rect.getBounds().intersects(object.getBounds())) {
                return true;
            } else if ((object instanceof Wall) && ((Wall) object).intersects(rect.getBounds())) {
                return true;
            }
        }
        return false;
    }

    public static Point getOneRandom(Dimension grid) {
        int x = random.nextInt(grid.width);
        int y = random.nextInt(grid.height);
        return new Point(x, y);
    }

    public static Point canChangeDirection(Direction direction, Point point, int speed) {
        switch (direction) {
        case Up:
            for (int i = 0; i < speed; i++) {
                if ((point.y - i) % Game.GRID_PIXEL_SIZE == 0) {
                    return new Point(point.x, point.y - i);
                }
            }
            break;
        case Down:
            for (int i = 0; i < speed; i++) {
                if ((point.y + i) % Game.GRID_PIXEL_SIZE == 0) {
                    return new Point(point.x, point.y + i);
                }
            }
            break;
        case Left:
            for (int i = 0; i < speed; i++) {
                if ((point.x - i) % Game.GRID_PIXEL_SIZE == 0) {
                    return new Point(point.x - i, point.y);
                }
            }
            break;
        case Right:
            for (int i = 0; i < speed; i++) {
                if ((point.x + i) % Game.GRID_PIXEL_SIZE == 0) {
                    return new Point(point.x + i, point.y);
                }
            }
            break;
        }
        return null;
    }
}
