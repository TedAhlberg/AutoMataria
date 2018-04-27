package gameclient;

import common.Action;
import common.*;
import common.messages.ConnectionMessage;
import common.messages.GameEventMessage;
import gameclient.Window;
import gameclient.interfaces.UserInterface;
import gameobjects.GameObject;
import gameobjects.Player;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

/**
 * @author Johannes Bluml
 */
public class Game {
    public static final String TITLE = "Auto-Mataria";
    public static final int GRID_PIXEL_SIZE = 100;

    public Game() {
        this("127.0.0.1", 32000, null, 100);
    }

    public Game(String serverIP, int serverPort, int frameRate) {
        this(serverIP, serverPort, null, frameRate);
    }

    public Game(String serverIP, int serverPort, Dimension windowSize, int framesPerSecond) {
        System.setProperty("sun.java2d.opengl", "True");

        Window window = new Window("Auto-Mataria", new Dimension(1000, 700));
        UserInterface userInterface = new UserInterface(window.getSize());
        window.setContentPane(userInterface);
        window.pack();
    }

    public static void main(String[] args) throws InterruptedException {
        new Game();
    }
}
