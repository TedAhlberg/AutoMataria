package gameclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author eriklundow
 */

public class SettingsScreen extends JPanel {
	private BufferedImage backgroundImage;

	private Dimension minimum = new Dimension(400, 400);
	private Dimension maximum = new Dimension(800, 800);
	private Font font = new Font("Orbitron", Font.BOLD, 100);
	public SettingsScreen() {
		
		setMinimumSize(minimum);
		setMaximumSize(maximum);

		try {
			backgroundImage = ImageIO.read(new File("resources/Stars.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString("SETTINGS", 100, 100);
		
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setMinimumSize(new Dimension(400, 400));
		frame.setPreferredSize(new Dimension(800, 800));
		frame.add(new SettingsScreen());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}
