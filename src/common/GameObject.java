package common;

import java.awt.*;
import java.io.Serializable;

/**
 * @author Johannes Blüml
 */
public abstract class GameObject implements Serializable {
    private static final long serialVersionUID = 1;
    protected int x, y, speed, width = 100, height = 100;
    protected Direction direction = Direction.Static;
    protected String name;

    public GameObject(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public abstract void tick();

    public abstract void render(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public String getName() {
        return name;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean equals(Object o) {
        return (o instanceof GameObject) && ((GameObject) o).getName().equals(this.getName());
    }
}
