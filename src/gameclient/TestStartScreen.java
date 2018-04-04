package gameclient;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestStartScreen extends JPanel {
	private BufferedImage bfImage;
	private String playFilenamePressed = "images/Play_Pressed.png";
	private String playFilenameUnpressed = "images/Play_Unpressed.png";
	private String browseFilenamePressed = "images/Browse_Pressed.png";
	private String browseFilenameUnpressed = "images/Browse_Unpressed.png";
	private String createFilenamePressed = "images/Create_Pressed.png";
	private String createFilenameUnpressed = "images/Create_Unpressed.png";
	private String settingsFilenamePressed = "images/Settings_Pressed.png";
	private String settingsFilenameUnpressed = "images/Settings_Unpressed.png";
	
	private Buttons btnPlay = new Buttons(playFilenamePressed, playFilenameUnpressed);
	private Buttons btnBrowse = new Buttons(browseFilenamePressed, browseFilenameUnpressed);
	private Buttons btnCreate = new Buttons(createFilenamePressed, createFilenameUnpressed);
	private Buttons btnSettings = new Buttons(settingsFilenamePressed, settingsFilenameUnpressed);
	
	public TestStartScreen() {
		
		try {
			bfImage = ImageIO.read(new File("images/Auto-Mataria.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		btnPlay.setMinimumSize(new Dimension(120, 120));
		btnPlay.setPreferredSize(new Dimension(120, 120));
		add(btnPlay);
		
		btnBrowse.setMinimumSize(new Dimension(120,120));
		btnBrowse.setPreferredSize(new Dimension(120,120));
		add(btnBrowse);
		
		btnCreate.setMinimumSize(new Dimension(120,120));
		btnCreate.setPreferredSize(new Dimension(120,120));
		add(btnCreate);
		
		btnSettings.setMinimumSize(new Dimension(120,120));
		btnSettings.setPreferredSize(new Dimension(120,120));
		add(btnSettings);
		
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		btnPlay.setLocation(40, getHeight()-btnPlay.getHeight() - 30);
		g.drawImage(bfImage, 0, 0, getWidth(), getHeight(), null);
		
		btnBrowse.setLocation(220, getHeight()-btnBrowse.getHeight() - 30);
		btnCreate.setLocation(400, getHeight()-btnCreate.getHeight() - 30 );
		btnSettings.setLocation(580, getHeight()-btnCreate.getHeight() - 30 );
		
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setMinimumSize(new Dimension(400,400));
		frame.setPreferredSize(new Dimension(800,800));
		TestStartScreen sc = new TestStartScreen();
		frame.add(sc);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
