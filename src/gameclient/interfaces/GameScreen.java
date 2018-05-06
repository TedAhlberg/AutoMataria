package gameclient.interfaces;

import common.Action;
import common.*;
import common.messages.*;
import gameclient.*;
import gameclient.sound.MusicManager;
import gameclient.sound.SoundFx;
import gameobjects.Player;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * GameScreen is a JPanel that connects and displays the Game.
 * It has a GamePanel with the actual game.
 * But also panels on the sides to display information about the game.
 */
public class GameScreen extends JPanel implements GameServerListener {
    private final int framesPerSecond = 60;
    private GameInfoPanel gameInfoPanel;
    private GamePanel gamePanel;
    private GameServerConnection client;
    private Player player;
    private UserInterface userInterface;
    private String username;

    public GameScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        setLayout(new GridBagLayout());

        gameInfoPanel = new GameInfoPanel(10);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.ipadx = 20;
        gbc.ipady = 20;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(gameInfoPanel, gbc);

        gamePanel = new GamePanel();
        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 10;
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

        AMButton backButton = new AMButton("DISCONNECT");
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

    public void connect(String ip, int port, String username) {
        this.username = username;
        client = new GameServerConnection(this);
        client.connect(ip, port);
        gamePanel.requestFocus();
    }

    public void onKeyPress(Action action) {
        if (action == Action.InterfaceBack) {
            if (client != null) {
                client.disconnect();
            }
            userInterface.changeToPreviousScreen();
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
        client.send(username);
        gameInfoPanel.add("# Trying to connect with username: " + username);
    }

    /**
     * When we are disconnected from the GameServer this method is called
     */
    public void onDisconnect() {
        gamePanel.stop();
        gameInfoPanel.add("# Connection closed", Color.RED);
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
        } else if (data instanceof ReadyPlayersMessage) {
            handleReadyPlayersMessage((ReadyPlayersMessage) data);
        }
    }

    private void handleReadyPlayersMessage(ReadyPlayersMessage message) {
        gameInfoPanel.add(message.getReadyPlayerCount() + "/" + message.getPlayerCount() + " ready players");
    }

    private void handleNewGameMessage(NewGameMessage message) {
        gameInfoPanel.add(":: Game starts in " + message.getTimeUntileGameBegins() / 1000.0 + " seconds");
    }

    private void handleMapChange(GameMap map) {
        gameInfoPanel.add(":: Map changed to: " + map.getName());
        gamePanel.setBackground(map.getBackground());
        gamePanel.setGrid(map.getGrid());
    }

    private void handleGameOverMessage(GameOverMessage message) {
        gameInfoPanel.add(":: Game Over");
        message.getRoundScores().forEach((player, score) -> {
            gameInfoPanel.add("Round score: " + player.getName() + " :: " + score, player.getColor());
        });
        message.getAccumulatedScores().forEach((player, score) -> {
            gameInfoPanel.add("Total score: " + player.getName() + " :: " + score, player.getColor());
        });
    }

    private void handleConnectionMessage(ConnectionMessage message) {
        if (message.success) {
            gamePanel.setServerTickRate(message.tickRate);
            player = message.player;
            handleMapChange(message.currentMap);
            gamePanel.start(framesPerSecond);
            MusicManager.changeTrack();
            MusicManager.getInstance().gameTrack1();
            gameInfoPanel.add("# Connected to server successfully", Color.GREEN);
        } else {
            gamePanel.stop();
            gameInfoPanel.add("# Failed to connect to server", Color.RED);
        }
    }

    private void handleGameServerUpdate(GameServerUpdate message) {
        gamePanel.updateGameObjects(message.gameObjects);
        gamePanel.setGameState(message.state);
        player = message.player;
    }

    private void handlePlayerPickupMessage(PlayerPickupMessage message) {
        if (message.getEvent() == PlayerPickupMessage.Event.PickupUsed) {
            try {
                String pickupClassName = message.getPickup().getClass().getSimpleName();
                gameInfoPanel.add(message.getPlayer().getName() + " used " + pickupClassName + " it will be active for " + message.getPickup().getActiveTime() / 1000.0 + "s", message.getPlayer().getColor());
                Method method = SoundFx.class.getMethod(pickupClassName);
                method.invoke(SoundFx.getInstance());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (message.getEvent() == PlayerPickupMessage.Event.PickupTaken) {
            String pickupClassName = message.getPickup().getClass().getSimpleName();
            gameInfoPanel.add(message.getPlayer().getName() + " picked up " + pickupClassName, message.getPlayer().getColor());
        }
    }

    private void handlePlayerMessage(PlayerMessage message) {
        String playerName = message.getPlayer().getName();
        Color playerColor = message.getPlayer().getColor();
        switch (message.getEvent()) {
            case Connected:
                gameInfoPanel.add(playerName + " has connected", playerColor);
                break;
            case Disconnected:
                gameInfoPanel.add(playerName + " has disconnected", playerColor);
                break;
            case Ready:
                gameInfoPanel.add(playerName + " is ready", playerColor);
                break;
            case Unready:
                gameInfoPanel.add(playerName + " is not ready", playerColor);
                break;
            case Crashed:
                gameInfoPanel.add(playerName + " has crashed", playerColor);
                SoundFx.getInstance().crash();
                break;
            case Moved:
                SoundFx.getInstance().movement();
                break;
            case ColorChange:
                gameInfoPanel.add(playerName + " has changed color", playerColor);
                break;
        }
    }
}
