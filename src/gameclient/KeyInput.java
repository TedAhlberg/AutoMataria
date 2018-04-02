package gameclient;

import common.Action;
import common.Direction;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Johannes Blüml
 */
public class KeyInput extends KeyAdapter {
    private Game game;
    private int lastKey = 0;

    public KeyInput(Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) {
            game.onKeyPress(Action.ExitGame);
        } else if (key == KeyEvent.VK_I) {
            game.onKeyPress(Action.ToggleInterpolation);
        }

        if (key == KeyEvent.VK_LEFT && lastKey != KeyEvent.VK_RIGHT) {
            game.onKeyPress(Direction.Left);
            lastKey = KeyEvent.VK_LEFT;
        } else if (key == KeyEvent.VK_RIGHT && lastKey != KeyEvent.VK_LEFT) {
            game.onKeyPress(Direction.Right);
            lastKey = KeyEvent.VK_RIGHT;
        } else if (key == KeyEvent.VK_UP && lastKey != KeyEvent.VK_DOWN) {
            game.onKeyPress(Direction.Up);
            lastKey = KeyEvent.VK_UP;
        } else if (key == KeyEvent.VK_DOWN && lastKey != KeyEvent.VK_UP) {
            game.onKeyPress(Direction.Down);
            lastKey = KeyEvent.VK_DOWN;
        }
    }
}
