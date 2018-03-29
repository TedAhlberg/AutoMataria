package common;

import gameclient.Game;
import gameserver.GameServer;

import javax.swing.*;
import java.awt.*;

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

    private GameServer server;
    private Color mapWallColor = Color.CYAN;

    TestUI() {

        checkFullscreen.addChangeListener(e -> {
            lblWindowSize.setEnabled(!checkFullscreen.isSelected());
            cbWindowSize.setEnabled(!checkFullscreen.isSelected());
        });

        btnStartGame.addActionListener(e -> {
            String clientIP = tfClientIP.getText();
            int clientPort = Integer.parseInt(tfClientPort.getText());
            if (checkFullscreen.isSelected()) {
                new Game(clientIP, clientPort);
            } else {
                String[] parts = cbWindowSize.getSelectedItem().toString().split("x");
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                new Game(clientIP, clientPort, new Dimension(width, height));
            }
        });

        btnStartServer.addActionListener(e -> {
            int serverPort = Integer.parseInt(tfServerPort.getText());
            int tickRate = Integer.parseInt(tfTickRate.getText());
            int updateRate = Integer.parseInt(tfUpdateRate.getText());
            int players = Integer.parseInt(tfPlayers.getText());
            int mapWidth = Integer.parseInt(tfMapWidth.getText());
            int mapHeight = Integer.parseInt(tfMapHeight.getText());

            GameMap map = new GameMap("default");
            map.setWidth(mapWidth);
            map.setHeight(mapHeight);
            map.setEdgeWalls(mapWallColor);
            server = new GameServer(serverPort, tickRate, updateRate, players, map);
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("TestUI2");
        frame.setContentPane(new TestUI().container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
