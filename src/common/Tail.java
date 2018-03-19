package common;

import java.awt.*;
import java.io.Serializable;

/**
 * @author Johannes Blüml
 */
public class Tail extends GameObject implements Serializable {
    private final Color color;

    public Tail(Player player) {
        super(player.getX(), player.getY(), player.getName() + "'s tail");
        width = player.getWidth();
        height = player.getHeight();
        color = player.getColor();

        if (player.getDirection() == Direction.Up) {
            y += height;
        } else if (player.getDirection() == Direction.Down) {
            y -= height;
        } else if (player.getDirection() == Direction.Left) {
            x += width;
        } else if (player.getDirection() == Direction.Right) {
            x -= width;
        }
    }

    public void grow(Direction direction, int size) {
        if (direction == Direction.Up) {
            y -= size;
            height += size;
        } else if (direction == Direction.Down) {
            height += size;
        } else if (direction == Direction.Left) {
            x -= size;
            width += size;
        } else if (direction == Direction.Right) {
            width += size;
        }
    }

    public void tick() {}

    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}
