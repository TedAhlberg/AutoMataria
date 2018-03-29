package gameclient;

import common.Direction;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Johannes Blüml
 */
public class KeyInput extends KeyAdapter {
    private final GameServerConnection client;
    private int lastKey = 0;

    public KeyInput(GameServerConnection client) {
        this.client = client;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) {
            if (client != null) {
                client.disconnect();
            } else {
                System.exit(0);
            }
        }

        if (key == KeyEvent.VK_LEFT && lastKey != KeyEvent.VK_RIGHT) {
            client.send(Direction.Left);
            lastKey = KeyEvent.VK_LEFT;
        } else if (key == KeyEvent.VK_RIGHT && lastKey != KeyEvent.VK_LEFT) {
            client.send(Direction.Right);
            lastKey = KeyEvent.VK_RIGHT;
        } else if (key == KeyEvent.VK_UP && lastKey != KeyEvent.VK_DOWN) {
            client.send(Direction.Up);
            lastKey = KeyEvent.VK_UP;
        } else if (key == KeyEvent.VK_DOWN && lastKey != KeyEvent.VK_UP) {
            client.send(Direction.Down);
            lastKey = KeyEvent.VK_DOWN;
        }
    }
}
