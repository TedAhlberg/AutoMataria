package gameclient;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author Johannes Blüml
 */
public class Window extends JFrame {

    public Window(String title) {
        this(title, null);
    }

    public Window(String title, Dimension windowSize) {

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            env.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Orbitron Bold.ttf")));
            FontUIResource defaultFont = new FontUIResource("Orbitron", Font.BOLD, 20);

            Enumeration keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof FontUIResource) {
                    UIManager.put(key, defaultFont);
                }
                if (key.toString().startsWith("Button")) {
                    System.out.println(key + " " + value);
                }
            }

            // Look and Feel changes
            UIManager.put("Panel.background", new ColorUIResource(0, 0, 0));
            UIManager.put("Label.foreground", new ColorUIResource(255, 255, 255));

            UIManager.put("ComboBox.background", new ColorUIResource(255, 255, 255));
            UIManager.put("ComboBox.foreground", new ColorUIResource(0, 0, 0));

            UIManager.put("ComboBox.selectionBackground", new ColorUIResource(0, 0, 0));
            UIManager.put("ComboBox.selectionForeground", new ColorUIResource(255, 255, 255));

            UIManager.put("Button.background", new ColorUIResource(0, 0, 0));
            UIManager.put("Button.foreground", new ColorUIResource(255, 255, 255));

        } catch (IOException | FontFormatException e) {
            System.out.println("Failed to load font.");
            e.printStackTrace();
        }

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
