package gameclient.interfaces;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyboardFocusManager;
import java.util.HashSet;

import javax.swing.JPanel;

import common.Action;
import common.Direction;
import common.GameMap;
import common.GameServerUpdate;
import common.GameState;
import common.Utility;
import common.messages.ConnectionMessage;
import common.messages.PlayerMessage;
import gameclient.Audio;
import gameclient.GamePanel;
import gameclient.GameServerConnection;
import gameclient.GameServerListener;
import gameclient.MusicManager;
import gameclient.SoundFx;
import gameclient.keyinput.KeyInput;
import gameobjects.GameObject;
import gameobjects.Player;

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

    public GameScreen() {
        setLayout(new GridBagLayout());
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyInput(this));
    }


    public void connect(String ip, int port, String username) {
        if (gamePanel == null) {
            gamePanel = new GamePanel(getSize());
            add(gamePanel);
            revalidate();
        }
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
                    }
                    
                    else if(playerMessage.getEvent() == PlayerMessage.Event.Ready) {
                        System.out.println(playerMessage.getPlayer().getName() + " is ready.");
                    }
                    else if(playerMessage.getEvent() == PlayerMessage.Event.Unready) {
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
