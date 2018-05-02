package gameclient;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import gameclient.interfaces.UserInterface;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Collection;

public class BrowseServers extends JPanel implements ServerInformationListener {
    private ServerInformationReceiver sir = new ServerInformationReceiver();
    private Font font = new Font("Orbitron", Font.PLAIN, 30);
    private ServersToBrowse server;
    private BufferedImage backGround;
    private JPanel panel;
    private JPanel pnlSouthCenter;
    private UserInterface userInterface;


    public BrowseServers(UserInterface userInterface) {
        this.userInterface = userInterface;
        setLayout(new BorderLayout());
        backGround = Resources.getImage("Stars.png");

        sir.start();
        sir.addListener(this);

        JLabel lblServers = new JLabel("Servers");
        lblServers.setFont(font);
        lblServers.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblServers, BorderLayout.NORTH);

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        add(panel, BorderLayout.CENTER);
        
        pnlSouthCenter = new JPanel();
        
        Buttons btnExit = new Buttons("Exit");
        btnExit.setPreferredSize(new Dimension(60,40));
        btnExit.setHorizontalAlignment(SwingConstants.CENTER);
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userInterface.changeScreen("StartScreen");
            }
            
        });
        
        Buttons btnCustom = new Buttons("Custom Connect");
        btnCustom.setPreferredSize(new Dimension(130,40));
        btnCustom.setHorizontalAlignment(SwingConstants.CENTER);
        btnCustom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userInterface.changeScreen("ConnectScreen");
            }
            
        });
        
        pnlSouthCenter.add(btnExit);
        pnlSouthCenter.add(btnCustom);
        pnlSouthCenter.setOpaque(false);
        
        
        
        add(pnlSouthCenter, BorderLayout.SOUTH);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backGround, 0, 0, getWidth(), getHeight(), null);
    }

    public void update(Collection<ServerInformation> serverList) {
//        panel.removeAll();
        //gridPanel = new JPanel(new GridBagLayout());
        int gridWeightX = 1;
        int gridX = 0;
        int gridY = 0;
        
        for (ServerInformation info : serverList) {
            server = new ServersToBrowse(info.getServerName(),
                    info.getMapName(),
                    info.getGameState(),
                    info.getConnectedClients(),
                    info.getMaxPlayers());
            
            Buttons button = new Buttons("Join");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    userInterface.startGame(info.getIp(), info.getServerPort());
                    System.out.println(info.getIp());
                }
            });
            
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = gridY;
            c.gridx = 0;
            panel.add(server, c);

            GridBagConstraints c2 = new GridBagConstraints();
            c2.gridx = 1;
            c2.gridy = gridY;
            c2.ipadx = 10;
            c2.ipady = 10;
            panel.add(button, c2);
            
            gridY+=1;
        }
        //panel.add(gridPanel);
        revalidate();
        repaint();
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserInterface userInterface = new UserInterface();
            userInterface.changeScreen("BrowseScreen");
        });
    }

}
