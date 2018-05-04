package gameclient.interfaces;

import common.Maps;
import gameclient.*;
import gameclient.Window;
import gameserver.GameServer;

import javax.swing.*;
import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class HostServerScreen extends JPanel {
    private Buttons startButton, stopButton, backButton, joinGameButton;
    private JComboBox<String> profileComboBox;
    private JSlider playerSpeedSlider, tickRateSlider, ticksPerUpdateSlider;
    private JComboBox<String> mapsComboBox;
    private JTextField portTextField, serverNameTextField;
    private JLabel serverStatusLabel;
    private GameServer server;
    private UserInterface userInterface;

    public HostServerScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        setOpaque(false);
        createLayout();
        addListeners();
        profileComboBox.setSelectedIndex(2);
    }

    public static void main(String[] args) {
        Window window = new Window("HostServerScreen", new Dimension(1440, 800));
        window.setContentPane(new HostServerScreen(null));
        window.pack();
    }

    private void createLayout() {
        GridBagConstraints c;

        setLayout(new GridBagLayout());

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("HOST A SERVER");
        headerLabel.setFont(Resources.getInstance().getTitleFont());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 10;
        c.insets = new Insets(0, 50, 0, 0);
        c.anchor = GridBagConstraints.WEST;
        topPanel.add(headerLabel, c);

        joinGameButton = new Buttons("JOIN GAME");
        joinGameButton.setVisible(false);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.ipadx = 80;
        c.ipady = 40;
        c.insets = new Insets(0, 0, 0, 10);
        c.anchor = GridBagConstraints.EAST;
        topPanel.add(joinGameButton, c);

        backButton = new Buttons("BACK");
        c = new GridBagConstraints();
        c.gridx = 2;
        c.ipadx = 80;
        c.ipady = 40;
        c.insets = new Insets(0, 0, 0, 50);
        c.anchor = GridBagConstraints.EAST;
        topPanel.add(backButton, c);


        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(topPanel, c);

        JPanel leftPanel = createLeftPanel();
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 50;
        c.weightx = 1;
        add(leftPanel, c);

        JPanel rightPanel = createRightPanel();
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 50;
        c.weightx = 1;
        add(rightPanel, c);
    }

    private GridBagConstraints getFieldConstraints(int gridx, int gridy) {
        GridBagConstraints c = getFieldConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        return c;
    }

    private GridBagConstraints getFieldConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.ipadx = 10;
        c.ipady = 10;
        c.insets = new Insets(10, 50, 10, 50);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private JPanel createLeftPanel() {
        GridBagConstraints c;
        JPanel panel = new JPanel(new GridBagLayout());

        panel.add(new JLabel("SERVER STATUS"), getFieldConstraints(0, 0));

        serverStatusLabel = new JLabel("OFFLINE");
        serverStatusLabel.setForeground(Color.RED);
        panel.add(serverStatusLabel, getFieldConstraints(1, 0));


        panel.add(new JLabel("SERVER NAME"), getFieldConstraints(0, 1));

        serverNameTextField = new JTextField("Auto-Mataria Server");
        serverNameTextField.setMargin(new Insets(4, 6, 0, 0));
        panel.add(serverNameTextField, getFieldConstraints(1, 1));


        panel.add(new JLabel("MAP POOL"), getFieldConstraints(0, 2));

        mapsComboBox = new JComboBox<>(Maps.getInstance().getMapList());
        panel.add(mapsComboBox, getFieldConstraints(1, 2));


        panel.add(new JLabel("SERVER PROFILE"), getFieldConstraints(0, 3));

        profileComboBox = new JComboBox<>(new String[]{
                "CUSTOM", "LOW PERFORMANCE", "NORMAL (DEFAULT)", "HIGH PERFORMANCE", "EXTREME PERFORMANCE"});
        panel.add(profileComboBox, getFieldConstraints(1, 3));


        panel.add(new JLabel("SERVER PORT"), getFieldConstraints(0, 4));

        portTextField = new JTextField("32000");
        portTextField.setMargin(new Insets(4, 6, 0, 0));
        panel.add(portTextField, getFieldConstraints(1, 4));


        startButton = new Buttons("START SERVER");
        c = getFieldConstraints(0, 5);
        c.ipady = 20;
        panel.add(startButton, c);

        stopButton = new Buttons("STOP SERVER");
        stopButton.setEnabled(false);
        c = getFieldConstraints(1, 5);
        c.ipady = 20;
        panel.add(stopButton, c);

        return panel;
    }

    private JPanel createRightPanel() {
        GridBagConstraints c;
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel serverProfileLabel = new JLabel("CUSTOM ADVANCED SETTINGS");
        serverProfileLabel.setForeground(Color.LIGHT_GRAY);
        c = getFieldConstraints(0, 0);
        c.gridwidth = 2;
        panel.add(serverProfileLabel, c);


        panel.add(new JLabel("TICK RATE"), getFieldConstraints(0, 1));

        tickRateSlider = new JSlider(50, 200, 100);
        tickRateSlider.setMajorTickSpacing(50);
        tickRateSlider.setPaintLabels(true);
        tickRateSlider.setSnapToTicks(true);
        panel.add(tickRateSlider, getFieldConstraints(1, 1));


        panel.add(new JLabel("TICKS / UPDATE"), getFieldConstraints(0, 2));

        ticksPerUpdateSlider = new JSlider(1, 4, 2);
        ticksPerUpdateSlider.setMajorTickSpacing(1);
        ticksPerUpdateSlider.setPaintLabels(true);
        tickRateSlider.setSnapToTicks(true);
        panel.add(ticksPerUpdateSlider, getFieldConstraints(1, 2));


        panel.add(new JLabel("PLAYER SPEED"), getFieldConstraints(0, 3));

        playerSpeedSlider = new JSlider(0, Game.GRID_PIXEL_SIZE, Game.GRID_PIXEL_SIZE / 4);
        playerSpeedSlider.setMajorTickSpacing(Game.GRID_PIXEL_SIZE / 4);
        playerSpeedSlider.setPaintLabels(true);
        playerSpeedSlider.setSnapToTicks(true);
        panel.add(playerSpeedSlider, getFieldConstraints(1, 3));

        return panel;
    }

    private void addListeners() {
        profileComboBox.addActionListener(e -> {
            String profile = (String) profileComboBox.getSelectedItem();

            if (profile.equals("CUSTOM")) {
                playerSpeedSlider.setEnabled(true);
                tickRateSlider.setEnabled(true);
                ticksPerUpdateSlider.setEnabled(true);
                return;
            }

            playerSpeedSlider.setEnabled(false);
            tickRateSlider.setEnabled(false);
            ticksPerUpdateSlider.setEnabled(false);

            switch (profile) {
                case "LOW PERFORMANCE":
                    playerSpeedSlider.setValue(75);
                    tickRateSlider.setValue(150);
                    ticksPerUpdateSlider.setValue(2);
                    break;
                case "HIGH PERFORMANCE":
                    playerSpeedSlider.setValue(15);
                    tickRateSlider.setValue(50);
                    ticksPerUpdateSlider.setValue(2);
                    break;
                case "EXTREME PERFORMANCE":
                    playerSpeedSlider.setValue(15);
                    tickRateSlider.setValue(50);
                    ticksPerUpdateSlider.setValue(1);
                    break;
                default:
                    playerSpeedSlider.setValue(50);
                    tickRateSlider.setValue(100);
                    ticksPerUpdateSlider.setValue(2);
            }
        });

        startButton.addActionListener(e -> {
            if (server != null) return;
            try {
                String name = serverNameTextField.getText();
                int port = Integer.parseInt(portTextField.getText());
                int tickRate = tickRateSlider.getValue();
                int ticksBetweenUpdates = ticksPerUpdateSlider.getValue();
                int playerSpeed = playerSpeedSlider.getValue();
                String map = (String) mapsComboBox.getSelectedItem();

                server = new GameServer(name, port, tickRate, ticksBetweenUpdates, playerSpeed, Maps.getInstance().get(map));
                server.start();
                serverStatusLabel.setText("ONLINE");
                serverStatusLabel.setForeground(Color.GREEN);
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                joinGameButton.setVisible(true);
            } catch (Exception error) {
                serverStatusLabel.setText("OFFLINE");
                serverStatusLabel.setForeground(Color.RED);
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                joinGameButton.setVisible(false);
            }
        });

        stopButton.addActionListener(e -> {
            if (server == null) return;
            server.stop();
            server = null;
            serverStatusLabel.setText("OFFLINE");
            serverStatusLabel.setForeground(Color.RED);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            joinGameButton.setVisible(false);
        });

        backButton.addActionListener(e -> {
            userInterface.changeToPreviousScreen();
        });

        joinGameButton.addActionListener(e -> {
            if (server != null) {
                userInterface.startGame("127.0.0.1", Integer.parseInt(portTextField.getText()));
            }
        });
    }
}
