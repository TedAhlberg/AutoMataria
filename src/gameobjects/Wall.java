package gameobjects;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.Collection;

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

    public Wall(Wall object) {
        this(object.getColor(), object.getBorderColor());
        this.shape = object.getShape();
    }

    public Wall(Color color, Color borderColor) {
        this.color = color;
        this.borderColor = borderColor;
    }

    /**
     * Adds a Shape object to be part of the Wall
     *
     * @param shape A Shape object to add to the Wall
     */
    public void add(Shape shape) {
        Area area = new Area();
        if (this.shape != null) {
            area.add(new Area(this.shape));
        }
        area.add(new Area(shape));
        this.shape = AffineTransform.getTranslateInstance(0, 0).createTransformedShape(area);
    }

    /**
     * Adds a Collection of Shape objects to be part of the Wall
     *
     * @param shape A Collection of Shape object to add to the Wall
     */
    public void add(Collection<Shape> shape) {
        Area area = new Area();
        if (this.shape != null) {
            area.add(new Area(this.shape));
        }
        for (Shape s : shape) {
            area.add(new Area(s));
        }
        this.shape = AffineTransform.getTranslateInstance(0, 0).createTransformedShape(area);
    }

    /**
     * Removes a Shape from the wall. It is possible to poke holes in the wall using this method.
     *
     * @param shape Shape to remove from the Wall
     */
    public void remove(Shape shape) {
        if (this.shape == null) return;
        Area area = new Area(this.shape);
        area.subtract(new Area(shape));
        this.shape = AffineTransform.getTranslateInstance(0, 0).createTransformedShape(area);
    }

    /**
     * Removes a Collection of shapes from the wall. It is possible to poke holes in the wall using this method.
     *
     * @param shape Collection of Shapes to remove from the Wall
     */
    public void remove(Collection<Shape> shape) {
        if (shape == null) return;
        Area area = new Area(this.shape);
        for (Shape s : shape) {
            area.subtract(new Area(s));
        }
        this.shape = AffineTransform.getTranslateInstance(0, 0).createTransformedShape(area);
    }

    /**
     * Removes the wall completely
     */
    public void clear() {
        shape = null;
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Shape getShape() {
        return shape;
    }

    @Override
    public String toString() {
        return "Wall{" +
                "id=" + id +
                ", color=" + color +
                ", borderColor=" + borderColor +
                '}';
    }
}
