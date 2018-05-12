package gameserver;

import common.*;
import common.messages.*;
import gameobjects.GameObject;
import gameobjects.Player;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Dante Håkansson
 * @author Johannes Blüml
 */
public class PlayerManager implements MessageListener {
    private final HashSet<Player> players = new HashSet<>();
    private final HashSet<Player> playersToRemove = new HashSet<>();
    private final StartingPositions startingPositions = new StartingPositions();
    private final GameColors colors = new GameColors();
    private final Collection<GameObject> gameObjects;
    private GameMap currentMap;
    private GameState state;
    private int currentPlayerSpeed;
    private MessageListener messageListener;

    public PlayerManager(Collection<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }

    public Player login(String username) {
        if (players.size() > currentMap.getPlayers()) return null;

        Player player = new Player(username, gameObjects, currentMap);
        player.setListener(this);
        player.setId(ID.getNext());
        player.setSpeed(currentPlayerSpeed);

        if (state == GameState.Warmup) {
            player.reset();
            player.setColor(colors.takeColor());
            player.setPoint(Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects));
            gameObjects.add(player);
            gameObjects.add(player.getTrail());
        }

        players.add(player);
        newMessage(new PlayerMessage(PlayerMessage.Event.Connected, player));

        return player;
    }

    public void controlPlayer(Player player, Object value) {

        if (value instanceof Direction) {

            if (state == GameState.Running || state == GameState.Warmup) {
                Direction direction = (Direction) value;
                player.setNextDirection(direction);
            }

        } else if (value instanceof Action) {

            if (value == Action.UsePickup) {

                player.usePickup();

            } else if (state == GameState.Warmup) {

                if (value == Action.TogglePlayerColor) {

                    player.setColor(colors.exchangeColor(player.getColor()));
                    newMessage(new PlayerMessage(PlayerMessage.Event.ColorChange, player));
                    updateReadyPlayers();

                } else if (value == Action.ToggleReady) {

                    boolean ready = !player.isReady();
                    player.setReady(ready);
                    newMessage(new PlayerMessage(
                            (ready) ? PlayerMessage.Event.Ready : PlayerMessage.Event.Unready, player));
                    updateReadyPlayers();
                }
            }

        }
    }

    public void resetGame() {
        gameObjects.clear();

        if (currentMap.getStartingPositions() != null) {
            startingPositions.set(currentMap.getStartingPositions());
        } else {
            startingPositions.generate(currentMap.getGrid(), currentMap.getPlayers());
        }

        players.forEach((player) -> {
            if (state != GameState.Warmup) {
                player.setReady(false);
            }
            player.reset();
            player.setSpeed(currentPlayerSpeed);
            player.setPoint(Utility.convertFromGrid(startingPositions.getNext()));
            gameObjects.add(player);
            gameObjects.add(player.getTrail());
        });
    }

    public void updateReadyPlayers() {
        if (state == GameState.Warmup) {
            ReadyPlayersMessage message = new ReadyPlayersMessage(players);
            newMessage(message);
        }
    }

    public void respawnDeadPlayers() {
        for (Player player : players) {
            if (player.isDead()) {
                player.reset();
                player.setSpeed(currentPlayerSpeed);
                player.setPoint(Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects));
            }
        }
    }

    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
    }

    public void setState(GameState state) {
        this.state = state;

        if (state == GameState.Warmup) {
            players.removeAll(playersToRemove);
            playersToRemove.clear();
        }
    }

    public void setCurrentPlayerSpeed(int currentPlayerSpeed) {
        this.currentPlayerSpeed = currentPlayerSpeed;
    }

    public void addListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * @param message Message to be sent to all connected clients
     */
    public void newMessage(Message message) {
        messageListener.newMessage(message);
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public void removePlayer(Player player) {
        if (state == GameState.Warmup) {
            colors.giveBackColor(player.getColor());
            players.remove(player);
            gameObjects.remove(player.getTrail());
            gameObjects.remove(player);
            updateReadyPlayers();
        } else {
            playersToRemove.add(player);
        }
        newMessage(new PlayerMessage(PlayerMessage.Event.Disconnected, player));
    }
}
