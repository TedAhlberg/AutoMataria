package gameclient.interfaces;

import gameclient.Resources;

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
        setBackground(Color.BLACK);
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        changeDefaultFont();
        modifyLookAndFeel();
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
            if (key.toString().startsWith("ScrollPane")) {
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

        UIManager.put("ScrollPane.background", backgroundColor);
        UIManager.put("TextPane.background", backgroundColor);
        UIManager.put("ScrollPane.border", "");

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

        if (device.getFullScreenWindow() != null) {
            System.out.println("WINDOW: Exiting fullscreen mode.");
            device.setFullScreenWindow(null);
            setAlwaysOnTop(false);
        }

        if (isDisplayable()) {
            System.out.println("WINDOW: Is already active. Disposing window before changing mode.");
            dispose();
        }

        if (mode == Mode.Fullscreen) {
            System.out.println("WINDOW: Changing to fullscreen mode.");

            setUndecorated(true);
            setResizable(false);
            device.setFullScreenWindow(this);

        } else if (mode == Mode.Maximized) {
            System.out.println("WINDOW: Changing to maximized window.");

            setUndecorated(true);
            setResizable(false);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setVisible(true);

        } else if (mode == Mode.Windowed && windowSize != null) {

            System.out.println("WINDOW: Changing to windowed mode. Dimensions: " + windowSize.getWidth() + "x" + windowSize.getHeight());

            setUndecorated(false);
            setResizable(true);
            setMinimumSize(windowSize);
            setPreferredSize(windowSize);
            setLocationRelativeTo(null);
            setVisible(true);

        } else if (mode == Mode.Windowed) {
            System.out.println("WINDOW: Changing to windowed mode.");

            setUndecorated(false);
            setResizable(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setVisible(true);
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
