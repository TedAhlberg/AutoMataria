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
    private int lastKey = 0;

    public KeyInput(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED) return false;
        int key = e.getKeyCode();

        // GoLeft
        if (key == KeyEvent.VK_LEFT && lastKey != KeyEvent.VK_RIGHT) {
            gameScreen.onKeyPress(Direction.Left);
            lastKey = KeyEvent.VK_LEFT;
            // GoRight
        } else if (key == KeyEvent.VK_RIGHT && lastKey != KeyEvent.VK_LEFT) {
            gameScreen.onKeyPress(Direction.Right);
            lastKey = KeyEvent.VK_RIGHT;
            // GoUp
        } else if (key == KeyEvent.VK_UP && lastKey != KeyEvent.VK_DOWN) {
            gameScreen.onKeyPress(Direction.Up);
            lastKey = KeyEvent.VK_UP;
            // GoDown
        } else if (key == KeyEvent.VK_DOWN && lastKey != KeyEvent.VK_UP) {
            gameScreen.onKeyPress(Direction.Down);
            lastKey = KeyEvent.VK_DOWN;
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
