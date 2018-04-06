package gameclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	private Font fontTextSmall = new Font("Orbitron", Font.BOLD, 14);
	private JLabel lblUserName = new JLabel("CHANGE USERNAME");
	private JLabel lblSettings = new JLabel("SETTINGS");
	private JLabel lblControls = new JLabel("CONTROLS");
	private JLabel lblUp = new JLabel("UP : ARROWKEY_UP");
	private JLabel lblLeft = new JLabel("LEFT : ARROWKEY_LEFT");
	private JLabel lblDown = new JLabel("LEFT : ARROWKEY_DOWN");
	private JLabel lblRight = new JLabel("RIGHT : ARROWKEY_RIGHT");

	private String fileChangePressed = "images/SetKeyBinding_Pressed.png";
	private String fileChangeUnpressed = "images/SetKeyBinding_Unpressed.png";

	private Buttons btnChange = new Buttons(fileChangePressed, fileChangeUnpressed);

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
		lblSettings.setFont(fontHead);
		lblSettings.setForeground(Color.white);
		lblSettings.setBounds(10, 10, 300, 50);

		lblUserName.setFont(fontText);
		lblUserName.setForeground(Color.WHITE);
		lblUserName.setBounds(30, 85, 300, 20);

		tfName.setBounds(300, 85, 100, 20);

		lblControls.setFont(fontText);
		lblControls.setForeground(Color.WHITE);
		lblControls.setBounds(30, 120, 300, 20);

		lblUp.setFont(fontTextSmall);
		lblDown.setFont(fontTextSmall);
		lblLeft.setFont(fontTextSmall);
		lblRight.setFont(fontTextSmall);

		lblUp.setForeground(Color.white);
		lblLeft.setForeground(Color.white);
		lblDown.setForeground(Color.white);
		lblRight.setForeground(Color.white);

		lblUp.setBounds(200, 150, 300, 20);
		lblLeft.setBounds(200, 170, 300, 20);
		lblDown.setBounds(200, 190, 300, 20);
		lblRight.setBounds(200, 210, 300, 20);

//		btnChange.setPreferredSize(new Dimension(50, 20));
//		btnChange.setBounds(100, 150, 500, 200);
		add(btnChange);

		add(lblSettings);
		add(lblUserName);
		add(tfName);
		add(lblControls);
		add(lblUp);
		add(lblLeft);
		add(lblDown);
		add(lblRight);

		repaint();

	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
		// g.setFont(fontHead);
		// g.setColor(Color.white);
		// g.drawString("SETTINGS", 30, 50);
		// g.setFont(fontText);
		// g.drawString("Change Username" , 30, 100);

	}

	public static void main(String[] args) {
		new SettingsScreen();
	}
}
