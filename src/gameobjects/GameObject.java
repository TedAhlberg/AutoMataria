package gameobjects;

import common.Direction;

import java.awt.*;
import java.io.Serializable;

/**
 * @author Johannes Blüml
 */
public abstract class GameObject implements Serializable {
    private static final long serialVersionUID = 2;
    protected int id = 0;
    protected int x, y, speed, width, height;
    protected Direction direction = Direction.Static;

    public GameObject() {
        this(0, 0);
    }

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void tick();

    public abstract void render(Graphics2D g);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
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

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean equals(Object obj) {
        return obj instanceof GameObject && ((GameObject) obj).getId() == id;
    }

    public int hashCode() {
        return id;
    }
}
