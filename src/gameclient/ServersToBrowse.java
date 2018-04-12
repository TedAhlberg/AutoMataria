package gameclient;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gameserver.GameServer;

public class ServersToBrowse extends JPanel {
	private GameServer gameServer;
	private String serverName;
	private String players;
	private int playersCurrently;
	private int playersTotal;
	private JLabel lblServerName;
	private JLabel lblPlayers;
	
	
	private JPanel pnlHead = new JPanel (new GridLayout(1,2, 3, 20));
	private JLabel lblTestName;
	private JLabel lblTestPlayers;
	
	public ServersToBrowse() {
		players = playersCurrently + "/" + playersTotal;
//		serverName = gameServer.getServerName();
		serverName += players;
		
		lblServerName = new JLabel(serverName + players);
		
//		serverName = gameServer.getServerName();
//		playersCurrently = gameServer.getPlayers();
//		playersTotal = gameServer.getMaxPlayers();
		
		
		lblServerName = new JLabel(serverName);
		lblServerName.setHorizontalAlignment(SwingConstants.LEFT);
		lblPlayers = new JLabel(playersCurrently + "/" + playersTotal);
		lblPlayers.setHorizontalAlignment(SwingConstants.RIGHT);
		
		
	}
	
	public static void main(String[] args) {
		ServersToBrowse stb = new ServersToBrowse();
		JOptionPane.showMessageDialog(null, stb);
	}
	
	
}
