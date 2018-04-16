package gameobjects;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * A Wall is GameObject that has a shape that can be changed into any shape by adding or removing Rectangles
 *
 * @author Johannes Blüml
 */
public class Wall extends GameObject {
    private static final long serialVersionUID = 2;
    private Color color, borderColor;
    private Shape shape;

    public Wall() {
        this(Color.CYAN.darker().darker(), null);
    }

    public Wall(Color color) {
        this(color, null);
    }

    public Wall(Color color, Color borderColor) {
        this.color = color;
        this.borderColor = borderColor;
    }

    /**
     * Changes the color of the Wall
     *
     * @param color The new color of the Wall
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Changes the border color of the Wall
     *
     * @param borderColor The new border color of the Wall
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Adds a Rectangle object to be part of the Wall
     *
     * @param rectangle Rectangle object to add to the Wall
     */
    public void add(Rectangle rectangle) {
        Area area = new Area();
        if (shape != null) {
            area.add(new Area(shape));
        }
        area.add(new Area(rectangle));
        shape = AffineTransform.getTranslateInstance(0, 0).createTransformedShape(area);
    }

    /**
     * Removes a Rectangle shape from the wall. It is possible to poke holes in the wall using this method.
     *
     * @param rectangle Rectangle to remove from the Wall
     */
    public void remove(Rectangle rectangle) {
        Area area = new Area();
        if (shape != null) {
            area.add(new Area(shape));
        }
        area.subtract(new Area(rectangle));
        shape = AffineTransform.getTranslateInstance(0, 0).createTransformedShape(area);
    }

    /**
     * Check if provided rectangle is inside this Wall GameObject
     *
     * @param rectangle Rectangle that you want to test
     * @return true if provided recangle is partially or completely inside the Wall otherwise false
     */
    public boolean intersects(Rectangle rectangle) {
        if (shape == null) return false;
        return shape.intersects(rectangle);
    }

    public void tick() {}

    /**
     * Paints the Wall shape to the provided Graphics object
     *
     * @param g Graphics2D object to paint on
     */
    public void render(Graphics2D g) {
        if (shape == null) return;

        g.setColor(color);
        g.fill(shape);

        if (borderColor != null) {
            g.setColor(borderColor);
            g.draw(shape);
        }
    }
}
