package common;

import gameserver.GameServer;

import java.awt.*;
import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Johannes Blüml
 */
public class Player extends GameObject implements Serializable {
    private static final long serialVersionUID = 1;
    private Direction previousDirection;
    private CopyOnWriteArrayList<GameObject> gameObjects;
    private ConcurrentLinkedQueue<Direction> inputQueue;
    private Trail lastTrail;

    private Color color;
    private boolean dead;
    private boolean hasTeleported;

    public Player(int x, int y, String name, Color color, CopyOnWriteArrayList<GameObject> gameObjects) {
        super(x, y, name);
        this.color = color;
        this.gameObjects = gameObjects;
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
            move(direction, speed);
        } else if (inputQueue.peek() == direction) {
            inputQueue.remove();
            move(direction, speed);
        } else if (x % GameServer.GRIDSIZE == 0 && y % GameServer.GRIDSIZE == 0) {
            previousDirection = direction;
            direction = inputQueue.remove();
            move(direction, width);
        } else {
            move(direction, speed);
        }
        checkCollisions();
    }

    private void teleportIfOutsideMap() {
        if (x < 0) {
            x = GameServer.WIDTH;
            hasTeleported = true;
        } else if (x > GameServer.WIDTH) {
            x = 0;
            hasTeleported = true;
        } else if (y < 0) {
            y = GameServer.HEIGHT;
            hasTeleported = true;
        } else if (y > GameServer.HEIGHT) {
            y = 0;
            hasTeleported = true;
        }
    }

    private void move(Direction direction, int amount) {
        switch (direction) {
            case Up: y -= amount;
                break;
            case Down: y += amount;
                break;
            case Left: x -= amount;
                break;
            case Right: x += amount;
                break;
            default: return;
        }
        teleportIfOutsideMap();
        growTail(direction, amount);
    }

    private void growTail(Direction direction, int amount) {
        if (previousDirection != direction || hasTeleported) {
            if (hasTeleported) {
                lastTrail.grow(direction, amount);
                hasTeleported = false;
            }
            lastTrail = new Trail(this);
            gameObjects.add(lastTrail);
        } else if (lastTrail != null) {
            lastTrail.grow(direction, amount);
        }
    }

    private void checkCollisions() {
        gameObjects.forEach(object -> {
            if (this.equals(object)) return;
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
