package gameclient;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.Enumeration;

/**
 * Window is a customized JFrame that has some extra features.
 * Mainly for changing between Fullscreen/Windowed/Maximized
 * Also changes the look of swing components to match the Auto-Mataria look.
 *
 * @author Johannes Blüml
 */
public class Window extends JFrame {
    private Dimension windowSize;

    /**
     * Default window size will be a quarter of the screen size
     *
     * @param title The title of the window
     */
    public Window(String title) {
        this(title, null);
    }

    /**
     * Creates a new window with the specified window size
     *
     * @param title      The title of the window
     * @param windowSize the size of the window (in windowed mode)
     */
    public Window(String title, Dimension windowSize) {
        this.windowSize = windowSize;
        this.setBackground(Color.BLACK);
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        changeDefaultFont();
        modifyLookAndFeel();

        setMode(Mode.Windowed);
    }

    /**
     * Changes the default font for all swing components
     */
    private void changeDefaultFont() {
        FontUIResource defaultFontResource = new FontUIResource(Resources.getInstance().getDefaultFont());

        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, defaultFontResource);
            }
            /*
            if (key.toString().startsWith("Slider")) {
                System.out.println(key + " " + value);
            }
            */
        }
    }

    /**
     * Changes the look of Swing components that are used in the interface.
     * Mostly black background with white text.
     */
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

    /**
     * Changes the window to the specified Mode.
     * Fullscreen, Windowed or Maximized
     * Only windowed mode is resizeable.
     *
     * @param mode The Mode to change to. Fullscreen, Windowed or Maximized
     */
    public void setMode(Mode mode) {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        this.dispose();
        if (mode == Mode.Fullscreen) {
            System.out.println("Changing to fullscreen mode.");

            
            this.setUndecorated(true);
            this.setResizable(false);
            this.pack();

            device.setFullScreenWindow(this);

        } else if (mode == Mode.Maximized) {
            System.out.println("WINDOW: Changing to maximized window.");

            this.setVisible(false);
            this.setUndecorated(true);
            this.setResizable(false);

            this.setVisible(true);
            this.setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
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

    /**
     * Changes the size of the window
     * Also changes the Mode to Windowed if it's Fullscreen or Maximized
     *
     * @param windowSize The new size of the window
     */
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
