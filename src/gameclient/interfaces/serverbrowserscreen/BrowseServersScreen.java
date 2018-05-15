package gameclient.interfaces.serverbrowserscreen;

import common.ServerInformation;
import gameclient.Resources;
import gameclient.interfaces.*;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Collection;

public class BrowseServersScreen extends JPanel implements ServerInformationListener, UserInterfaceScreen {
    private ServerInformationReceiver serverInformationReceiver;
    private JPanel panel;
    private UserInterface userInterface;

    public BrowseServersScreen(UserInterface userInterface) {
        this.userInterface = userInterface;

        createLayout();
    }

    private void createLayout() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        GridBagConstraints c;

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("FIND A GAME");
        headerLabel.setFont(Resources.getInstance().getTitleFont());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 10;
        c.insets = new Insets(0, 50, 0, 0);
        c.anchor = GridBagConstraints.WEST;
        topPanel.add(headerLabel, c);

        AMButton hostServerButton = new AMButton("HOST SERVER");
        hostServerButton.addActionListener(e -> userInterface.changeScreen("HostServerScreen"));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.ipadx = 10;
        c.ipady = 10;
        c.insets = new Insets(0, 0, 0, 10);
        c.anchor = GridBagConstraints.EAST;
        topPanel.add(hostServerButton, c);

        AMButton customConnectButton = new AMButton("CUSTOM CONNECT");
        customConnectButton.addActionListener(e -> userInterface.changeScreen("ConnectScreen"));
        c = new GridBagConstraints();
        c.gridx = 2;
        c.ipadx = 10;
        c.ipady = 10;
        c.insets = new Insets(0, 0, 0, 10);
        c.anchor = GridBagConstraints.EAST;
        topPanel.add(customConnectButton, c);

        AMButton backButton = new AMButton("BACK");
        backButton.addActionListener(e -> userInterface.changeToPreviousScreen());
        c = new GridBagConstraints();
        c.gridx = 3;
        c.ipadx = 10;
        c.ipady = 10;
        c.insets = new Insets(0, 0, 0, 50);
        c.anchor = GridBagConstraints.EAST;
        topPanel.add(backButton, c);


        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(topPanel, c);

        panel = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 50;
        c.weightx = 1;
        add(panel, c);
    }

    /**
     * Updates the serverlist with the latest serverinformation
     *
     * @param serverList A list of servers with all important information about it
     */
    public void update(Collection<ServerInformation> serverList) {
        panel.removeAll();

        int column = 0, row = 0;
        GridBagConstraints c;

        JLabel[] headerLabels = {
                new JLabel("SERVER NAME"),
                new JLabel("MAP"),
                new JLabel("STATE"),
                new JLabel("PLAYERS"),
                new JLabel("IP : PORT"),
                new JLabel()
        };

        for (JLabel label : headerLabels) {
            label.setForeground(Color.MAGENTA.darker());
            panel.add(label, getTableConstraints(column, row));

            column += 1;
        }
        row += 1;

        JPanel separator = new JPanel();
        separator.setBorder(new MatteBorder(1, 0, 0, 0, Color.MAGENTA.darker().darker()));
        c = new GridBagConstraints();
        c.gridwidth = headerLabels.length;
        c.gridy = row;
        c.fill = GridBagConstraints.BOTH;
        panel.add(separator, c);

        row += 1;

        for (ServerInformation info : serverList) {
            column = 0;

            JLabel[] labels = {
                    new JLabel(info.getServerName()),
                    new JLabel(info.getMapName()),
                    new JLabel(info.getGameState()),
                    new JLabel(info.getConnectedClients() + "/" + info.getMaxPlayers()),
                    new JLabel(info.getIp() + ":" + info.getServerPort())
            };

            for (JLabel label : labels) {
                panel.add(label, getTableConstraints(column, row));
                column += 1;
            }

            AMButton joinButton = new AMButton("JOIN");
            joinButton.addActionListener(actionEvent -> userInterface.startGame(info.getIp(), info.getServerPort()));
            panel.add(joinButton, getTableConstraints(column, row));

            row += 1;
        }
        revalidate();
        repaint();
    }

    private GridBagConstraints getTableConstraints(int gridx, int gridy) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        c.insets = new Insets(10, 10, 10, 10);
        return c;
    }

    public void onScreenActive() {
        serverInformationReceiver = new ServerInformationReceiver();
        serverInformationReceiver.addListener(this);
        serverInformationReceiver.start();
        System.out.println("Started Server Information Receiver");
    }

    public void onScreenInactive() {
        if (serverInformationReceiver == null) return;
        serverInformationReceiver.close();
        serverInformationReceiver = null;
        panel.removeAll();
        System.out.println("Stopped Server Information Receiver");
    }
}
