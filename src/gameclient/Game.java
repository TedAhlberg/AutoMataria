package gameclient;

import gameclient.interfaces.UserInterface;

import javax.swing.*;

/**
 * Starts the Auto-Mataria Game
 *
 * @author Johannes Bluml
 */
public class Game {
    public static final String TITLE = "Auto-Mataria";
    public static final int GRID_PIXEL_SIZE = 100;
    public static final int LOCAL_UDP_PORT = 63211;
    public static final String MAIN_SERVER_IP = "johannes.bluml.se";
    public static final int MAIN_SERVER_PORT = 80;
    private UserInterface userInterface;

    public Game() {
        System.setProperty("sun.java2d.opengl", "True");
        System.setProperty("awt.useSystemAAFontSettings", "on");

        SwingUtilities.invokeLater(() -> {
            userInterface = new UserInterface();
        });
    }

    public static void main(String[] args) {
        new Game();
    }
}
