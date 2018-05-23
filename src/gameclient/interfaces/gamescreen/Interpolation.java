package gameclient.interfaces.gamescreen;

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
        currentPositions.putIfAbsent(player, previousTarget);
        targetPositions.put(player, newTarget);

        // If this player is 1.5 times or more behind the difference between
        // server updates the player will be forced to the server position
        double differencePlayer = difference(currentPositions.get(player), newTarget);
        double differenceBetweenUpdates = difference(previousTarget, newTarget);
        if (differencePlayer > differenceBetweenUpdates * 1.5) {
            currentPositions.put(player, newTarget);
        }
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

        Point target = targetPositions.get(player);
        if (target == null) return;

        double speedPerMillisecond = player.getSpeed() / (double) tickRate;
        double interpolation = currentDeltaTime * speedPerMillisecond;

        Point current = currentPositions.get(player);
        current.x = (int) Math.ceil(approach(current.x, target.x, interpolation));
        current.y = (int) Math.ceil(approach(current.y, target.y, interpolation));
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
     * @param currentDeltaTime Time in milliseconds since the last rendered frame was completed in the game loop
     */
    public void setCurrentDeltaTime(double currentDeltaTime) {
        this.currentDeltaTime = currentDeltaTime;
    }
}
