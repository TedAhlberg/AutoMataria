package gameclient;

import gameclient.interfaces.UserInterface;

import java.awt.*;

/**
 * @author Johannes Bluml
 */
public class Game {
    public static final String TITLE = "Auto-Mataria";
    public static final int GRID_PIXEL_SIZE = 100;
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
