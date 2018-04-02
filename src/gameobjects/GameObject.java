package gameobjects;

import common.Direction;
import common.ID;

import java.awt.*;
import java.io.Serializable;

/**
 * @author Johannes Blüml
 */
public abstract class GameObject implements Serializable {
    private static final long serialVersionUID = 1;
    protected final int id;
    protected int x, y, speed, width, height;
    protected Direction direction = Direction.Static;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
        this.id = ID.getNext();
    }

    public int hashCode() {
        return id;
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

    public int getId() {
        return id;
    }

    public boolean isAtSamePositionAs(GameObject o) {
        return x == o.getX() && y == o.getY();
    }

    public boolean equals(Object o) {
        return (o instanceof GameObject) && ((GameObject) o).id == this.id;
    }
}
