package GamePanel;

import java.awt.Dimension;

import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class GameWindow extends JFrame {
	private GamePanel gamePanel = new GamePanel();

	public GameWindow() {
		add(gamePanel);
		setLocation(getCenterLocation());
		pack();
		setVisible(true);
	}

	private Point getCenterLocation() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen = toolkit.getScreenSize();
		int x = (screen.width / 2) - (gamePanel.getWidth() / 2);
		int y = (screen.height / 2) - (gamePanel.getHeight() / 2);
		return new Point(x, y);
	}

	public static void main(String[] args) {
		GameWindow frame = new GameWindow();
	}
}
