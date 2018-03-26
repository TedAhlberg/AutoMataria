package gameclient;

import common.*;

import java.awt.event.*;

/**
 * @author Johannes Blüml
 */
public class KeyInput extends KeyAdapter {
	private final ClientConnection client;
	private int lastKey = 0;

	public KeyInput(ClientConnection client) {
		this.client = client;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_ESCAPE) {
			System.exit(0);
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
