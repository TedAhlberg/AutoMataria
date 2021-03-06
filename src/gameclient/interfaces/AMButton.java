package gameclient.interfaces;

import gameclient.Resources;
import gameclient.sound.SoundFx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

/**
 * @author Henrik Olofsson & Erik Lundow
 */
public class AMButton extends JButton implements MouseListener {
    private SoundFx soundEffects = SoundFx.getInstance();
    private BufferedImage imagePressed;
    private BufferedImage imageUnpressed;
    private boolean isPressed = false;

    public AMButton(String text) {
        this("ButtonPressed.png", "Button.png");
        setText(text);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public AMButton(String filenamePressed, String filenameUnpressed) {

        imagePressed = Resources.getImage(filenamePressed);
        imageUnpressed = Resources.getImage(filenameUnpressed);

        setFocusPainted(false);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorder(null);
        setForeground(Color.WHITE);

        addMouseListener(this);
    }

    public void mouseClicked(MouseEvent event) {}

    public void mouseEntered(MouseEvent event) {}

    public void mouseExited(MouseEvent event) {}

    public void mousePressed(MouseEvent event) {
        soundEffects.menuSelect();
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
