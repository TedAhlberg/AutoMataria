package gameclient.keyinput;

import common.Action;
import gameclient.interfaces.UserInterface;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Johannes Blüml
 * @author Ted Ahlberg
 */
public class KeyInput implements KeyEventDispatcher {
    private UserInterface userInterface;
    private KeyBindings bindings;

    public KeyInput(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED) return false;
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            userInterface.onKeyPress(Action.GoLeft);
        } else if (key == KeyEvent.VK_RIGHT) {
            userInterface.onKeyPress(Action.GoRight);
        } else if (key == KeyEvent.VK_UP) {
            userInterface.onKeyPress(Action.GoUp);
        } else if (key == KeyEvent.VK_DOWN) {
            userInterface.onKeyPress(Action.GoDown);
        } else if (key == KeyEvent.VK_SPACE) {
            userInterface.onKeyPress(Action.UsePickup);
        } else if (key == KeyEvent.VK_C) {
            userInterface.onKeyPress(Action.TogglePlayerColor);
        } else if (key == KeyEvent.VK_R) {
            userInterface.onKeyPress(Action.ToggleReady);
        } else if (key == KeyEvent.VK_F3) {
            userInterface.onKeyPress(Action.ToggleFPS);
        } else if (key == KeyEvent.VK_F2) {
            userInterface.onKeyPress(Action.ToggleInterpolation);
        } else if (key == KeyEvent.VK_F1) {
            userInterface.onKeyPress(Action.ToggleChat);
        } else if (key == KeyEvent.VK_Y) {
            userInterface.onKeyPress(Action.OpenChatPrompt);
        } else if (key == KeyEvent.VK_ENTER) {
            userInterface.onKeyPress(Action.SendChatMessage);
        } else if (key == KeyEvent.VK_ESCAPE) {
            userInterface.onKeyPress(Action.InterfaceBack);
        }

        return false;
    }
}
