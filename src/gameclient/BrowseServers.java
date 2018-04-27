package gameclient;

import javax.swing.JPanel;
import java.util.Collection;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class BrowseServers extends JPanel implements ServerInformationListener {
    private ServerInformationReceiver sir = new ServerInformationReceiver();
    private Font font = new Font("Orbitron", Font.PLAIN, 30);
    private ServersToBrowse server;
    private BufferedImage backGround;
    private JScrollPane scrollPane;
    private JPanel panel;
    
    
	
	public BrowseServers() {
	    setLayout(new BorderLayout());
	    backGround = Resources.getImage("Stars.png");
	    
	    sir.start();
	    sir.addListener(this);
	    
	    JLabel lblServers = new JLabel("Servers");
	    lblServers.setFont(font);
	    lblServers.setHorizontalAlignment(SwingConstants.CENTER);
	    add(lblServers, BorderLayout.NORTH);
	    
	    JScrollPane scrollPane = new JScrollPane();
	    scrollPane.setOpaque(false);
	    add(scrollPane, BorderLayout.CENTER);
	    
	    JPanel panel = new JPanel();
	    panel.setOpaque(false);
	    scrollPane.setViewportView(panel);
	    
	    
	    add(panel);

        
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	    g.drawImage(backGround, 0, 0, getWidth(), getHeight(), null);
	}

    public void update(Collection<ServerInformation> serverList) {
    	scrollPane.removeAll();
        
        for(ServerInformation info : serverList) {
            server = new ServersToBrowse(info.getServerName(),
                                                          info.getMapName(),
                                                          info.getGameState().toString(),
                                                          info.getConnectedClients(),
                                                          info.getMaxPlayers());
           
           panel.add(server);
            
        }
        scrollPane.revalidate();
        scrollPane.repaint();
    }
    
    public void addButton(ActionListener actionListener, JPanel panel) {
    	Buttons button = new Buttons("Join");
    	button.addActionListener(actionListener);
    	button.setFont(font);
    	
    	GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 30;
        c.ipady = 30;
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        
        panel.add(button);
    	
    }
    
    public class Listener implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			
			
		}
    	
    }
    
    public JScrollPane getScrollPane() {
    	return this.scrollPane;
    }
    
    
	public static void main(String[] args) {
	    BrowseServers bs = new BrowseServers();
	    Window window = new Window("Test", new Dimension(800,800));
	    window.add(bs);
	    window.pack();
	}
}
