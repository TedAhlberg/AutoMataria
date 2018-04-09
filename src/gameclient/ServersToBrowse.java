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
	private JPanel pnlHead2 = new JPanel (new GridLayout(1,2, 3, 20));
	private JLabel lblTestName;
	private JLabel lblTestPlayers;
	
	public ServersToBrowse() {
//		players = playersCurrently + "/" + playersTotal;
//		serverName = gameServer.getServerName();
//		serverName += players;
//		players.format(format, args)
//		
//		lblServerName = new JLabel(serverName + players);
//		System.out.println(players);
		
//		serverName = gameServer.getServerName();
//		playersCurrently = gameServer.getPlayers();
//		playersTotal = gameServer.getMaxPlayers();
		
		serverName = "AutoMataria-server";
		playersCurrently = 7;
		playersTotal = 14;
		
		String testName = "Am";
		int cur = 1;
		int tot = 20;
		
		lblServerName = new JLabel(serverName);
		lblServerName.setHorizontalAlignment(SwingConstants.LEFT);
		lblPlayers = new JLabel(playersCurrently + "/" + playersTotal);
		lblPlayers.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lblTestName = new JLabel(testName);
		lblTestName.setHorizontalAlignment(SwingConstants.LEFT);
		lblTestPlayers = new JLabel(cur + "/" + tot);
		lblTestPlayers.setHorizontalAlignment(SwingConstants.RIGHT);
		
//		grid.addLayoutComponent(serverName,lblServerName);
//		grid.addLayoutComponent(playersCurrently + "/" + playersTotal, lblPlayers);
		
		pnlHead.add(lblServerName);
		pnlHead.add(lblPlayers);
		
		pnlHead2.add(lblTestName);
		pnlHead2.add(lblTestPlayers);
//		add(lblServerName, grid);
//		add(lblPlayers, grid);
		add(pnlHead);
		add(pnlHead2);
		
	}
	
	public static void main(String[] args) {
		ServersToBrowse stb = new ServersToBrowse();
		JOptionPane.showMessageDialog(null, stb);
	}
	
	
}
