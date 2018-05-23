package gameserver;

import common.*;
import common.messages.*;
import gameobjects.GameObject;
import gameobjects.Player;

import java.util.*;

/**
 * @author Dante Håkansson
 * @author Johannes Blüml
 */
public class PlayerManager implements MessageListener {
    private final HashSet<Player> players = new HashSet<>();
    private final HashSet<Player> playersToAddAfterMatch = new HashSet<>();
    private final HashSet<Player> playersToRemoveAfterMatch = new HashSet<>();
    private final StartingPositions startingPositions = new StartingPositions();
    private final GameColors colors = new GameColors();
    private final Collection<GameObject> gameObjects;
    private final Random random = new Random();
    private GameMap currentMap;
    private GameState state;
    private int currentPlayerSpeed;
    private MessageListener messageListener;

    public PlayerManager(Collection<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }

    public Player login(String username) {
        if (players.size() > currentMap.getPlayers()) return null;

        if (isUsernameUsed(username)) {
            username = getUniqueUsername(username);
        }

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
            players.add(player);
        } else {
            playersToAddAfterMatch.add(player);
        }

        newMessage(new PlayerMessage(PlayerMessage.Event.Connected, player));

        return player;
    }

    public void controlPlayer(Player player, Action value) {
        if (state == GameState.Countdown || state == GameState.GameOver) return;

        switch (value) {
            case GoUp:
                player.setNextDirection(Direction.Up);
                break;

            case GoDown:
                player.setNextDirection(Direction.Down);
                break;

            case GoLeft:
                player.setNextDirection(Direction.Left);
                break;

            case GoRight:
                player.setNextDirection(Direction.Right);
                break;

            case UsePickup:
                player.usePickup();
                break;

            case ToggleReady:
                if (state != GameState.Warmup) break;
                boolean ready = !player.isReady();
                player.setReady(ready);
                newMessage(new PlayerMessage((ready) ? PlayerMessage.Event.Ready : PlayerMessage.Event.Unready, player));
                updateReadyPlayers();
                break;

            case TogglePlayerColor:
                if (state != GameState.Warmup) break;
                player.setColor(colors.exchangeColor(player.getColor()));
                newMessage(new PlayerMessage(PlayerMessage.Event.ColorChange, player));
                updateReadyPlayers();
                break;
        }
    }

    public void resetGame() {
        gameObjects.clear();

        if (currentMap.getStartingPositions().length >= currentMap.getPlayers()) {
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

    public void setState(GameState state) {
        this.state = state;
        switch (state) {
            case Warmup:
                players.removeAll(playersToRemoveAfterMatch);
                playersToRemoveAfterMatch.clear();
                players.addAll(playersToAddAfterMatch);
                playersToAddAfterMatch.clear();
                resetGame();
                updateReadyPlayers();
                break;
            case Countdown:
                resetGame();
            case RoundOver:
            case GameOver:
                players.forEach(player -> player.setNextDirection(Direction.Static));
                break;
        }
    }

    public void removePlayer(Player player) {
        if (state == GameState.Warmup) {
            colors.giveBackColor(player.getColor());
            players.remove(player);
            gameObjects.remove(player.getTrail());
            gameObjects.remove(player);
            updateReadyPlayers();
        } else {
            playersToRemoveAfterMatch.add(player);
        }
        newMessage(new PlayerMessage(PlayerMessage.Event.Disconnected, player));
    }

    private boolean isUsernameUsed(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) return true;
        }
        return false;
    }

    private String getUniqueUsername(String username) {
        int index = 2;
        while (isUsernameUsed(username + " [" + index + "]")) {
            index += 1;
        }
        return username + " [" + index + "]";
    }

    public void moveStaticPlayers() {
        Direction[] directions = {Direction.Down, Direction.Right, Direction.Left, Direction.Up};
        players.forEach(player -> {
            if (player.getDirection() == Direction.Static) {
                player.setNextDirection(directions[random.nextInt(directions.length)]);
            }
        });
    }

    public void addListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setCurrentPlayerSpeed(int currentPlayerSpeed) {
        this.currentPlayerSpeed = currentPlayerSpeed;
    }

    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    /**
     * @param message Message to be sent to all connected clients
     */
    public void newMessage(Message message) {
        messageListener.newMessage(message);
    }
}
