package gameclient.keyinput;

import common.Action;
import common.Direction;
import gameclient.Game;
import gameclient.interfaces.GameScreen;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Johannes Blüml
 * @author Ted Ahlberg
 */
public class KeyInput implements KeyEventDispatcher {
    private GameScreen gameScreen;
    private KeyBindings bindings;

    public KeyInput(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED) return false;
        int key = e.getKeyCode();

        // GoLeft
        if (key == KeyEvent.VK_LEFT) {
            gameScreen.onKeyPress(Direction.Left);
            // GoRight
        } else if (key == KeyEvent.VK_RIGHT) {
            gameScreen.onKeyPress(Direction.Right);
            // GoUp
        } else if (key == KeyEvent.VK_UP) {
            gameScreen.onKeyPress(Direction.Up);
            // GoDown
        } else if (key == KeyEvent.VK_DOWN) {
            gameScreen.onKeyPress(Direction.Down);
        }
        else if(key == KeyEvent.VK_SPACE) {
            gameScreen.onKeyPress(Action.UsePickup);
        }

        // ExitGame
        else if (key == KeyEvent.VK_ESCAPE) {
            gameScreen.onKeyPress(Action.ExitGame);
        }

        // ToggleInterpolation
        else if (key == KeyEvent.VK_I) {
            gameScreen.onKeyPress(Action.ToggleInterpolation);
        }

        // Toggle Player Color
        else if (key == KeyEvent.VK_C) {
            gameScreen.onKeyPress(Action.TogglePlayerColor);
        } else if (key == KeyEvent.VK_R) {
            gameScreen.onKeyPress(Action.ToggleReady);
        } else if (key == KeyEvent.VK_F1) {
            gameScreen.onKeyPress(Action.ToggleDebugText);
        }

        return false;
    }
}
