package gameclient;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
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
/*
        scrollPane = new JScrollPane();
        scrollPane.setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
  */

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        add(panel, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        BrowseServers bs = new BrowseServers();
        Window window = new Window("Test", new Dimension(800, 800));
        window.add(bs);
        window.pack();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backGround, 0, 0, getWidth(), getHeight(), null);
    }

    public void update(Collection<ServerInformation> serverList) {
        //panel.removeAll();
        for (ServerInformation info : serverList) {
            server = new ServersToBrowse(info.getServerName(),
                    info.getMapName(),
                    info.getGameState(),
                    info.getConnectedClients(),
                    info.getMaxPlayers(), new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    //userInterface.startGame(serverIP, serPort, "Player");
                }
            });
            GridBagConstraints c = new GridBagConstraints();
            c.gridx =0;
            c.weightx = 1;
            panel.add(server, c);
            System.out.println(server.getSize());
        }
        revalidate();
        repaint();
/*
        scrollPane.setViewportView(panel);
        scrollPane.revalidate();
        scrollPane.repaint();
        */
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

    public JScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public class Listener implements ActionListener {

        public void actionPerformed(ActionEvent event) {


        }

    }
}
