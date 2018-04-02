package gameclient;

import common.Action;
import common.*;
import gameobjects.Player;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * @author Johannes Bluml
 */
public class Game {
    public static final String TITLE = "Auto-Mataria";
    public static final int GRID_PIXEL_SIZE = 100;
    private final GamePanel gamePanel;

    private GameServerConnection client;
    private Audio backgroundMusic = new Audio("AM-trck1.mp3");

    public Game() {
        this("127.0.0.1", 32000, null, 100);
    }

    public Game(String serverIP, int serverPort, int frameRate) {
        this(serverIP, serverPort, null, frameRate);
    }

    public Game(String serverIP, int serverPort, Dimension windowSize, int framesPerSecond) {
        String playerName = JOptionPane.showInputDialog("Enter your username:", "Username");

        Window window = new Window(TITLE, windowSize);

        gamePanel = new GamePanel();
        gamePanel.setSize(window.getSize());
        window.add(gamePanel);
        window.pack();

        client = new GameServerConnection(new GameServerListener() {
            public void onConnect() {
                System.out.println("Connected to server.");
                client.send(playerName);
            }

            public void onDisconnect() {
                backgroundMusic.stop();
                gamePanel.stop();
                window.dispose();
            }

            public void onData(Object data) {
                if (data instanceof Player) {
                    gamePanel.setPlayer((Player) data);
                } else if (data instanceof GameMap) {
                    GameMap map = (GameMap) data;
                    gamePanel.setBackground(map.getBackground());
                    gamePanel.setGrid(map.getGrid());
                    double scale = Math.min((double) gamePanel.getWidth() / map.getWidth(), (double) gamePanel.getHeight() / map.getHeight());
                    gamePanel.start(scale, framesPerSecond, map.getPlayerSpeedPerSecond());
                    backgroundMusic.play();
                }
                if (data instanceof Collection) {
                    gamePanel.updateGameObjects((Collection) data);
                }
            }
        });

        gamePanel.addKeyListener(new KeyInput(this));
        gamePanel.requestFocus();

        client.connect(serverIP, serverPort);
    }

    public static int clamp(int var, int min, int max) {
        if (var >= max) return max;
        if (var <= min) return min;
        return var;
    }

    public void onKeyPress(Action action) {
        if (action == Action.ExitGame) {
            if (client != null) {
                client.disconnect();
            } else {
                System.exit(0);
            }
        } else if (action == Action.ToggleInterpolation) {
            gamePanel.toggleInterpolation();
        } else {
            client.send(action);
        }
    }

    public void onKeyPress(Direction direction) {
        client.send(direction);
    }
}
