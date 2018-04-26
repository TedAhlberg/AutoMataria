package common;

import gameclient.Game;
import gameobjects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Random;

/**
 * @author Johannes Blüml
 */
public class Utility {

    private static Random random = new Random();

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

    public static Point getRandomUniquePosition(Dimension grid, Collection<GameObject> gameObjects) {
        Rectangle rectangle = getGridRectangle();
        while (true) {
            rectangle.setLocation(convertFromGrid(getRandomPosition(grid)));
            System.out.println(rectangle);
            if (intersectsNoGameObject(rectangle, gameObjects)) {
                return rectangle.getLocation();
            }
        }
    }

    public static boolean intersectsNoGameObject(Rectangle rect, Collection<GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Wall) {
                if (((Wall) gameObject).intersects(rect)) {
                    return false;
                }
            } else if (rect.intersects(gameObject.getBounds())) {
                return false;
            }
        }
        return true;
    }

    public static Point getRandomPosition(Dimension grid) {
        int x = random.nextInt(grid.width);
        int y = random.nextInt(grid.height);
        return new Point(x, y);
    }

    /**
     * Checks if there is a position in the future (but limited by the speed)
     * where it is possible to change direction (only very close to grid boundaries)
     *
     * @param direction Direction that you want to change to
     * @param point     Current Position
     * @param speed     Speed that object is traveling at (used to limit grid change)
     * @return -1 if not possible otherwise amount of pixels where direction change can happen
     */
    public static int canChangeDirection(Direction direction, Point point, int speed) {
        if (point == null || direction == null) return -1;
        int gridSize = Game.GRID_PIXEL_SIZE;
        for (int i = 0; i < speed; i++) {
            switch (direction) {
                case Up:
                    if ((point.y - i) % gridSize == 0) {
                        return i;
                    }
                    break;
                case Down:
                    if ((point.y + i) % gridSize == 0) {
                        return i;
                    }
                    break;
                case Left:
                    if ((point.x - i) % gridSize == 0) {
                        return i;
                    }
                    break;
                case Right:
                    if ((point.x + i) % gridSize == 0) {
                        return i;
                    }
                    break;
            }
        }
        return -1;
    }

    public static Point convertToGrid(Point point) {
        int x = point.x / Game.GRID_PIXEL_SIZE;
        int y = point.y / Game.GRID_PIXEL_SIZE;
        return new Point(x, y);
    }

    public static Point convertFromGrid(Point point) {
        int x = point.x * Game.GRID_PIXEL_SIZE;
        int y = point.y * Game.GRID_PIXEL_SIZE;
        return new Point(x, y);
    }

    public static Dimension convertToGrid(Dimension dimension) {
        int width = dimension.width / Game.GRID_PIXEL_SIZE;
        int height = dimension.height / Game.GRID_PIXEL_SIZE;
        return new Dimension(width, height);
    }

    public static Dimension convertFromGrid(Dimension dimension) {
        int width = dimension.width * Game.GRID_PIXEL_SIZE;
        int height = dimension.height * Game.GRID_PIXEL_SIZE;
        return new Dimension(width, height);
    }

    public static Rectangle getGridRectangle() {
        return new Rectangle(Game.GRID_PIXEL_SIZE, Game.GRID_PIXEL_SIZE);
    }

    /**
     * Creates a compatible BufferedImage that can be used to paint on
     *
     * @param size Dimension of the BufferedImage that is created
     * @return A BufferedImage
     */
    public static BufferedImage createCompatibleImage(Dimension size) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();
        return config.createCompatibleImage(size.width, size.height, Transparency.TRANSLUCENT);
    }
}
