package gameclient;

import common.AudioPlayer;
import common.GameObject;
import gameserver.GameServer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

/**
 * @author Johannes Bluml
 */
public class Game extends Canvas {
	public static final String TITLE = "Auto-Mataria";
	private final double scale;

	private ClientConnection client;
	private String serverIP = "localhost";
	private int serverPort = 32000;
	private BufferedImage background;
	private AudioPlayer backgroundMusic = AudioPlayer.getSound("resources/Music/AM-track1.wav");

	public Game() {

		String playerName = JOptionPane.showInputDialog("Enter your username:", "Username");
		backgroundMusic.play();
		new Window(TITLE, this);

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			env.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Orbitron Bold.ttf")));
			background = ImageIO.read(new File("resources/Stars.png"));
		} catch (IOException | FontFormatException e) {
			System.out.println("Failed to load resources.");
			e.printStackTrace();
		}

		Rectangle bounds = env.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
		scale = Math.min(bounds.getWidth() / GameServer.WIDTH, bounds.getHeight() / GameServer.HEIGHT);

		Graphics2D g = (Graphics2D) background.getGraphics();
		g.scale(scale, scale);
		g.setPaint(new Color(1, 1, 1, 0.2f));
		for (int i = 0; i <= GameServer.WIDTH; i += GameServer.GRIDSIZE) {
			g.drawLine(i, 0, i, GameServer.HEIGHT);
		}
		for (int i = 0; i <= GameServer.HEIGHT; i += GameServer.GRIDSIZE) {
			g.drawLine(0, i, GameServer.WIDTH, i);
		}
		g.dispose();

		try {
			Socket socket = new Socket(serverIP, serverPort);
			client = new ClientConnection(socket, gameObjects -> render((Collection<GameObject>) gameObjects));
			client.send(playerName);
			this.addKeyListener(new KeyInput(client));
			this.requestFocus();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unable to connect server.");
			System.exit(1);
		}
	}

	public static int clamp(int var, int min, int max) {
		if (var >= max)
			return max;
		if (var <= min)
			return min;
		return var;
	}

	public static void main(String[] args) {
		new Game();
	}

	private void render(Collection<GameObject> gameObjects) {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			return;
		}
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();

		g.drawImage(background, 0, 0, this);
		g.scale(scale, scale);
		gameObjects.forEach(gameObject -> gameObject.render(g));

		g.dispose();
		bs.show();
		Toolkit.getDefaultToolkit().sync();
	}
}
