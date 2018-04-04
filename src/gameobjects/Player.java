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
    private Direction previousDirection;

    public Player(int x, int y, String name, Color color, GameMap map) {
        super(x, y);
        this.width = map.getGridMultiplier();
        this.height = map.getGridMultiplier();
        this.name = name;
        this.color = color;
        this.map = map;
        this.inputQueue = new ConcurrentLinkedQueue<>();
        this.trail = new Trail(this);
        this.previousDirection = direction;
        map.add(trail);
    }

    public void render(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(color.darker());
        Font font = new Font("Orbitron", Font.BOLD, 100);
        g.setFont(font);
        String displayName = name.toUpperCase();
        FontMetrics fontMetrics = g.getFontMetrics(font);
        int stringWidth = fontMetrics.stringWidth(displayName);
        if (dead) displayName += " (DEAD)";
        g.drawString(displayName, x + (map.getGridMultiplier() / 2) - (stringWidth / 2), y - 50);
    }

    public void tick() {
        if (dead) return;

        Point previousPosition = new Point(x, y);

        updateDirection();
        move();

        Point newPosition = new Point(x, y);
        trail.grow(previousPosition, newPosition);

        checkCollisions();
    }

    private void updateDirection() {
        previousDirection = direction;
        if (inputQueue.isEmpty()) return;

        while (inputQueue.peek() == direction) {
            inputQueue.remove();
        }

        if (inputQueue.isEmpty()) return;

        if (x % map.getGridMultiplier() == 0 && y % map.getGridMultiplier() == 0) {
            previousDirection = direction;
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
            } else if ((object instanceof Trail) && ((Trail) object).intersects(this.getBounds())) {
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

    public Direction getPreviousDirection() {
        return previousDirection;
    }
}
