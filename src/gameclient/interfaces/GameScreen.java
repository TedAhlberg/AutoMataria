package gameclient.interfaces;

import common.Action;
import common.*;
import common.messages.ConnectionMessage;
import common.messages.PlayerMessage;
import gameclient.*;
import gameclient.keyinput.KeyInput;
import gameobjects.GameObject;
import gameobjects.Player;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

public class GameScreen extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private GamePanel gamePanel;
    private GameServerConnection client;
    private Player player;
    private Audio backgroundMusic;
    private HashSet<Player> players = new HashSet<>();
    private int framesPerSecond = 60;
    private UserInterface userInterface;

    public GameScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 1;
        add(createLeftPanel(), gbc);

        gamePanel = new GamePanel();
        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 3;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(gamePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 1;
        add(createRightPanel(), gbc);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyInput(this));
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        Buttons exitButton = new Buttons("EXIT");
        exitButton.addActionListener(e -> {
            MusicManager.changeTrack();
            client.disconnect();
            gamePanel.stop();
            userInterface.changeScreen("StartScreen");
            MusicManager.getInstance().menuTrack();
        });
        c.ipadx = 20;
        c.ipady = 20;
        c.anchor = GridBagConstraints.NORTHEAST;
        panel.add(exitButton, c);

        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        return panel;
    }


    public void connect(String ip, int port, String username) {
        client = new GameServerConnection(new GameServerListener() {
            public void onConnect() {
                System.out.println("Connected to server.");
                client.send(username);
            }

            public void onDisconnect() {
                if (backgroundMusic != null) {
                    backgroundMusic.stop();
                }
                gamePanel.stop();
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
                        MusicManager.changeTrack();
                        MusicManager.getInstance().gameTrack1();
                        System.out.println("CLIENT: Connected to server successfully");
                    } else {
                        gamePanel.stop();
                        System.out.println("CLIENT: Failed to connect to server");
                    }
                } else if (data instanceof PlayerMessage) {
                    PlayerMessage playerMessage = (PlayerMessage) data;

                    if (playerMessage.getEvent() == (PlayerMessage.Event.Connected)) {
                        System.out.println(playerMessage.getPlayer().getName() + " has connected.");

                    } else if (playerMessage.getEvent() == PlayerMessage.Event.Disconnected) {
                        System.out.println(playerMessage.getPlayer().getName() + " has disconnected.");

                    } else if (playerMessage.getEvent() == PlayerMessage.Event.Crashed) {
                        System.out.println(playerMessage.getPlayer().getName() + " has crashed.");
                        SoundFx.getInstance().crash();

                    } else if (playerMessage.getEvent() == PlayerMessage.Event.ColorChange) {
                        System.out.println(playerMessage.getPlayer().getName() + " has changed Color to " + playerMessage.getPlayerColor());
                    } else if (playerMessage.getEvent() == PlayerMessage.Event.Ready) {
                        System.out.println(playerMessage.getPlayer().getName() + " is ready.");
                    } else if (playerMessage.getEvent() == PlayerMessage.Event.Unready) {
                        System.out.println(playerMessage.getPlayer().getName() + " is not ready.");
                    }

                }
            }
        });
        client.connect(ip, port);
        gamePanel.requestFocus();
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

    public void onKeyPress(Direction direction) {
        client.send(direction);
    }
}
