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
    private UserInterface userInterface;

    public Game() {
        System.setProperty("sun.java2d.opengl", "True");
        userInterface = new UserInterface();
    }

    public static void main(String[] args) {
        new Game();
    }
}
