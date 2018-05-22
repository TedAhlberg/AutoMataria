package gameclient.interfaces.hostserverscreen;

import common.*;
import gameclient.Resources;
import gameclient.interfaces.*;
import gameserver.GameServer;

import javax.swing.*;
import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class HostServerScreen extends JPanel implements UserInterfaceScreen {
    private AMButton startButton, stopButton, backButton, joinGameButton;
    private JComboBox<String> profileComboBox;
    private JSlider playerSpeedSlider, tickRateSlider, ticksPerUpdateSlider;
    private JComboBox<String> mapsComboBox;
    private JTextField serverNameTextField;
    private JLabel serverStatusLabel;
    private GameServer server;
    private UserInterface userInterface;
    private JSlider roundLimitSlider, scoreLimitSlider, portSlider;
    private MapPoolPanel mapPoolPanel;

    public HostServerScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        setOpaque(false);
        createLayout();
        addListeners();
        profileComboBox.setSelectedIndex(2);
    }

    public static void main(String[] args) {
        gameclient.interfaces.Window window = new gameclient.interfaces.Window("HostServerScreen", new Dimension(1440, 800));
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

        joinGameButton = new AMButton("JOIN GAME");
        joinGameButton.setVisible(false);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.ipadx = 10;
        c.ipady = 10;
        c.insets = new Insets(0, 0, 0, 10);
        c.anchor = GridBagConstraints.EAST;
        topPanel.add(joinGameButton, c);

        backButton = new AMButton("BACK");
        c = new GridBagConstraints();
        c.gridx = 2;
        c.ipadx = 10;
        c.ipady = 10;
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
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private JPanel createCustomSlider(JSlider slider) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(Integer.toString(slider.getValue()));

        slider.addChangeListener(e -> label.setText(Integer.toString(slider.getValue())));

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(slider, c);

        c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        panel.add(label, c);

        return panel;
    }

    private JPanel createLeftPanel() {
        GridBagConstraints c;
        JPanel panel = new JPanel(new GridBagLayout());

        panel.add(new JLabel("SERVER STATUS"), getFieldConstraints(0, 0));
        serverStatusLabel = new JLabel("OFFLINE");
        serverStatusLabel.setForeground(Color.RED);
        panel.add(serverStatusLabel, getFieldConstraints(1, 0));

        panel.add(new JLabel("SERVER NAME"), getFieldConstraints(0, 1));
        serverNameTextField = new JTextField(userInterface.getSettingsScreen().getUsername() + " SERVER");
        panel.add(serverNameTextField, getFieldConstraints(1, 1));

        panel.add(new JLabel("MAP"), getFieldConstraints(0, 2));
        mapsComboBox = new JComboBox<>(Maps.getInstance().getMapList().toArray(new String[0]));
        panel.add(mapsComboBox, getFieldConstraints(1, 2));

        panel.add(new JLabel("ROUND LIMIT"), getFieldConstraints(0, 3));
        roundLimitSlider = new JSlider(0, 25, 3);
        roundLimitSlider.setSnapToTicks(true);
        roundLimitSlider.setMajorTickSpacing(1);
        panel.add(createCustomSlider(roundLimitSlider), getFieldConstraints(1, 3));

        panel.add(new JLabel("SCORE LIMIT"), getFieldConstraints(0, 4));
        scoreLimitSlider = new JSlider(0, 100, 0);
        scoreLimitSlider.setSnapToTicks(true);
        scoreLimitSlider.setMajorTickSpacing(5);
        panel.add(createCustomSlider(scoreLimitSlider), getFieldConstraints(1, 4));

        panel.add(new JLabel("SERVER PORT"), getFieldConstraints(0, 5));
        portSlider = new JSlider(32000, 32100, 32000);
        panel.add(createCustomSlider(portSlider), getFieldConstraints(1, 5));

        startButton = new AMButton("START SERVER");
        c = getFieldConstraints(0, 6);
        panel.add(startButton, c);

        stopButton = new AMButton("STOP SERVER");
        stopButton.setEnabled(false);
        c = getFieldConstraints(1, 6);
        panel.add(stopButton, c);

        return panel;
    }

    private JPanel createRightPanel() {
        GridBagConstraints c;
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel mapPoolLabel = new JLabel("MAP POOL");
        c = getFieldConstraints(0, 0);
        c.gridwidth = 2;
        panel.add(mapPoolLabel, c);
        mapPoolPanel = new MapPoolPanel();
        c = getFieldConstraints(0, 1);
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        panel.add(mapPoolPanel, c);

        JLabel serverProfileLabel = new JLabel("ADVANCED SETTINGS");
        serverProfileLabel.setForeground(Color.LIGHT_GRAY);
        c = getFieldConstraints(0, 2);
        c.gridwidth = 2;
        panel.add(serverProfileLabel, c);

        panel.add(new JLabel("SERVER PROFILE"), getFieldConstraints(0, 3));
        profileComboBox = new JComboBox<>(new String[]{
                "CUSTOM", "HIGH (LAN)", "NORMAL (WIFI)"});
        panel.add(profileComboBox, getFieldConstraints(1, 3));

        panel.add(new JLabel("TICK RATE"), getFieldConstraints(0, 4));
        tickRateSlider = new JSlider(10, 200, 25);
        panel.add(createCustomSlider(tickRateSlider), getFieldConstraints(1, 4));

        panel.add(new JLabel("TICKS / UPDATE"), getFieldConstraints(0, 5));
        ticksPerUpdateSlider = new JSlider(1, 10, 4);
        panel.add(createCustomSlider(ticksPerUpdateSlider), getFieldConstraints(1, 5));

        panel.add(new JLabel("PLAYER SPEED"), getFieldConstraints(0, 6));
        playerSpeedSlider = new JSlider(Game.GRID_PIXEL_SIZE / 4, Game.GRID_PIXEL_SIZE * 2, Game.GRID_PIXEL_SIZE / 4);
        panel.add(createCustomSlider(playerSpeedSlider), getFieldConstraints(1, 6));

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
                case "HIGH (LAN)":
                    playerSpeedSlider.setValue(25);
                    tickRateSlider.setValue(25);
                    ticksPerUpdateSlider.setValue(2);
                    break;
                case "NORMAL (WIFI)":
                    playerSpeedSlider.setValue(50);
                    tickRateSlider.setValue(50);
                    ticksPerUpdateSlider.setValue(2);
                    break;
            }
        });

        mapsComboBox.addActionListener(e -> {
            if (server == null) return;
            String map = (String) mapsComboBox.getSelectedItem();
            server.changeMap(Maps.getInstance().get(map));
        });

        startButton.addActionListener(e -> {
            if (server != null) return;
            GameServerSettings settings = new GameServerSettings();
            settings.name = serverNameTextField.getText();
            settings.port = portSlider.getValue();
            settings.tickRate = tickRateSlider.getValue();
            settings.amountOfTickBetweenUpdates = ticksPerUpdateSlider.getValue();
            settings.playerSpeed = playerSpeedSlider.getValue();
            settings.mapPool = mapPoolPanel.getSelectedMaps();
            settings.roundLimit = roundLimitSlider.getValue();
            settings.scoreLimit = scoreLimitSlider.getValue();

            server = new GameServer(settings, event -> {
                if (event == GameServer.Event.Stopped) {
                    serverStatusLabel.setText("OFFLINE");
                    serverStatusLabel.setForeground(Color.RED);
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    joinGameButton.setVisible(false);
                    setAllEnabled(true);
                    server = null;
                } else if (event == GameServer.Event.Started) {
                    server.changeMap(Maps.getInstance().get((String) mapsComboBox.getSelectedItem()));
                    serverStatusLabel.setText("ONLINE");
                    serverStatusLabel.setForeground(Color.GREEN);
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    joinGameButton.setVisible(true);
                    setAllEnabled(false);
                    int result = JOptionPane.showConfirmDialog(this, "SERVER STARTED.\nDO YOU WANT TO JOIN IT?");
                    if (result == 0) {
                        userInterface.startGame("127.0.0.1", portSlider.getValue());
                    }
                }
            });
            server.start();
        });

        stopButton.addActionListener(e -> {
            if (server == null) return;
            server.stop();
        });

        backButton.addActionListener(e -> {
            userInterface.changeToPreviousScreen();
        });

        joinGameButton.addActionListener(e -> {
            if (server != null) {
                userInterface.startGame("127.0.0.1", portSlider.getValue());
            }
        });
    }

    private void setAllEnabled(boolean enabled) {
        serverNameTextField.setEnabled(enabled);
        roundLimitSlider.setEnabled(enabled);
        scoreLimitSlider.setEnabled(enabled);
        portSlider.setEnabled(enabled);
        mapPoolPanel.setEnabled(enabled);

        profileComboBox.setEnabled(enabled);
        tickRateSlider.setEnabled(enabled);
        ticksPerUpdateSlider.setEnabled(enabled);
        playerSpeedSlider.setEnabled(enabled);
        if (enabled) {
            profileComboBox.setSelectedIndex(profileComboBox.getSelectedIndex());
        }
    }

    public void onScreenActive() {
        if (server == null) {
            mapsComboBox.removeAllItems();
            for (String map : Maps.getInstance().getMapList()) {
                mapsComboBox.addItem(map);
            }
            mapPoolPanel.loadMaps();
        } else {
            String currentMap = server.getServerInformation().getMapName();
            mapsComboBox.setSelectedItem(currentMap);
        }
    }

    public void onScreenInactive() {
    }
}
