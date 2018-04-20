package gameclient;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

/**
 * 
 * @author Henrik Olofsson & Erik Lundow
 *
 */
public class Buttons extends JButton implements MouseListener {
    private BufferedImage imagePressed;
    private BufferedImage imageUnpressed;
    private int width = 100, height = 50;
    private boolean isPressed = false;
    private States state = States.Unpressed;

    private enum States {
        Pressed, Unpressed;
    };

    public Buttons(String filenamePressed, String filenameUnpressed) {
        this.setOpaque(false);
        this.setBorder(null);

        this.imagePressed = Resources.getButtonImage(filenamePressed);
        this.imageUnpressed = Resources.getButtonImage(filenameUnpressed);

        this.addMouseListener(this);
    }

    public void mouseClicked(MouseEvent play) {

    }

    public void mouseEntered(MouseEvent play) {

    }

    public void mouseExited(MouseEvent play) {

    }

    public void mousePressed(MouseEvent play) {
        state = States.Pressed;
        isPressed = true;
        repaint();
    }

    public void mouseReleased(MouseEvent play) {
        state = States.Unpressed;
        isPressed = false;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        if (state == States.Pressed) {
            g.drawImage(imagePressed, 0, 0, width, height, null);

        } else if (state == States.Unpressed) {
            g.drawImage(imageUnpressed, 0, 0, width, height, null);

        }

    }

    public void setWidth(int width) {
        this.width = width;
        setSize();
        repaint();
    }

    public void setHeight(int height) {
        this.height = height;
        setSize();
        repaint();

    }
    
    public void setSize() {
        this.setPreferredSize(new Dimension(width, height));
    }

    public boolean isPressed() {
        return isPressed;
    }
}
