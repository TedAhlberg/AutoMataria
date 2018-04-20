package gameclient;

import common.Direction;
import common.Utility;
import gameobjects.*;

import java.awt.*;
import java.util.HashMap;
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
    private final ConcurrentHashMap<GameObject, Direction> clientDirection = new ConcurrentHashMap<>();
    private int tickRate = 100, updateRate = 200;
    private double currentDeltaTime;

    /**
     * Adds a target that a current object will be interpolated towards
     *
     * @param gameObject A gameobject that will be used as target for the same gameobjects current position
     */
    public void addTarget(GameObject gameObject) {
        currentPositions.put(gameObject, gameObject.getPoint());

        if (clientDirection.containsKey(gameObject)) {
            // If player has changed direction locally check if we can change direction then change the interpolation target
            if (clientDirection.get(gameObject) != gameObject.getDirection()) {
                Point canChange = Utility.canChangeDirection(clientDirection.get(gameObject), gameObject.getPoint(), gameObject.getSpeed());
                if (canChange != null) {
                    gameObject.setPoint(canChange);
                    gameObject.setDirection(clientDirection.get(gameObject));
                    System.out.println("----> CLIENT CHANGED DIRECTION TO " + gameObject.getDirection());
                }
            }
        }

        int distanceToNextUpdate = gameObject.getSpeed() * (updateRate / tickRate);
        Point targetPoint = gameObject.getPoint();
        switch (gameObject.getDirection()) {
            case Up:
                targetPoint.y -= distanceToNextUpdate;
                break;
            case Down:
                targetPoint.y += distanceToNextUpdate;
                break;
            case Left:
                targetPoint.x -= distanceToNextUpdate;
                break;
            case Right:
                targetPoint.x += distanceToNextUpdate;
                break;
        }
        targetPositions.put(gameObject, targetPoint);
    }

    public void changeDirection(GameObject gameObject, Direction direction) {
        clientDirection.put(gameObject, direction);
    }

    /**
     * Interpolate current position towards target position
     *
     * @param gameObject The GameObject to interpolate (Move closer to target position)
     */
    public void interpolate(GameObject gameObject) {

        Point current = currentPositions.get(gameObject);

        if (clientDirection.containsKey(gameObject)) {
            // If player has changed direction locally check if we can change direction then change the interpolation target
            if (clientDirection.get(gameObject) != gameObject.getDirection()) {
                Point canChange = Utility.canChangeDirection(clientDirection.get(gameObject), current, gameObject.getSpeed());
                if (canChange != null) {
                    current = canChange;
                    gameObject.setPoint(canChange);
                    gameObject.setDirection(clientDirection.get(gameObject));
                    System.out.println("----> CLIENT CHANGED DIRECTION TO " + gameObject.getDirection());
                    addTarget(gameObject);
                }
            }
        }

        Point target = targetPositions.get(gameObject);

        int speedPerSecond = (1000 / tickRate) * gameObject.getSpeed();
        double interpolation = currentDeltaTime * speedPerSecond;
        double interpolatedX = approach(current.x, target.x, interpolation);
        double interpolatedY = approach(current.y, target.y, interpolation);
        if (gameObject instanceof Player) {
            Trail trail = ((Player) gameObject).getTrail();
            if (trail != null) {
                trail.grow(current, new Point((int) Math.ceil(interpolatedX), (int) Math.ceil(interpolatedY)));
            }
        }
        current.x = (int) Math.ceil(interpolatedX);
        current.y = (int) Math.ceil(interpolatedY);
        gameObject.setPoint(current);
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
     * @param updateRate Update rate from server in milliseconds
     */
    public void setUpdateRate(int updateRate) {
        this.updateRate = updateRate;
    }

    /**
     * @param currentDeltaTime Time in seconds since the last rendered frame was completed in the game loop
     */
    public void setCurrentDeltaTime(double currentDeltaTime) {
        this.currentDeltaTime = currentDeltaTime;
    }
}
