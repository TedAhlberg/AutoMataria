package gameclient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
	private String highScoresFilenamePressed = "images/Highscore_Pressed.png";
	private String highScoresFilenameUnpressed = "images/Highscore_Unpressed.png";
	
	private Buttons btnPlay = new Buttons(playFilenamePressed, playFilenameUnpressed);
	private Buttons btnBrowse = new Buttons(browseFilenamePressed, browseFilenameUnpressed);
	private Buttons btnCreate = new Buttons(createFilenamePressed, createFilenameUnpressed);
	private Buttons btnSettings = new Buttons(settingsFilenamePressed, settingsFilenameUnpressed);
	private Buttons btnHighScore = new Buttons(highScoresFilenamePressed, highScoresFilenameUnpressed);
	
	private JPanel pnlGrid = new JPanel(new GridLayout(1,5,0,50));
	
	public TestStartScreen() {
		
		try {
			bfImage = ImageIO.read(new File("images/Auto-Mataria.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		btnPlay.setMinimumSize(new Dimension(120, 120));
		btnPlay.setPreferredSize(new Dimension(120, 120));
//		add(btnPlay);
		pnlGrid.add(btnPlay);
		
		btnBrowse.setMinimumSize(new Dimension(120,120));
		btnBrowse.setPreferredSize(new Dimension(120,120));
//		add(btnBrowse);
		pnlGrid.add(btnBrowse);
		
		btnCreate.setMinimumSize(new Dimension(120,120));
		btnCreate.setPreferredSize(new Dimension(120,120));
//		add(btnCreate);
		pnlGrid.add(btnCreate);
		
		btnSettings.setMinimumSize(new Dimension(120,120));
		btnSettings.setPreferredSize(new Dimension(120,120));
//		add(btnSettings);
		pnlGrid.add(btnSettings);
		
		btnHighScore.setMinimumSize(new Dimension(120,120));
		btnHighScore.setPreferredSize(new Dimension(120,120));
//		add(btnHighScore);
		pnlGrid.add(btnHighScore);
		pnlGrid.setOpaque(false);
		
		add(pnlGrid);
		
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bfImage, 0, 0, getWidth(), getHeight(), null);
		pnlGrid.setLocation(getWidth()/5, getHeight() - getHeight()/4);
		
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setMinimumSize(new Dimension(400,400));
		frame.setPreferredSize(new Dimension(930,800));
		TestStartScreen sc = new TestStartScreen();
		frame.add(sc);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
