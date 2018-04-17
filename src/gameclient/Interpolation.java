package gameclient;

import gameobjects.GameObject;
import gameobjects.Player;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Interpolation handles the smooth movement of a character between updates from the server
 *
 * Related requirements:
 * SF001, Interpolering
 *
 * @author Johannes Blüml
 */
public class Interpolation {
    private final ConcurrentHashMap<GameObject, Point> currentPositions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<GameObject, Point> targetPositions = new ConcurrentHashMap<>();
    private Function<Integer, Integer> calculateInterpolation;
    private final long timeBetweenRenders;

    /**
     * @param timeBetweenRenders time in nanoseconds between each rendered frame
     */
    public Interpolation(long timeBetweenRenders) {
        this.timeBetweenRenders = timeBetweenRenders;
    }

    /**
     * Adds a target that a current object will be interpolated towards
     *
     * @param gameObject A gameobject that will be used as target for the same gameobjects current position
     */
    public void addTarget(GameObject gameObject) {
        targetPositions.put(gameObject, new Point(gameObject.getX(), gameObject.getY()));
    }

    /**
     * Creates a Function that will be used in the interpolation
     * method to calculate that gameobjects position change
     *
     * @param timeSinceLastRender Time in nanoseconds since the last rendered frame was completed
     */
    public void createCalculation(long timeSinceLastRender) {
        long ratio = timeSinceLastRender / timeBetweenRenders;
        long nanoSecond = 1000000000;
        calculateInterpolation = changePersecond -> (int) Math.ceil(ratio * (changePersecond / ((double) nanoSecond / timeSinceLastRender)));
    }

    /**
     * @param gameObject The GameObject to interpolate (Move closer to target position)
     */
    public void interpolate(GameObject gameObject) {
        int interpolation = calculateInterpolation.apply(((Player) gameObject).getSpeedPerSecond());

        // Get current position
        Point current = currentPositions.get(gameObject);
        if (current == null) {
            // Set current position from the gameObject if there none available yet
            currentPositions.put(gameObject, new Point(gameObject.getX(), gameObject.getY()));
            current = currentPositions.get(gameObject);
        }

        // Get target position
        Point target = targetPositions.get(gameObject);

        {
            // Sets a limit for how far behind the current position can be from target
            int limit = 3 * Game.GRID_PIXEL_SIZE;
            int xDiff = target.x - current.x;
            int yDiff = target.y - current.y;
            if (xDiff > limit || xDiff < -limit || yDiff > limit || yDiff < -limit) {
                current.x = target.x;
                current.y = target.y;
            }
        }

        // Interpolate current position towards target position
        current.setLocation(approach(current.x, target.x, interpolation), approach(current.y, target.y, interpolation));
        gameObject.setX(current.x);
        gameObject.setY(current.y);
    }

    /**
     * @param current Current position
     * @param target Target position
     * @param interpolation How far to interpolate (if the difference is larger then the interpolation amount)
     * @return new interpolated position
     */
    private int approach(int current, int target, int interpolation) {
        int difference = target - current;
        if (difference > interpolation) return current + interpolation;
        if (difference < -interpolation) return current - interpolation;
        return target;
    }
}
