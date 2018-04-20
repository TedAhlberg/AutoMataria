package gameclient;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JScrollBar;
import javax.swing.JList;

public class BrowseServers extends JPanel {
    private Font font = new Font("Orbitron", Font.PLAIN, 30);
    private JScrollPane scrollPane = new JScrollPane();
//    private JList<ServersToBrowse> list = new JList<>();
    
    ServersToBrowse stb = new ServersToBrowse("Best Server", "Best Map", "Warm Up", 5, 15);
    ServersToBrowse stb2 = new ServersToBrowse("Best Server", "Best Map", "Warm Up", 5, 15);
    ServersToBrowse stb3 = new ServersToBrowse("Best Server", "Best Map", "Warm Up", 5, 15);
    ServersToBrowse stb4 = new ServersToBrowse("Best Server", "Best Map", "Warm Up", 5, 15);
    
	
	public BrowseServers() {
	    setLayout(new BorderLayout(0, 0));
	    
	    JLabel lblServers = new JLabel("Servers");
	    lblServers.setFont(font);
	    lblServers.setHorizontalAlignment(SwingConstants.CENTER);
	    add(lblServers, BorderLayout.NORTH);
	    
	   
//	    list.add(stb);
//	    list.add(stb2);
//	    list.add(stb3);
//	    list.add(stb4);
        
        scrollPane.add(stb);
        scrollPane.add(stb);
        scrollPane.add(stb);
        scrollPane.add(stb);
        
        add(scrollPane, BorderLayout.CENTER);
	}
	
	public static void main(String[] args) {
	    BrowseServers bs = new BrowseServers();
	    Window window = new Window("Test", new Dimension(800,800));
	    window.add(bs);
	    window.pack();
	}
	

}
