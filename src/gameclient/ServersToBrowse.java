package gameclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Henrik Olofsson
 *
 */

public class ServersToBrowse extends JPanel {
	private JLabel lblServerName;
	private JLabel lblMapName;
	private JLabel lblGameState;
	private JLabel lblPlayersCurrently;
	private JLabel lblPlayersMax;
	private GridBagConstraints gbc = new GridBagConstraints();
	
	private Font font = new Font("Orbitron", Font.PLAIN, 12);
	
	
	public ServersToBrowse(String serverName, String mapName, String gameState,
	                          int playersCurrently, int playersMax) {
	    
	    setLayout(new GridBagLayout());
	    setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    
	    lblServerName = new JLabel(serverName);
	    lblMapName = new JLabel(mapName);
	    lblGameState = new JLabel(gameState);
	    lblPlayersCurrently = new JLabel(Integer.toString(playersCurrently) + " / " + Integer.toString(playersMax));
	    lblPlayersMax = new JLabel(Integer.toString(playersMax));
	    
	    lblServerName.setFont(font);
	    lblMapName.setFont(font);
	    lblGameState.setFont(font);
	    lblPlayersCurrently.setFont(font);
	    lblPlayersMax.setFont(font);
	    
	    gbc.gridx = 0;
	    gbc.weightx = 0.1;
	    add(lblServerName, gbc);
	    
	    gbc = new GridBagConstraints();
	    gbc.gridx = 1;
	    gbc.weightx = 0.5;
	    add(lblMapName, gbc);
	    
	    gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.weightx = 0.5;
        add(lblGameState, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.weightx = 0.2;
        add(lblPlayersCurrently, gbc);
        
	}
	
	public void setText(String serverName, String mapName, String gameState,
                              int playersCurrently, int playersMax) {
	    
	    lblServerName = new JLabel(serverName);
        lblMapName = new JLabel(mapName);
        lblGameState = new JLabel(gameState);
        lblPlayersCurrently = new JLabel(Integer.toString(playersCurrently));
        lblPlayersMax = new JLabel(Integer.toString(playersMax));
	}
	
	public static void main(String[] args) {
		ServersToBrowse stb = new ServersToBrowse("Best Server", "Best Map", "Warm Up", 5, 15);
		Window window = new Window("Test", new Dimension(400 , 30));
        window.add(stb);
		window.pack();
	}
	
	
}
