package gameclient.interfaces.gamescreen;

import common.Action;
import common.GameMap;
import common.messages.*;
import gameclient.interfaces.*;
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
public class GameScreen extends JPanel implements GameServerListener, UserInterfaceScreen {
    private final int framesPerSecond = 60;
    private final JPanel leftPanel, rightPanel;
    private GameInfoPanel gameInfoPanel;
    private GamePanel gamePanel;
    private ScorePanel scorePanel;
    private ReadyPlayersPanel readyPlayersPanel;
    private GameServerConnection client;
    private Player player;
    private UserInterface userInterface;
    private boolean connected;
    private JTextField chatTextField;

    public GameScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        leftPanel = createLeftPanel();
        add(leftPanel, gbc);

        gamePanel = new GamePanel();
        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(gamePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        rightPanel = createRightPanel();
        add(rightPanel, gbc);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gamePanel.setPreferredSize(new Dimension(getHeight(), getHeight()));
        Dimension sidePanelSize = new Dimension((getWidth() - gamePanel.getHeight()) / 2, gamePanel.getHeight());
        leftPanel.setPreferredSize(sidePanelSize);
        rightPanel.setPreferredSize(sidePanelSize);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        gameInfoPanel = new GameInfoPanel(20);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(gameInfoPanel, gbc);

        chatTextField = new JTextField();
        chatTextField.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20, 20, 20, 20);
        panel.add(chatTextField, gbc);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        AMButton settingsButton = new AMButton("SETTINGS");
        settingsButton.addActionListener(e -> userInterface.changeScreen("SettingsScreen"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.ipadx = 10;
        gbc.ipady = 10;
        gbc.insets = new Insets(20, 20, 20, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(settingsButton, gbc);

        AMButton disconnectButton = new AMButton("DISCONNECT");
        disconnectButton.addActionListener(e -> client.disconnect());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.ipadx = 10;
        gbc.ipady = 10;
        gbc.insets = new Insets(20, 10, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(disconnectButton, gbc);

        readyPlayersPanel = new ReadyPlayersPanel();
        gbc = new GridBagConstraints();
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 20, 20, 20);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(readyPlayersPanel, gbc);

        scorePanel = new ScorePanel();
        panel.add(scorePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    public void connect(String ip, int port) {
        if (client != null && client.isConnected()) return;
        client = new GameServerConnection(this);
        client.connect(ip, port);
        gamePanel.requestFocus();
    }

    public void onKeyPress(Action action) {
        if (chatTextField.isVisible()) {
            switch (action) {
                case InterfaceBack:
                    closeChat();
                    break;
                case SendChatMessage:
                    sendChatMessage();
                    break;
            }
            return;
        }
        switch (action) {
            case ToggleFPS:
                gamePanel.toggleFPS();
                break;
            case ToggleChat:
                gameInfoPanel.setVisible(!gameInfoPanel.isVisible());
                break;
            case ToggleInterpolation:
                gamePanel.toggleInterpolation();
                break;
            case OpenChatPrompt:
                openChat();
                break;
            case InterfaceBack:
                userInterface.changeToPreviousScreen();
                break;
            case ToggleNames:
            case SendChatMessage:
                break;
            default:
                client.send(action);
        }
    }

    /**
     * When we are connected to a GameServer this method will be called
     * So we send the username to login to the GameServer
     */
    public void onConnect() {
        String username = userInterface.getSettingsScreen().getUsername();
        client.send(username);
        gameInfoPanel.add("# Trying to connect with username: " + username);
        connected = true;
    }

    /**
     * When we are disconnected from the GameServer this method is called
     */
    public void onDisconnect() {
        connected = false;

        gamePanel.stop();
        gameInfoPanel.clear();
        scorePanel.removeAll();
        readyPlayersPanel.removeAll();

        MusicManager.changeTrack();
        MusicManager.getInstance().menuTrack();

        userInterface.changeToPreviousScreen();
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
        } else if (data instanceof RoundOverMessage) {
            handleRoundOverMessage((RoundOverMessage) data);
        } else if (data instanceof GameOverMessage) {
            handleGameOverMessage((GameOverMessage) data);
        } else if (data instanceof ScoreUpdateMessage) {
            handleScoreUpdateMessage((ScoreUpdateMessage) data);
        } else if (data instanceof GameMap) {
            handleMapChange((GameMap) data);
        } else if (data instanceof ReadyPlayersMessage) {
            handleReadyPlayersMessage((ReadyPlayersMessage) data);
        } else if (data instanceof ChatMessage) {
            handleChatMessage((ChatMessage) data);
        }
    }

    private void openChat() {
        if (chatTextField.isVisible()) return;
        chatTextField.setVisible(true);
        leftPanel.revalidate();
        chatTextField.requestFocus();
    }

    private void closeChat() {
        if (!chatTextField.isVisible()) return;
        chatTextField.setVisible(false);
        chatTextField.setText("");
        leftPanel.revalidate();
    }

    private void sendChatMessage() {
        if (!chatTextField.isVisible()) return;
        String message = chatTextField.getText().trim();
        if (!message.equals("")) {
            client.send(new ChatMessage(message, player));
        }
        chatTextField.setText("");
        chatTextField.setVisible(false);
    }

    private void handleChatMessage(ChatMessage message) {
        gameInfoPanel.add(message.player.getName() + ": " + message.message, message.player.getColor());
    }

    private void handleScoreUpdateMessage(ScoreUpdateMessage message) {
        readyPlayersPanel.setVisible(false);
        scorePanel.setVisible(true);
        scorePanel.update(message.getScores(), message.getHighestScore(), message.getPlayedRounds(), message.isGameOver());
    }

    private void handleReadyPlayersMessage(ReadyPlayersMessage message) {
        scorePanel.setVisible(false);
        readyPlayersPanel.setVisible(true);
        readyPlayersPanel.update(message.getPlayers(), message.getReadyPlayerCount(), message.getPlayerCount());
    }

    private void handleNewGameMessage(NewGameMessage message) {
        gameInfoPanel.add(":: Game starts in " + message.getTimeUntileGameBegins() / 1000.0 + " seconds");
    }

    private void handleMapChange(GameMap map) {
        gameInfoPanel.add(":: Map changed to: " + map.getName());
        gamePanel.setBackground(map.getBackground());
        gamePanel.setGrid(map.getGrid());
        scorePanel.setMapName(map.getName());
        readyPlayersPanel.setMapName(map.getName());
    }

    private void handleRoundOverMessage(RoundOverMessage message) {
        gameInfoPanel.add(":: Next round starts in " + message.getTimeUntilNextGame() / 1000.0 + " seconds");
    }

    private void handleGameOverMessage(GameOverMessage message) {
        gameInfoPanel.add(":: Next map starts in " + message.getTimeUntilNextGame() / 1000.0 + " seconds");
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
            readyPlayersPanel.setLimits(message.roundLimit, message.scoreLimit);
            readyPlayersPanel.setServerName(message.serverName);
            scorePanel.setLimits(message.roundLimit, message.scoreLimit);
            scorePanel.setServerName(message.serverName);
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
                gameInfoPanel.add(playerName + " has connected to the server", playerColor);
                break;
            case Disconnected:
                gameInfoPanel.add(playerName + " has disconnected from the server", playerColor);
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
                if (message.getPlayer().equals(player))
                    SoundFx.getInstance().movement();
                break;
            case ColorChange:
                gameInfoPanel.add(playerName + " has changed color", playerColor);
                break;
        }
    }

    public boolean isConnectedToServer() {
        return connected;
    }

    public void onScreenActive() {
    }

    public void onScreenInactive() {

    }
}
