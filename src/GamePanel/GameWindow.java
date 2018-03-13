package GamePanel;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

public class GameWindow extends JFrame {
	private GamePanel panel = new GamePanel();

	public GameWindow() {
		add(panel);
		setLocation(getCenterLocation());
		pack();
		setVisible(true);
	}

	private Point getCenterLocation() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen = toolkit.getScreenSize();
		int x = (screen.width / 2) - (panel.getWidth() / 2);
		int y = (screen.height / 2) - (panel.getHeight() / 2);
		return new Point(x, y);
	}

	public static void main(String[] args) {
		GameWindow frame = new GameWindow();
	}
}
