package gameclient;

import common.*;

import java.awt.event.*;

/**
 * @author Johannes Blüml
 */
public class KeyInput extends KeyAdapter {
    private final ClientConnection client;

    public KeyInput(ClientConnection client) {
        this.client = client;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
        if (key == KeyEvent.VK_LEFT) {
            client.send(Direction.Left);
        } else if (key == KeyEvent.VK_RIGHT) {
            client.send(Direction.Right);
        } else if (key == KeyEvent.VK_UP) {
            client.send(Direction.Up);
        } else if (key == KeyEvent.VK_DOWN) {
            client.send(Direction.Down);
        }
    }
}
