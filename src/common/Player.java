package common;

import gameclient.*;

import java.awt.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * @author Johannes Blüml
 */
public class Player extends GameObject implements Serializable {
    private static final long serialVersionUID = 1;
    private CopyOnWriteArrayList<GameObject> gameObjects;

    private Color color;

    public Player(int x, int y, String name, Color color, CopyOnWriteArrayList<GameObject> gameObjects) {
        super(x, y, name);
        this.color = color;
        this.gameObjects = gameObjects;
    }

    public void tick() {
        if (direction == Direction.Left) {
            x -= speed;
        } else if (direction == Direction.Right) {
            x += speed;
        } else if (direction == Direction.Up) {
            y -= speed;
        } else if (direction == Direction.Down) {
            y += speed;
        }

        x = Game.clamp(x, 0, Game.WIDTH - width - 5);
        y = Game.clamp(y, 0, Game.HEIGHT - height - 31);

        if (x == 0 || x == (Game.WIDTH - width - 5) || y == 0 || y == (Game.HEIGHT - height - 31)) {
            invertDirection();
        }

        gameObjects.forEach(object -> {
            if (this.equals(object)) return;
            if (object instanceof Player && this.getBounds().intersects(object.getBounds())) {
                invertDirection();
                speed += 2;
                System.out.println(this.getName() + " HAS CRASHED INTO " + object.getName());
            }
        });
    }

    public void invertDirection() {
        if (direction == Direction.Down) {
            direction = Direction.Up;
        } else if (direction == Direction.Up) {
            direction = Direction.Down;
        } else if (direction == Direction.Left) {
            direction = Direction.Right;
        } else if (direction == Direction.Right) {
            direction = Direction.Left;
        }
    }

    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setFont(new Font("Orbitron", Font.BOLD, 20));
        g.drawString(this.getName().toUpperCase(), x, y - 14);
    }

    public String toString() {
        return "Player: " + name + " Position: x=" + x + ", y=" + y + " Speed: " + speed + " Direction: " + direction;
    }
}
