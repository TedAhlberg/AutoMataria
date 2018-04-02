package common;

import gameclient.Game;
import gameserver.GameServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Johannes Blüml
 */
public class TestUI {
    private JTextField tfClientIP;
    private JTextField tfClientPort;
    private JButton btnStartGame;
    private JPanel container;
    private JButton btnStartServer;
    private JButton btnStopServer;
    private JTextField tfServerPort;
    private JTextField tfPlayers;
    private JTextField tfTickRate;
    private JTextField tfUpdateRate;
    private JTextField tfMapWidth;
    private JTextField tfMapHeight;
    private JButton btnChangeWallColor;
    private JCheckBox checkFullscreen;
    private JLabel lblWindowSize;
    private JComboBox cbWindowSize;
    private JTextField tfPlayerSpeedTick;
    private JTextField tfFramesPerSecond;
    private JTextField tfPlayerSpeedSecond;

    private GameServer server;
    private Color mapWallColor = Color.CYAN;

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
            GameMap map = new GameMap("default");
            map.setPlayerSpeed(playerSpeedTick);
            map.setGrid(new Dimension(mapWidth, mapHeight));
            map.addEdgeWalls(mapWallColor);
            if (server == null) server = new GameServer(serverPort, tickRate, updateRate, players, map);
        });
        btnStopServer.addActionListener(e -> {
            if (server != null) {
                server.stop();
                server = null;
            }
        });

        btnChangeWallColor.setForeground(mapWallColor);
        btnChangeWallColor.addActionListener(e -> {
            mapWallColor = JColorChooser.showDialog(null, "Wall Color", Color.CYAN);
            btnChangeWallColor.setForeground(mapWallColor);
        });
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("TestUI2");
        frame.setContentPane(new TestUI().container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public String toString() {
        return "TestUI{" +
                "mapWallColor=" + mapWallColor +
                ", clientIP='" + clientIP + '\'' +
                ", serverPort=" + serverPort +
                ", tickRate=" + tickRate +
                ", updateRate=" + updateRate +
                ", players=" + players +
                ", mapWidth=" + mapWidth +
                ", mapHeight=" + mapHeight +
                ", clientPort=" + clientPort +
                ", framesPerSecond=" + framesPerSecond +
                ", clientWidth=" + clientWidth +
                ", clientHeight=" + clientHeight +
                ", playerSpeedTick=" + playerSpeedTick +
                ", playerSpeedSecond=" + playerSpeedSecond +
                ", fullscreen=" + fullscreen +
                '}';
    }
}
