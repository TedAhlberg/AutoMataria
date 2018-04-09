package common;

import gameclient.Game;
import gameobjects.GameObject;
import gameobjects.Wall;
import gameserver.GameServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Johannes Blüml
 */
public class TestUI {
    private JPanel container;
    private JTextField tfClientIP, tfClientPort, tfPlayerSpeedTick, tfFramesPerSecond, tfPlayerSpeedSecond,
            tfServerPort, tfPlayers, tfTickRate, tfUpdateRate, tfMapWidth, tfMapHeight;
    private JButton btnStartGame, btnStartServer, btnStopServer, btnChangeWallColor, btnChangeWallBorderColor;
    private JCheckBox checkFullscreen;
    private JComboBox cbWindowSize;
    private JLabel lblWindowSize;

    private GameServer server;
    private Color mapWallColor = Color.CYAN.darker().darker().darker();
    private Color mapWallBorderColor = Color.CYAN.darker();

    private String clientIP;
    private int serverPort, tickRate, updateRate, players, mapWidth, mapHeight, clientPort, framesPerSecond, clientWidth, clientHeight;
    private double playerSpeedTick, playerSpeedSecond;
    private boolean fullscreen;

    TestUI() {
        tfPlayerSpeedTick.setEnabled(false);

        container.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                SwingUtilities.invokeLater(() -> {
                    updateAllVariables();
                    updateAllTextFields();
                });
            }
        });
        SwingUtilities.invokeLater(this::updateAllVariables);

        checkFullscreen.addChangeListener(e -> {
            fullscreen = checkFullscreen.isSelected();
            lblWindowSize.setEnabled(!fullscreen);
            cbWindowSize.setEnabled(!fullscreen);
        });

        btnStartGame.addActionListener(e -> {
            updateAllVariables();
            System.out.println(toString());
            if (fullscreen) {
                new Game(clientIP, clientPort, framesPerSecond);
            } else {
                new Game(clientIP, clientPort, new Dimension(clientWidth, clientHeight), framesPerSecond);
            }
        });

        btnStartServer.addActionListener(e -> {
            updateAllVariables();
            System.out.println(toString());
            GameMap map = new GameMap();
            map.setBackground("resources/Stars.png");
            map.setMusicTrack("resources/Music/AM-trck1.mp3");
            map.setPlayers(players);
            map.setPlayerSpeed(playerSpeedTick);
            map.setGrid(new Dimension(mapWidth, mapHeight));
            Wall wall = new Wall(mapWallColor, mapWallBorderColor);
            int width = mapWidth * Game.GRID_PIXEL_SIZE;
            int height = mapHeight * Game.GRID_PIXEL_SIZE;
            wall.add(new Rectangle(0, 0, Game.GRID_PIXEL_SIZE, width));
            wall.add(new Rectangle(height - Game.GRID_PIXEL_SIZE, 0, Game.GRID_PIXEL_SIZE, width));
            wall.add(new Rectangle(0, 0, height, Game.GRID_PIXEL_SIZE));
            wall.add(new Rectangle(0, height - Game.GRID_PIXEL_SIZE, height, Game.GRID_PIXEL_SIZE));
            GameObject[] startingObjects = {wall};
            map.setStartingGameObjects(startingObjects);
            if (server == null) server = new GameServer("AM-test-server", serverPort, tickRate, updateRate, map);
        });
        btnStopServer.addActionListener(e -> {
            if (server != null) {
                server.stop();
                server = null;
            }
        });

        btnChangeWallColor.setForeground(mapWallColor);
        btnChangeWallColor.addActionListener(e -> {
            mapWallColor = JColorChooser.showDialog(null, "Wall Color", mapWallColor);
            btnChangeWallColor.setForeground(mapWallColor);
        });
        btnChangeWallBorderColor.setForeground(mapWallBorderColor);
        btnChangeWallBorderColor.addActionListener(e -> {
            mapWallBorderColor = JColorChooser.showDialog(null, "Wall Border Color", mapWallBorderColor);
            btnChangeWallBorderColor.setForeground(mapWallBorderColor);
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TestUI2");
        frame.setContentPane(new TestUI().container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void updateAllTextFields() {
        // CLIENT
        cbWindowSize.setSelectedItem(clientWidth + " " + clientHeight);
        checkFullscreen.setSelected(fullscreen);
        tfClientIP.setText(clientIP);
        tfClientPort.setText(Integer.toString(clientPort));
        tfFramesPerSecond.setText(Integer.toString(framesPerSecond));
        // SERVER
        tfServerPort.setText(Integer.toString(serverPort));
        tfTickRate.setText(Integer.toString(tickRate));
        tfUpdateRate.setText(Integer.toString(updateRate));
        tfPlayers.setText(Integer.toString(players));
        // SERVER MAP
        tfMapWidth.setText(Integer.toString(mapWidth));
        tfMapHeight.setText(Integer.toString(mapHeight));
        tfPlayerSpeedTick.setText(Double.toString(playerSpeedTick));
        tfPlayerSpeedSecond.setText(Double.toString(playerSpeedSecond));
    }

    private void updateAllVariables() {
        try {
            // CLIENT
            String[] parts = cbWindowSize.getSelectedItem().toString().split("x");
            clientWidth = Integer.parseInt(parts[0]);
            clientHeight = Integer.parseInt(parts[1]);
            fullscreen = checkFullscreen.isSelected();
            clientIP = tfClientIP.getText();
            clientPort = Integer.parseInt(tfClientPort.getText());
            framesPerSecond = Integer.parseInt(tfFramesPerSecond.getText());
            // SERVER
            serverPort = Integer.parseInt(tfServerPort.getText());
            int tickRate = Integer.parseInt(tfTickRate.getText());
            updateRate = Integer.parseInt(tfUpdateRate.getText());
            players = Integer.parseInt(tfPlayers.getText());
            //SERVER MAP
            mapWidth = Integer.parseInt(tfMapWidth.getText());
            mapHeight = Integer.parseInt(tfMapHeight.getText());
            double playerSpeedTick = Double.parseDouble(tfPlayerSpeedTick.getText());
            double playerSpeedSecond = Double.parseDouble(tfPlayerSpeedSecond.getText());

            // Some special calculation for a better user experience
            if (tickRate != this.tickRate || playerSpeedTick != this.playerSpeedTick) {
                this.playerSpeedSecond = (1000 / tickRate) * playerSpeedTick;
                this.tickRate = tickRate;
                this.playerSpeedTick = playerSpeedTick;
            } else if (playerSpeedSecond != this.playerSpeedSecond) {
                playerSpeedSecond = Math.round(playerSpeedSecond * 2.0) / 2.0;
                this.playerSpeedTick = playerSpeedSecond / (1000 / tickRate);
                this.playerSpeedSecond = playerSpeedSecond;
            } else {
                this.tickRate = tickRate;
                this.playerSpeedSecond = playerSpeedSecond;
                this.playerSpeedTick = playerSpeedTick;
            }

        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(null, "Please enter only numbers.");
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        container = new JPanel();
        container.setLayout(new GridBagLayout());
        tfServerPort = new JTextField();
        tfServerPort.setText("32000");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfServerPort, gbc);
        tfMapWidth = new JTextField();
        tfMapWidth.setText("50");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfMapWidth, gbc);
        tfMapHeight = new JTextField();
        tfMapHeight.setText("50");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfMapHeight, gbc);
        btnChangeWallColor = new JButton();
        btnChangeWallColor.setText("Change");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(btnChangeWallColor, gbc);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Client Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Game IP");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Game Port");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Fullscreen");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label4, gbc);
        lblWindowSize = new JLabel();
        lblWindowSize.setText("Window Size");
        lblWindowSize.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(lblWindowSize, gbc);
        tfClientIP = new JTextField();
        tfClientIP.setText("127.0.0.1");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfClientIP, gbc);
        tfClientPort = new JTextField();
        tfClientPort.setText("32000");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfClientPort, gbc);
        cbWindowSize = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("1000x1000");
        defaultComboBoxModel1.addElement("750x750");
        defaultComboBoxModel1.addElement("500x500");
        cbWindowSize.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(cbWindowSize, gbc);
        checkFullscreen = new JCheckBox();
        checkFullscreen.setEnabled(true);
        checkFullscreen.setSelected(false);
        checkFullscreen.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(checkFullscreen, gbc);
        btnStartGame = new JButton();
        btnStartGame.setText("Start Game");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 9;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(btnStartGame, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        container.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        container.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer3, gbc);
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$(null, Font.BOLD, -1, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("Server Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Server Port");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label6, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        container.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer5, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Wall Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label7, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Horizontal Grids");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 15;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label8, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Vertical Grids");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 16;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label9, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 17;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer6, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 19;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 21;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer8, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer9, gbc);
        btnStartServer = new JButton();
        btnStartServer.setText("Start Server");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 18;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(btnStartServer, gbc);
        btnStopServer = new JButton();
        btnStopServer.setText("Stop Server");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 20;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(btnStopServer, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("PlayerSpeed (grids/sec)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label10, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Frame Rate (FPS)");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label11, gbc);
        tfFramesPerSecond = new JTextField();
        tfFramesPerSecond.setText("60");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfFramesPerSecond, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer10, gbc);
        final JLabel label12 = new JLabel();
        Font label12Font = this.$$$getFont$$$(null, Font.BOLD, -1, label12.getFont());
        if (label12Font != null) label12.setFont(label12Font);
        label12.setText("Map Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label12, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer11, gbc);
        final JLabel label13 = new JLabel();
        label13.setText("PlayerSpeed (grids/tick)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label13, gbc);
        tfPlayerSpeedTick = new JTextField();
        tfPlayerSpeedTick.setText("0.25");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfPlayerSpeedTick, gbc);
        tfPlayerSpeedSecond = new JTextField();
        tfPlayerSpeedSecond.setText("5.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfPlayerSpeedSecond, gbc);
        final JLabel label14 = new JLabel();
        label14.setText("Wall Border Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label14, gbc);
        btnChangeWallBorderColor = new JButton();
        btnChangeWallBorderColor.setText("Change");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(btnChangeWallBorderColor, gbc);
        final JLabel label15 = new JLabel();
        label15.setText("Players");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label15, gbc);
        tfPlayers = new JTextField();
        tfPlayers.setText("5");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfPlayers, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        container.add(spacer12, gbc);
        final JLabel label16 = new JLabel();
        label16.setText("Tick Rate (ms)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label16, gbc);
        tfTickRate = new JTextField();
        tfTickRate.setText("50");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfTickRate, gbc);
        final JLabel label17 = new JLabel();
        label17.setText("Update Rate (ms)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label17, gbc);
        tfUpdateRate = new JTextField();
        tfUpdateRate.setText("150");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfUpdateRate, gbc);
        label2.setLabelFor(tfClientIP);
        label3.setLabelFor(tfClientPort);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {resultName = currentFont.getName();} else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {resultName = fontName;} else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return container; }
}
