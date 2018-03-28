package common;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Johannes Blüml
 */
public class Player extends GameObject {
    private Direction previousDirection;
    private GameMap map;
    private ConcurrentLinkedQueue<Direction> inputQueue;
    private Trail lastTrail;
    private Color color;
    private boolean dead;
    private boolean hasTeleported;

    public Player(int x, int y, String name, Color color, GameMap map) {
        super(x, y, name);
        this.width = map.getGridSize();
        this.height = map.getGridSize();
        this.color = color;
        this.map = map;
        this.previousDirection = direction;
        this.inputQueue = new ConcurrentLinkedQueue<>();
    }

    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setFont(new Font("Orbitron", Font.BOLD, 100));
        String name = this.getName().toUpperCase();
        if (dead) name += " (DEAD)";
        g.drawString(name, x, y - 50);
    }

    public void tick() {
        if (dead) return;

        if (inputQueue.isEmpty()) {
        } else if (inputQueue.peek() == direction) {
            inputQueue.remove();
        } else if (x % map.getGridSize() == 0 && y % map.getGridSize() == 0) {
            previousDirection = direction;
            direction = inputQueue.remove();
        }
        move();

        checkCollisions();
    }

    private void teleportIfOutsideMap() {
        if (x < 0) {
            x += map.getWidth();
            hasTeleported = true;
        } else if (x > map.getWidth()) {
            x -= map.getWidth();
            hasTeleported = true;
        } else if (y < 0) {
            y += map.getHeight();
            hasTeleported = true;
        } else if (y > map.getHeight()) {
            y -= map.getHeight();
            hasTeleported = true;
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
        teleportIfOutsideMap();
        growTrail();
    }

    private void growTrail() {
        if (previousDirection != direction || hasTeleported) {
            if (hasTeleported) {
                lastTrail.grow(direction, width);
                hasTeleported = false;
            }
            lastTrail = new Trail(this);
            map.getGameObjects().add(lastTrail);
        } else if (lastTrail != null) {
            lastTrail.grow(direction, speed);
        }
    }

    private void checkCollisions() {
        map.getGameObjects().forEach(object -> {
            if (this.equals(object) || dead) return;
            if ((object instanceof Player || object instanceof Wall) && this.getBounds().intersects(object.getBounds())) {
                this.dead = true;
                System.out.println(this.getName() + " HAS CRASHED WITH " + object.getName());
            }
        });
    }

    public String toString() {
        return "Player: " + name + " Position: x=" + x + ", y=" + y + " Speed: " + speed + " Direction: " + direction;
    }

    public void setDirection(Direction direction) {
        inputQueue.add(direction);
    }

    public Color getColor() {
        return color;
    }
}
