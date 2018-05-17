package gameclient;

import gameclient.interfaces.UserInterface;

import java.net.InetSocketAddress;

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
    private UserInterface userInterface;
    public static final InetSocketAddress MAIN_SERVER = new InetSocketAddress("127.0.0.1", 2000);

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
