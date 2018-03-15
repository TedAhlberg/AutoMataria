package common;

import gameclient.*;

import java.awt.*;
import java.io.*;

/**
 * @author Johannes Blüml
 */
public class Player extends GameObject implements Serializable {
    private static final long serialVersionUID = 1;
    private final Color color;

    public Player(int x, int y, String name) {
        super(x, y, name);
        speedX = 4;
        this.color = Color.RED;
    }

    public Color getColor() {
        return color;
    }

    public void tick() {
        x += speedX;
        y += speedY;

        x = Game.clamp(x, 0, Game.WIDTH - 16 - 5);
        y = Game.clamp(y, 0, Game.HEIGHT - 16 - 31);
    }

    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, 16, 16);
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.drawString(this.getName(), x, y - 14);
    }

    public String toString() {
        return "Player: " + name + " Position: x=" + x + ", y=" + y + " Speed: x=" + speedX + ", y=" + speedY;
    }
}
