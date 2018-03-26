package common;

import gameclient.Game;

import java.awt.*;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Johannes Blüml
 */
public class Player extends GameObject implements Serializable {
    private static final long serialVersionUID = 1;
    private Direction previousDirection;
    private CopyOnWriteArrayList<GameObject> gameObjects;
    private Tail lastTail;

    private Color color;
    private boolean dead;
    private boolean teleported;

    public Player(int x, int y, String name, Color color, CopyOnWriteArrayList<GameObject> gameObjects) {
        super(x, y, name);
        this.color = color;
        this.gameObjects = gameObjects;
        this.previousDirection = direction;
    }

    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setFont(new Font("Orbitron", Font.BOLD, 20));
        String name = this.getName().toUpperCase();
        if (dead) name += " (DEAD)";
        g.drawString(name, x, y - 12);
    }

    public void tick() {
        if (dead) return;

        movePlayer();
        teleportIfOutsideMap();
        growTail();
        checkCollisions();

        previousDirection = direction;
    }

    private void teleportIfOutsideMap() {
        if (x < 0) {
            x = Game.WIDTH;
            teleported = true;
        } else if (x > Game.WIDTH) {
            x = 0;
            teleported = true;
        } else if (y < 0) {
            y = Game.HEIGHT;
            teleported = true;
        } else if (y > Game.HEIGHT) {
            y = 0;
            teleported = true;
        }
    }

    private void move(int amount) {
        switch (direction) {
            case Up: y -= amount;
                break;
            case Down: y += amount;
                break;
            case Left: x -= amount;
                break;
            case Right: x += amount;
                break;
        }
    }

    private void movePlayer() {
        if (previousDirection == direction) move(speed);
        else move(width);
    }

    private void growTail() {
        if (previousDirection != direction || teleported) {
            if (teleported) {
                lastTail.grow(direction, width);
                teleported = false;
            }
            lastTail = new Tail(this);
            gameObjects.add(lastTail);
        } else if (lastTail != null) {
            lastTail.grow(direction, speed);
        }
    }

    private void checkCollisions() {
        gameObjects.forEach(object -> {
            if (this.equals(object)) return;
            if ((object instanceof Player || object instanceof Tail) && this.getBounds().intersects(object.getBounds())) {
                this.dead = true;
                System.out.println(this.getName() + " HAS CRASHED WITH " + object.getName());
            }
        });
    }

    public String toString() {
        return "Player: " + name + " Position: x=" + x + ", y=" + y + " Speed: " + speed + " Direction: " + direction;
    }

    public Color getColor() {
        return color;
    }
}
