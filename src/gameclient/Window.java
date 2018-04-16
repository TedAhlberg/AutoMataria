package gameclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Johannes Blüml
 */
public class Window extends JFrame {

    public Window(String title) {
        this(title, null);
    }

    public Window(String title, Dimension windowSize) {
        this.getContentPane().setBackground(Color.BLACK);
        this.setTitle(title);
        this.setResizable(false);
        this.setUndecorated(true);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                System.exit(0);
            }
        });

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            env.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Orbitron Bold.ttf")));
        } catch (IOException | FontFormatException e) {
            System.out.println("Failed to load font.");
            e.printStackTrace();
        }

        if (windowSize == null) {
            env.getDefaultScreenDevice().setFullScreenWindow(this);
        } else {
            this.setPreferredSize(windowSize);
            this.setMinimumSize(windowSize);
            this.setMaximumSize(windowSize);
            this.setVisible(true);
        }
    }
}
