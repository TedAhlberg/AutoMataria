package gameclient.keyInput;

import common.Action;
import common.Direction;
import gameclient.Game;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Johannes Blüml
 * @author Ted Ahlberg
 */
public class KeyInput implements KeyEventDispatcher {
    private Game game;
    private KeyBindings bindings;
    private int lastKey = 0;

    public KeyInput(Game game) {
        this.game = game;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED) return false;
        int key = e.getKeyCode();

        // GoLeft
        if (key == KeyEvent.VK_LEFT && lastKey != KeyEvent.VK_RIGHT) {
            game.onKeyPress(Direction.Left);
            lastKey = KeyEvent.VK_LEFT;
            // GoRight
        } else if (key == KeyEvent.VK_RIGHT && lastKey != KeyEvent.VK_LEFT) {
            game.onKeyPress(Direction.Right);
            lastKey = KeyEvent.VK_RIGHT;
            // GoUp
        } else if (key == KeyEvent.VK_UP && lastKey != KeyEvent.VK_DOWN) {
            game.onKeyPress(Direction.Up);
            lastKey = KeyEvent.VK_UP;
            // GoDown
        } else if (key == KeyEvent.VK_DOWN && lastKey != KeyEvent.VK_UP) {
            game.onKeyPress(Direction.Down);
            lastKey = KeyEvent.VK_DOWN;
        }

        // ExitGame
        else if (key == KeyEvent.VK_ESCAPE) {
            game.onKeyPress(Action.ExitGame);
        }

        // ToggleInterpolation
        else if (key == KeyEvent.VK_I) {
            game.onKeyPress(Action.ToggleInterpolation);
        }

        // Toggle Player Color
        else if (key == KeyEvent.VK_C) {
            game.onKeyPress(Action.TogglePlayerColor);
        } else if (key == KeyEvent.VK_R) {
            game.onKeyPress(Action.ToggleReady);
        } else if (key == KeyEvent.VK_F1) {
            game.onKeyPress(Action.ToggleDebugText);
        }

        return false;
    }
}
