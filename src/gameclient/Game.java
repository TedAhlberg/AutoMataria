package gameclient;

import common.Action;
import common.*;
import common.messages.ConnectionMessage;
import common.messages.GameEventMessage;
import gameobjects.GameObject;
import gameobjects.Player;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

/**
 * @author Johannes Bluml
 */
public class Game {
    public static final String TITLE = "Auto-Mataria";
    public static final int GRID_PIXEL_SIZE = 100;
    private final GamePanel gamePanel;

    private GameServerConnection client;
    private Audio backgroundMusic;
    private HashSet<Player> players = new HashSet<>();
    private Player player;

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

        window.setContentPane(gamePanel);
        window.pack();

        client = new GameServerConnection(new GameServerListener() {
            public void onConnect() {
                System.out.println("Connected to server.");
                client.send(playerName);
            }

            public void onDisconnect() {
                if (backgroundMusic != null) {
                    backgroundMusic.stop();
                }
                gamePanel.stop();
                window.dispose();
            }

            public void onData(Object data) {
                if (data instanceof GameServerUpdate) {
                    GameServerUpdate message = (GameServerUpdate) data;
                    gamePanel.updateGameObjects(message.gameObjects);
                    gamePanel.setGameState(message.state);
                    if (message.state == GameState.Warmup) {
                        for (GameObject gameObject : message.gameObjects) {
                            if (gameObject instanceof Player) {
                                if (gameObject.equals(player)) {
                                    player = (Player) gameObject;
                                } else {
                                    players.add((Player) gameObject);
                                }
                            }
                        }
                        gamePanel.setReadyPlayers(Utility.getReadyPlayerPercentage(players));
                    }
                    player = message.player;
                } else if (data instanceof ConnectionMessage) {
                    ConnectionMessage message = (ConnectionMessage) data;
                    if (message.success) {
                        gamePanel.setServerTickRate(message.tickRate);
                        player = message.player;
                        GameMap map = message.currentMap;
                        gamePanel.setBackground(map.getBackground());
                        gamePanel.setGrid(map.getGrid());
                        gamePanel.start(framesPerSecond);
                        backgroundMusic = Audio.getTrack(map.getMusicTrack());
                        backgroundMusic.play(99);
                        System.out.println("CLIENT: Connected to server successfully");
                    } else {
                        gamePanel.stop();
                        System.out.println("CLIENT: Failed to connect to server");
                    }
                } else if (data instanceof GameEventMessage) {
                    readGameEventMessage((GameEventMessage) data);
                }
            }
        });

//        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyInput(this));

        gamePanel.requestFocus();

        client.connect(serverIP, serverPort);
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
        } else if (action == Action.ToggleDebugText) {
            gamePanel.toggleDebugInfo();
        } else {
            client.send(action);
        }
    }

    public void readGameEventMessage(GameEventMessage message) {
        if ((message).data.equals("crash")) {
            SoundFx.getInstance().crash();
        } else if ((message).data.equals("SelfSpeedPickup")) {
            SoundFx.getInstance().SelfSpeedPickup();
        }

    }

    public void onKeyPress(Direction direction) {
        client.send(direction);
    }
}
