package common;

import java.awt.*;
import java.io.*;

/**
 * @author Johannes Blüml
 */
public abstract class GameObject implements Serializable {
    private static final long serialVersionUID = 1;
    protected int x, y, speedX, speedY;
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

    public int getSpeedX() {
        return speedX;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        return (o instanceof GameObject) && ((GameObject) o).getName().equals(this.getName());
    }
}
