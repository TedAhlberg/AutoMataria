package gameclient;

import common.Direction;
import gameobjects.GameObject;
import gameobjects.Player;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interpolation handles the smooth movement of a character between updates from the server
 * Related requirements:
 * SF001, Interpolering
 *
 * @author Johannes Blüml
 */
public class Interpolation {
    private final ConcurrentHashMap<GameObject, Point> currentPositions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<GameObject, Point> targetPositions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<GameObject, Double> differenceBetweenUpdates = new ConcurrentHashMap<>();
    private int tickRate = 100;
    private double currentDeltaTime;

    /**
     * Adds a target that a current object will be interpolated towards
     *
     * @param player A gameobject that will be used as target for the same gameobjects current position
     */
    public void addTarget(Player player) {
        Point previousTarget = targetPositions.get(player);
        Point newTarget = player.getPoint();
        if (previousTarget == null) {
            previousTarget = newTarget;
        }
        currentPositions.put(player, previousTarget);
        targetPositions.put(player, newTarget);

        differenceBetweenUpdates.put(player, difference(previousTarget, newTarget));
    }

    /**
     * Calculates the difference between two Points
     *
     * @param p1 First Point
     * @param p2 Second Point
     * @return Distance between the two Points
     */
    public double difference(Point p1, Point p2) {
        return Math.sqrt(
                (p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + ((p1.getY() - p2.getY()) * (p1.getY() - p2.getY()))
        );
    }

    /**
     * Interpolate current position towards target position
     *
     * @param player The Player to interpolate (Move closer to target position)
     */
    public void interpolate(Player player) {
        if (player.getDirection() == Direction.Static) return;
        Point current = currentPositions.get(player);
        Point target = targetPositions.get(player);

        if (target == null) return;
        if (current.equals(target)) {
            // Since we are at the target position which only happens if server updates are delayed
            // a new target position is calculated based on the last difference between updates
            double difference = differenceBetweenUpdates.get(player) / 2; // division by 2 for smoother movement
            switch (player.getDirection()) {
                case Left:
                    target = new Point(current.x - (int) difference, current.y);
                    targetPositions.put(player, target);
                    break;
                case Right:
                    target = new Point(current.x + (int) difference, current.y);
                    targetPositions.put(player, target);
                    break;
                case Up:
                    target = new Point(current.x, current.y - (int) difference);
                    targetPositions.put(player, target);
                    break;
                case Down:
                    target = new Point(current.x, current.y + (int) difference);
                    targetPositions.put(player, target);
                    break;
            }
        }

        int speedPerSecond = (1000 / tickRate) * player.getSpeed();
        double interpolation = currentDeltaTime * speedPerSecond;
        double interpolatedX = approach(current.x, target.x, interpolation);
        double interpolatedY = approach(current.y, target.y, interpolation);

        current.x = (int) Math.ceil(interpolatedX);
        current.y = (int) Math.ceil(interpolatedY);
        player.setPoint(current);
    }

    /**
     * @param current       Current position
     * @param target        Target position
     * @param interpolation How far to interpolate (if the difference is larger then the interpolation amount)
     * @return new interpolated position
     */
    private double approach(int current, int target, double interpolation) {
        int difference = target - current;
        if (difference > interpolation) return current + interpolation;
        if (difference < -interpolation) return current - interpolation;
        return target;
    }

    /**
     * @param tickRate Tick rate on server in milliseconds
     */
    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }

    /**
     * @param currentDeltaTime Time in seconds since the last rendered frame was completed in the game loop
     */
    public void setCurrentDeltaTime(double currentDeltaTime) {
        this.currentDeltaTime = currentDeltaTime;
    }
}
