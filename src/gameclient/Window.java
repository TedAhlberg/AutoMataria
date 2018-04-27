package gameclient;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author Johannes Blüml
 */
public class Window extends JFrame {
    private Dimension windowSize;

    public Window(String title) {
        this(title, null);
    }

    public Window(String title, Dimension windowSize) {
        this.windowSize = windowSize;
        this.getContentPane().setBackground(Color.BLACK);
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            Font orbitronFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Orbitron Bold.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(orbitronFont);

            changeDefaultFont();
            modifyLookAndFeel();

        } catch (IOException | FontFormatException e) {
            System.out.println("Failed to load font.");
            e.printStackTrace();
        }

        setMode(Mode.Windowed);
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
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
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

    public void setMode(Mode mode) {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (mode == Mode.Fullscreen) {
            System.out.println("Changing to fullscreen mode.");

            this.dispose();
            this.setUndecorated(true);
            this.setResizable(false);

            device.setFullScreenWindow(this);

        } else if (mode == Mode.Maximized) {
            System.out.println("WINDOW: Changing to maximized window.");

            this.dispose();
            this.setUndecorated(true);
            this.setResizable(false);

            this.setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
            this.setVisible(true);
        } else {
            if (windowSize == null) {
                System.out.println("No windowsize defined. Using 1/4 of available screen.");
                windowSize = new Dimension(device.getDisplayMode().getWidth() / 2, device.getDisplayMode().getHeight() / 2);
            }
            System.out.println("WINDOW: Changing to windowed mode. Dimensions: " + windowSize.getWidth() + "x" + windowSize.getHeight());

            this.dispose();
            this.setUndecorated(false);
            this.setResizable(true);

            this.setPreferredSize(windowSize);

            this.setVisible(true);
        }
    }

    public void setWindowSize(Dimension windowSize) {
        this.windowSize = windowSize;
        setMode(Mode.Windowed);
    }

    public enum Mode {
        Fullscreen,
        Windowed,
        Maximized
    }
}
