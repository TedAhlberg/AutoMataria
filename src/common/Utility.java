package common;

import gameobjects.GameObject;
import gameobjects.Wall;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Random;

/**
 * Some utility methods that are used in various places in the Game.
 *
 * @author Johannes Blüml
 */
public class Utility {
    private static Random random = new Random();

    /**
     * Finds a gameobject in the provided collection with the provided ID
     *
     * @param id          ID of gameobject to find
     * @param gameObjects Collection of gameobjects to search in
     * @return The found gameobject or null if not found
     */
    public static GameObject getById(int id, Collection<GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getId() == id) {
                return gameObject;
            }
        }
        return null;
    }

    /**
     * Returns a convinient name for a specific GRID Dimension
     *
     * @param grid Dimension of the grid
     * @return The pretty name for the grid size
     */
    public static String getGridSizeName(Dimension grid) {
        if (grid.height != grid.width) return "Unknown";
        if (grid.width == 50) return "Large";
        if (grid.width == 75) return "Normal";
        if (grid.width == 100) return "Small";
        return "Unknown";
    }

    /**
     * Returns the GRID Dimension for a specific name
     *
     * @param gridName Large Small or Normal
     * @return A Dimension that corresponds to the provided GRID name
     */
    public static Dimension getGridFromName(String gridName) {
        switch (gridName) {
            case "Large":
                return new Dimension(50, 50);
            case "Small":
                return new Dimension(100, 100);
        }
        return new Dimension(75, 75);
    }

    /**
     * Finds a position on the map that doesn't collide with any existing GameObjects
     *
     * @param grid        The grid size from the current GameMap
     * @param gameObjects The GameObjects that are used to check collisions
     * @return A Point (Not a grid point) that does not collide with any other GameObject
     */
    public static Point getRandomUniquePosition(Dimension grid, Collection<GameObject> gameObjects) {
        Rectangle rectangle = getGridRectangle();
        while (true) {
            rectangle.setLocation(convertFromGrid(getRandomPosition(grid)));
            if (intersectsNoGameObject(rectangle, gameObjects)) {
                return rectangle.getLocation();
            }
        }
    }

    /**
     * Checks if the provided Rectangle does not collide with any GameObjects
     *
     * @param rect        The Rectangle to use to check for collisions
     * @param gameObjects The GameObjects to iterate over
     * @return true if Rectangle does not collide with any GameObject, false if it does
     */
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

    /**
     * @param grid The grid that limits the search of a position.
     * @return A random position on the provided grid
     */
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

    /**
     * Converts the provided Point into a Point on the GRID
     *
     * @param point The Point to convert to GRID
     * @return The converted GRID Point
     */
    public static Point convertToGrid(Point point) {
        int x = point.x / Game.GRID_PIXEL_SIZE;
        int y = point.y / Game.GRID_PIXEL_SIZE;
        return new Point(x, y);
    }

    /**
     * Converts the provided GRID Point into a Point in the Game (pixels)
     *
     * @param point The GRID Point to convert
     * @return The converted Point
     */
    public static Point convertFromGrid(Point point) {
        int x = point.x * Game.GRID_PIXEL_SIZE;
        int y = point.y * Game.GRID_PIXEL_SIZE;
        return new Point(x, y);
    }

    /**
     * Converts the provided Dimension into a Dimension as a GRID
     *
     * @param dimension The Dimension to convert to GRID Dimension
     * @return The converted GRID Dimension
     */
    public static Dimension convertToGrid(Dimension dimension) {
        int width = dimension.width / Game.GRID_PIXEL_SIZE;
        int height = dimension.height / Game.GRID_PIXEL_SIZE;
        return new Dimension(width, height);
    }

    /**
     * Converts the provided GRID Dimension into a Dimension in the Game (pixels)
     *
     * @param dimension The GRID Dimension to convert
     * @return The converted Dimension
     */
    public static Dimension convertFromGrid(Dimension dimension) {
        int width = dimension.width * Game.GRID_PIXEL_SIZE;
        int height = dimension.height * Game.GRID_PIXEL_SIZE;
        return new Dimension(width, height);
    }

    /**
     * @return A Rectangle that is the size of one grid point in the Game
     */
    public static Rectangle getGridRectangle() {
        return new Rectangle(Game.GRID_PIXEL_SIZE, Game.GRID_PIXEL_SIZE);
    }

    public static Direction getOppositeDirection(Direction direction) {
        switch (direction) {
            case Up:
                return Direction.Down;
            case Down:
                return Direction.Up;
            case Left:
                return Direction.Right;
            case Right:
                return Direction.Left;
        }
        return Direction.Static;
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

    /**
     * Converts a BufferedImage into a compatible image to improve performance
     *
     * @param image The BufferedImage to convert
     * @return The Converted image
     */
    public static BufferedImage convertToCompatibleImage(BufferedImage image) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();

        // Image is already compatible
        if (image.getColorModel().equals(config.getColorModel())) {
            return image;
        }

        // Create a compatible image
        BufferedImage compatibleImage = config.createCompatibleImage(image.getWidth(), image.getHeight(),
                image.getTransparency());
        Graphics2D g2d = (Graphics2D) compatibleImage.getGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return compatibleImage;
    }
}
