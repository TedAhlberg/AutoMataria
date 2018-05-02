package gameclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

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
	    //setPreferredSize(new Dimension(getWidth()/2, getHeight()/10));
	    setOpaque(false);
	    
	    lblServerName = new JLabel(serverName);
	    lblServerName.setBackground(Color.RED);
	    lblServerName.setForeground(Color.WHITE);
	    
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
	    gbc.ipadx = 10;
	    gbc.ipady = 10;
	    gbc.insets = new Insets(10, 10, 10, 10);
	    add(lblServerName, gbc);
	    
	    gbc = new GridBagConstraints();
	    gbc.gridx = 1;
	    gbc.weightx = 0.5;
	    gbc.ipadx = 10;
	    gbc.ipady = 10;
	    gbc.insets = new Insets(10, 10, 10, 10);
	    add(lblMapName, gbc);
	    
	    gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.weightx = 0.5;
        gbc.ipadx = 10;
        gbc.ipady = 10;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(lblGameState, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.weightx = 0.2;
        gbc.ipadx = 10;
        gbc.ipady = 10;
        gbc.insets = new Insets(10, 10, 10, 10);
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
}
