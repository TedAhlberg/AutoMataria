package gameobjects;

import common.Direction;
import common.GameMap;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Johannes Blüml
 */
public class Player extends GameObject {
    private GameMap map;
    private ConcurrentLinkedQueue<Direction> inputQueue;
    private Trail trail;
    private String name;
    private Color color;
    private boolean dead;
    private int gridPositionX, gridPositionY, previousGridPositionX, previousGridPositionY;

    public Player(int x, int y, String name, Color color, GameMap map) {
        super(x, y);
        previousGridPositionX = x;
        previousGridPositionY = y;
        this.width = map.getGridMultiplier();
        this.height = map.getGridMultiplier();
        this.name = name;
        this.color = color;
        this.map = map;
        this.inputQueue = new ConcurrentLinkedQueue<>();
        this.trail = new Trail(color, map);
        map.add(trail);
    }

    public void render(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setFont(new Font("Orbitron", Font.BOLD, 100));
        String displayName = name.toUpperCase();
        if (dead) displayName += " (DEAD)";
        g.drawString(displayName, x, y - 50);
    }

    public void tick() {
        if (dead) return;

        updateDirection();
        move();
        growTrail();

        checkCollisions();
    }

    private void updateDirection() {
        if (inputQueue.isEmpty()) return;

        while (inputQueue.peek() == direction) {
            inputQueue.remove();
        }

        if (inputQueue.isEmpty()) return;

        if (x % map.getGridMultiplier() == 0 && y % map.getGridMultiplier() == 0) {
            direction = inputQueue.remove();
        }
    }

    private void move() {
        switch (direction) {
            case Up: y -= speed;
                break;
            case Down: y += speed;
                break;
            case Left: x -= speed;
                break;
            case Right: x += speed;
                break;
            default: return;
        }
        // Teleport to other side if player is outside the map
        if (x < 0) {
            x += map.getWidth();
        } else if (x > map.getWidth()) {
            x -= map.getWidth();
        } else if (y < 0) {
            y += map.getHeight();
        } else if (y > map.getHeight()) {
            y -= map.getHeight();
        }
    }

    private void growTrail() {
        gridPositionX = x / map.getGridMultiplier();
        gridPositionY = y / map.getGridMultiplier();
        if (gridPositionX != previousGridPositionX || gridPositionY != previousGridPositionY) {
            trail.add(previousGridPositionX, previousGridPositionY);
            previousGridPositionX = gridPositionX;
            previousGridPositionY = gridPositionY;
        }
    }

    private void checkCollisions() {
        for (GameObject object : map.getGameObjects()) {
            if (this.equals(object) || dead) continue;
            if ((object instanceof Player) && this.getBounds().intersects(object.getBounds())) {
                setDead();
                ((Player) object).setDead();
                System.out.println(name + " HAS CRASHED WITH " + ((Player) object).getName());
            } else if ((object instanceof Wall) && this.getBounds().intersects(object.getBounds())) {
                setDead();
                System.out.println(name + " CRASHED INTO A WALL");
            } else if (object instanceof Trail && ((Trail) object).contains(gridPositionX, gridPositionY)) {
                setDead();
                System.out.println(name + " CRASHED INTO A TRAIL");
            }
        }
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead() {
        direction = Direction.Static;
        dead = true;
    }

    public void setDirection(Direction direction) {
        inputQueue.add(direction);
    }

    public String toString() {
        return "Player: " + name + " Position: x=" + x + ", y=" + y + " Speed: " + speed + " Direction: " + direction;
    }
}
