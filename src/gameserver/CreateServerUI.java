package gameserver;

import common.GameMap;

import javax.swing.*;
import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class CreateServerUI {
    private static GameServer server;
    private static void start(int port, int updatesPerSecond, int players) {
        GameMap map = new GameMap("default");
        map.setEdgeWalls(Color.CYAN.darker().darker());
        server = new GameServer(port, updatesPerSecond, players, map);
    }
    private static void stop() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            JPanel panel = new JPanel();
            JButton start = new JButton("Start Server");
            JButton stop = new JButton("Stop Server");
            JTextField serverPort = new JTextField("32000");
            JTextField updatesPerSecond = new JTextField("30");
            JTextField players = new JTextField("4");
            frame.add(panel);
            panel.add(serverPort);
            panel.add(updatesPerSecond);
            panel.add(players);
            panel.add(start);
            panel.add(stop);
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setTitle("Auto-Mataria Server Manager");

            start.addActionListener(e -> {
                int port = Integer.parseInt(serverPort.getText());
                int ups = Integer.parseInt(updatesPerSecond.getText());
                int p = Integer.parseInt(players.getText());
                start(port, ups, p);
            });
            stop.addActionListener(e -> stop());
        });
    }
}
