package gameclient;

import javax.swing.JPanel;
import java.util.Collection;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import gameserver.ServerInformation;

public class BrowseServers extends JPanel implements ServerInformationListener {
    private Font font = new Font("Orbitron", Font.PLAIN, 30);
    private JScrollPane scrollPane = new JScrollPane();
    private ServerInformationReceiver sir = new ServerInformationReceiver();
    private BufferedImage backGround;
    private JPanel pnlCenter = new JPanel();
    
	
	public BrowseServers() {
	    setLayout(new BorderLayout());
	    backGround = Resources.getImage("Stars.png");
	    
	    sir.start();
	    sir.addListener(this);
	    
	    JLabel lblServers = new JLabel("Servers");
	    lblServers.setFont(font);
	    lblServers.setHorizontalAlignment(SwingConstants.CENTER);
	    add(lblServers, BorderLayout.NORTH);
	    
	    scrollPane.setOpaque(false);
        scrollPane.setViewportView(pnlCenter);
        add(scrollPane, BorderLayout.CENTER);
	}
	
	public static void main(String[] args) {
	    BrowseServers bs = new BrowseServers();
	    Window window = new Window("Test", new Dimension(800,800));
	    window.add(bs);
	    window.pack();
	}
	
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    g.drawImage(backGround, 0, 0, getWidth(), getHeight(), null);
	}

    public void update(Collection<ServerInformation> serverList) {
        scrollPane.removeAll();
        pnlCenter.setLayout(new GridLayout(serverList.size(),2));
        pnlCenter.setOpaque(false);
        
        for(ServerInformation info : serverList) {
            ServersToBrowse servers = new ServersToBrowse(info.getServerName(),
                                                          info.getMapName(),
                                                          info.getGameState().toString(),
                                                          info.getConnectedClients(),
                                                          info.getMaxPlayers());
            JButton btnJoin = new JButton("JOIN");
            pnlCenter.add(servers);
            pnlCenter.add(btnJoin);    
            
            add(pnlCenter);
        }
        scrollPane.setViewportView(pnlCenter);
        scrollPane.revalidate();
        scrollPane.repaint();
    }
}
