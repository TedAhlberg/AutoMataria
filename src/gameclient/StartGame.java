package gameclient;

import gameclient.interfaces.UserInterface;

import javax.swing.*;

/**
 * Starts the AutoMataria GameClient
 *
 * @author Johannes Blüml
 */
public class StartGame {
    public static void main(String[] args) {

        if (args.length == 1) {
            // Scales the game interface to the provided size
            // 2.0 is a good size for HiDPI displays
            System.setProperty("sun.java2d.uiScale", args[0]);
        }

        System.setProperty("sun.java2d.opengl", "True");
        System.setProperty("awt.useSystemAAFontSettings", "on");

        SwingUtilities.invokeLater(() -> {
            new UserInterface();
        });
    }
}
