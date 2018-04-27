package gameclient;

import gameclient.interfaces.UserInterface;

/**
 * Starts the Auto-Mataria Game
 * Contains some important settings.
 *
 * @author Johannes Bluml
 */
public class Game {
    public static final String TITLE = "Auto-Mataria";
    public static final int GRID_PIXEL_SIZE = 100;
    public static final int LOCAL_UDP_PORT = 63211;
    private UserInterface userInterface;

    public Game() {
        System.setProperty("sun.java2d.opengl", "True");
        System.setProperty("awt.useSystemAAFontSettings", "on");

        userInterface = new UserInterface();
    }

    public static void main(String[] args) {
        new Game();
    }
}
