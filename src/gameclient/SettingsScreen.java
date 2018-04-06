package gameclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author eriklundow
 */

public class SettingsScreen extends JPanel {
	private BufferedImage backgroundImage;
	private JTextField tfName = new JTextField();
	private Font fontHead = new Font("Orbitron", Font.BOLD, 50);
	private Font fontText = new Font("Orbitron", Font.BOLD, 20);
//	private JPanel pnlSettings = new JPanel(new BorderLayout());
	

	public SettingsScreen() {
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(500, 500));
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(500, 500));

		try {
			backgroundImage = ImageIO.read(new File("resources/Stars.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
//		tfName.setPreferredSize(new Dimension(30,20));
//		tfName.setBounds(30, 30,10,10);
		tfName.setBounds(300, 85, 100, 20);
		frame.getContentPane().add(tfName);
		add(tfName);
		repaint();
		
		
		
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
		g.setFont(fontHead);
		g.setColor(Color.white);
		g.drawString("SETTINGS", 30, 50);
		g.setFont(fontText);
		g.drawString("Change Username" , 30, 100);
		
	}

	public static void main(String[] args) {
		new SettingsScreen();
	}
}
