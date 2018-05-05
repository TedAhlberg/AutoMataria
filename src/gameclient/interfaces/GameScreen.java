package gameclient.interfaces;

import common.Action;
import common.*;
import common.messages.*;
import gameclient.*;
import gameclient.sound.MusicManager;
import gameclient.sound.SoundFx;
import gameobjects.GameObject;
import gameobjects.Player;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * GameScreen is a JPanel that connects and displays the Game.
 * It has a GamePanel with the actual game.
 * But also panels on the sides to display information about the game.
 */
public class GameScreen extends JPanel implements GameServerListener {
    private GamePanel gamePanel;
    private GameServerConnection client;
    private Player player;
    private HashSet<Player> players = new HashSet<>();
    private final int framesPerSecond = 60;
    private UserInterface userInterface;
    private String username;

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
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        AMButton backButton = new AMButton("BACK");
        backButton.addActionListener(e -> {
            MusicManager.changeTrack();
            client.disconnect();
            gamePanel.stop();
            userInterface.changeToPreviousScreen();
            MusicManager.getInstance().menuTrack();
        });
        c.ipadx = 20;
        c.ipady = 20;
        c.anchor = GridBagConstraints.NORTHEAST;
        panel.add(backButton, c);

        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        return panel;
    }


    public void connect(String ip, int port, String username) {
        this.username = username;
        client = new GameServerConnection(this);
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

    /**
     * When we are connected to a GameServer this method will be called
     * So we send the username to login to the GameServer
     */
    public void onConnect() {
        System.out.println("Connected to server.");
        client.send(username);
    }

    /**
     * When we are disconnected from the GameServer this method is called
     */
    public void onDisconnect() {
        gamePanel.stop();
    }

    /**
     * When data from the GameServer is received this method is called with the data
     * this method will then dispatch the message to the correct method to handle the message
     *
     * @param data Data received from GameServer
     */
    public void onData(Object data) {
        if (data instanceof GameServerUpdate) {
            handleGameServerUpdate((GameServerUpdate) data);
        } else if (data instanceof ConnectionMessage) {
            handleConnectionMessage((ConnectionMessage) data);
        } else if (data instanceof PlayerMessage) {
            handlePlayerMessage((PlayerMessage) data);
        } else if (data instanceof PlayerPickupMessage) {
            handlePlayerPickupMessage((PlayerPickupMessage) data);
        } else if (data instanceof NewGameMessage) {
            handleNewGameMessage((NewGameMessage) data);
        } else if (data instanceof GameOverMessage) {
            handleGameOverMessage((GameOverMessage) data);
        } else if (data instanceof GameMap) {
            handleMapChange((GameMap) data);
        }
    }

    private void handleNewGameMessage(NewGameMessage message) {
        System.out.println("NEW GAME STARTS IN " + message.getTimeUntileGameBegins() + "ms");
    }

    private void handleMapChange(GameMap gameMap) {
        System.out.println("MAP CHANGED TO " + gameMap.getName());
        GameMap map = gameMap;
        gamePanel.setBackground(map.getBackground());
        gamePanel.setGrid(map.getGrid());
    }

    private void handleGameOverMessage(GameOverMessage message) {
        System.out.println("GAME OVER MESSAGE");
        System.out.println(message.getScores());
    }

    private void handleConnectionMessage(ConnectionMessage message) {
        if (message.success) {
            gamePanel.setServerTickRate(message.tickRate);
            player = message.player;
            handleMapChange(message.currentMap);
            gamePanel.start(framesPerSecond);
            MusicManager.changeTrack();
            MusicManager.getInstance().gameTrack1();
            System.out.println("CLIENT: Connected to server successfully");
        } else {
            gamePanel.stop();
            System.out.println("CLIENT: Failed to connect to server");
        }
    }

    private void handleGameServerUpdate(GameServerUpdate message) {
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
    }

    private void handlePlayerPickupMessage(PlayerPickupMessage message) {
        if (message.getEvent() == PlayerPickupMessage.Event.PickupUsed) {
            try {
                String pickupClassName = message.getPickup().getClass().getSimpleName();
                System.out.println(message.getPlayer().getName() + " used " + pickupClassName + " it will be active for " + message.getPickup().getActiveTime() + "ms");
                Method method = SoundFx.class.getMethod(pickupClassName);
                method.invoke(SoundFx.getInstance());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (message.getEvent() == PlayerPickupMessage.Event.PickupTaken) {
            String pickupClassName = message.getPickup().getClass().getSimpleName();
            System.out.println(message.getPlayer().getName() + " picked up " + pickupClassName);
        }
    }

    private void handlePlayerMessage(PlayerMessage message) {
        String playerName = message.getPlayer().getName();
        switch (message.getEvent()) {
            case Connected:
                System.out.println(playerName + " has connected.");
                break;
            case Disconnected:
                System.out.println(playerName + " has disconnected.");
                break;
            case Ready:
                System.out.println(playerName + " is ready.");
                break;
            case Unready:
                System.out.println(playerName + " is not ready.");
                break;
            case Crashed:
                System.out.println(playerName + " has crashed.");
                SoundFx.getInstance().crash();
                break;
            case Moved:
                SoundFx.getInstance().movement();
                break;
            case ColorChange:
                System.out.println(playerName + " has changed color to " + message.getPlayerColor());
                break;
        }
    }
}
