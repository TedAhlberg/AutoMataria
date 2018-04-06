package gameclient.keyinput;

import common.Action;
import common.Direction;
import gameclient.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Johannes Blüml
 * @author Ted Ahlberg
 */
public class KeyInput extends KeyAdapter {
    private Game game;
    private KeyBindings bindings;
    private int lastKey = 0;

    public KeyInput(Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // ExitGame
        if (key == KeyEvent.VK_ESCAPE) {
            game.onKeyPress(Action.ExitGame);
        }

        // ToggleInterpolation
        if (key == KeyEvent.VK_I) {
            game.onKeyPress(Action.ToggleInterpolation);
        }

        // Toggle Player Color
        if (key == KeyEvent.VK_C) {
            game.onKeyPress(Action.TogglePlayerColor);
        }

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
    }
}
