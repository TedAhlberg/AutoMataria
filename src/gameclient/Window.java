package gameclient;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

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

            changeDefaultFont();
            modifyLookAndFeel();

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

        // Workaround for MAC problem with fullscreen - ComboBox not working
//        if (System.getProperty("os.name").contains("Mac OS")) {
            setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
            setVisible(true);
//        } else {
//            if (windowSize == null) {
//                env.getDefaultScreenDevice().setFullScreenWindow(this);
//            } else {
//                this.setPreferredSize(windowSize);
//                this.setMinimumSize(windowSize);
//                this.setMaximumSize(windowSize);
//                this.setVisible(true);
//            }
//        }

        this.requestFocus();
    }

    private void changeDefaultFont() {
        FontUIResource defaultFont = new FontUIResource("Orbitron", Font.BOLD, 12);

        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, defaultFont);
            }
            /*
            if (key.toString().startsWith("Slider")) {
                System.out.println(key + " " + value);
            }
            */
        }
    }

    private void modifyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel()); 
        } catch (UnsupportedLookAndFeelException e) {e.printStackTrace();
        }
        ColorUIResource backgroundColor = new ColorUIResource(0, 0, 0);

        UIManager.put("Panel.background", backgroundColor);
        UIManager.put("Label.foreground", new ColorUIResource(255, 255, 255));

        UIManager.put("ComboBox.background", new ColorUIResource(255, 255, 255));
        UIManager.put("ComboBox.foreground", backgroundColor);

        UIManager.put("ComboBox.selectionBackground", backgroundColor);
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(255, 255, 255));

        UIManager.put("Slider.background", backgroundColor);
    }
}
