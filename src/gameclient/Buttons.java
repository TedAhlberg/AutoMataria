package gameclient;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;

/**
 * 
 * @author Henrik Olofsson & Erik Lundow
 *
 */
public class Buttons extends JButton implements MouseListener {
    private BufferedImage imagePressed;
    private BufferedImage imageUnpressed;
    private boolean isPressed = false;

    public Buttons(String text) {
        this("Blank_Pressed.png", "Blank_Unpressed.png");
        this.setText(text);
    }

    public Buttons(String filenamePressed, String filenameUnpressed) {
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorder(null);

        this.imagePressed = Resources.getButtonImage(filenamePressed);
        this.imageUnpressed = Resources.getButtonImage(filenameUnpressed);

        this.addMouseListener(this);
    }

    public void mouseClicked(MouseEvent event) {

    }

    public void mouseEntered(MouseEvent event) {

    }

    public void mouseExited(MouseEvent event) {

    }

    public void mousePressed(MouseEvent event) {
        isPressed = true;
        repaint();
    }

    public void mouseReleased(MouseEvent event) {
        isPressed = false;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        if (isPressed) {
            g.drawImage(imagePressed, 0, 0, getWidth(), getHeight(), null);

        } else {
            g.drawImage(imageUnpressed, 0, 0, getWidth(), getHeight(), null);
        }
        super.paintComponent(g);
    }
}
