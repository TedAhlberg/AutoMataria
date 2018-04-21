package gameclient;

import common.Direction;
import common.Utility;
import gameobjects.*;

import java.awt.*;
import java.util.Collection;
import java.util.concurrent.*;

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
        if (targetPositions.get(player) == null) {
            targetPositions.put(player, player.getPoint());
        }
        currentPositions.put(player, targetPositions.get(player));
        targetPositions.put(player, player.getPoint());
    }

    /**
     * Interpolate current position towards target position
     *
     * @param player The Player to interpolate (Move closer to target position)
     */
    public void interpolate(Player player) {
        Point current = currentPositions.get(player);
        Point target = targetPositions.get(player);

        if (target == null) return;

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
