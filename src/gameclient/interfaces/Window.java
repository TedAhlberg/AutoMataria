package gameclient.interfaces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import gameclient.Resources;

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
        setIconImage(Resources.getImage("AM-Icon.png"));
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
            if (key.toString().startsWith("List")) {
                System.out.println(key + " " + value);
            }
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
        ColorUIResource accentColor = new ColorUIResource(new Color(0x8e44ad));
        ColorUIResource darkColor = new ColorUIResource(0, 0, 0);
        ColorUIResource brightColor = new ColorUIResource(255, 255, 255);

        UIManager.put("ScrollPane.background", darkColor);
        UIManager.put("TextPane.background", darkColor);
        UIManager.put("ScrollPane.border", "");

        UIManager.put("Panel.background", darkColor);
        UIManager.put("Label.foreground", brightColor);

        UIManager.put("ComboBox.background", brightColor);
        UIManager.put("ComboBox.foreground", darkColor);

        UIManager.put("TabbedPane.foreground", brightColor);
        UIManager.put("TabbedPane.background", darkColor);
        UIManager.put("TabbedPane.shadow", darkColor);
        UIManager.put("TabbedPane.highlight", accentColor);
        UIManager.put("TabbedPane.darkShadow", darkColor);
        UIManager.put("TabbedPane.contentAreaColor", darkColor);
        UIManager.put("TabbedPane.tabAreaColor", darkColor);
        UIManager.put("TabbedPane.tabAreaBackground", darkColor);
        UIManager.put("TabbedPane.unselectedBackground", new ColorUIResource(50, 50, 50));
        UIManager.put("TabbedPane.borderHightlightColor", darkColor);
        UIManager.put("TabbedPane.light", darkColor);
        UIManager.put("TabbedPane.focus", darkColor);
        UIManager.put("TabbedPane.selectHighlight", darkColor);
        UIManager.put("TabbedPane.selected", darkColor);
        UIManager.put("TabbedPane.selectedLabelShift", 0);
        UIManager.put("TabbedPane.labelShift", 0);
        UIManager.put("TabbedPane.textIconGap", 0);
        UIManager.put("TabbedPane.tabRunOverlay", 0);

        UIManager.put("TabbedPane.contentBorderInsets", new Insets(10, 10, 10, 10));
        UIManager.put("TabbedPane.tabInsets", new Insets(10, 10, 10, 10));

        UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(0, 0, 0, 0));

        UIManager.put("ComboBox.selectionBackground", darkColor);
        UIManager.put("ComboBox.selectionForeground", brightColor);

        LinkedList gradient = new LinkedList();
        gradient.add(0.0);
        gradient.add(0.0);
        gradient.add(new Color(0x9b59b6));
        gradient.add(new Color(0x9b59b6));
        gradient.add(new Color(0x8e44ad));
        UIManager.put("Slider.gradient", gradient);
        UIManager.put("Slider.focusGradient", gradient);
        UIManager.put("Slider.background", darkColor);
        UIManager.put("Slider.foreground", accentColor);
        UIManager.put("Slider.highlight", accentColor);
        UIManager.put("Slider.shadow", accentColor);
        UIManager.put("Slider.focus", accentColor);
        UIManager.put("Slider.altTrackColor", accentColor);
        UIManager.put("Slider.tickColor", accentColor);

        UIManager.put("CheckBox.background", darkColor);
        UIManager.put("CheckBox.foreground", brightColor);
        UIManager.put("CheckBox.focus", darkColor);

        UIManager.put("RadioButton.background", darkColor);
        UIManager.put("RadioButton.foreground", brightColor);
        UIManager.put("RadioButton.focus", darkColor);

        UIManager.put("OptionPane.messageForeground", brightColor);
        UIManager.put("OptionPane.informationIcon", new ImageIcon());
        UIManager.put("OptionPane.messageAreaBorder", new MatteBorder(20, 20, 20, 20, darkColor));
        UIManager.put("OptionPane.border", new MatteBorder(5, 5, 5, 5, accentColor));

        darkColor = new ColorUIResource(40, 40, 40);
        UIManager.put("Button.background", darkColor);
        UIManager.put("Button.foreground", brightColor);
        UIManager.put("Button.gradient", gradient);
        UIManager.put("Button.focusGradient", gradient);
        UIManager.put("Button.highlight", darkColor);
        UIManager.put("Button.shadow", darkColor);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.select", darkColor);
        UIManager.put("Button.margin", new Insets(10, 10, 10, 10));
        UIManager.put("Button.border", new CompoundBorder(new MatteBorder(1, 1, 1, 1, darkColor), new EmptyBorder(8, 15, 6, 15)));

        UIManager.put("TextField.border", new CompoundBorder(new MatteBorder(1, 1, 1, 1, darkColor), new EmptyBorder(7, 7, 5, 7)));
        UIManager.put("TextField.margin", new Insets(0, 0, 0, 0));
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
