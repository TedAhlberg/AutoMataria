package gameobjects;

import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class Wall extends GameObject {
    private final Color color;

    public Wall(int x, int y, int width, int height, Color color) {
        super(x, y);
        this.color = color;
        this.width = width;
        this.height = height;
    }

    public void tick() {}

    public void render(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}
