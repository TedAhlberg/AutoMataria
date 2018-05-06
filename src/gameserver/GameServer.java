package gameserver;

import common.*;
import common.messages.*;
import gameobjects.GameObject;
import gameobjects.Player;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A Controller that connects together the serverConnection part of Auto-Mataria.
 * The game loop, Clients that connect. Everything that happens in the actual game.
 *
 * @author Johannes Blüml
 */
public class GameServer implements ConnectionListener, MessageListener {
    private final ConcurrentLinkedQueue<GameObject> gameObjects = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Client, Player> connectedClients = new ConcurrentHashMap<>();
    private final LinkedList<Player> players = new LinkedList<>();
    private final StartingPositions startingPositions = new StartingPositions();
    private final GameColors colors = new GameColors();

    private final int tickRate, amountOfTickBetweenUpdates, serverPort, serverPlayerSpeed;
    private final ServerConnection serverConnection;
    private final GameObjectSpawner gameObjectSpawner;
    private final ServerInformationSender serverInformationSender;
    private final GameScore gameScore;
    private int gameStartCountdown, gameOverCountDown, playerSpeed, currentCountdown;
    private String serverName;
    private boolean running;
    private GameState state;
    private GameMap currentMap;

    /**
     * A Controller that connects together the serverConnection part of Auto-Mataria.
     * The game loop, Clients that connect. Everything that happens in the actual game.
     *
     * @param serverName                 Name of the server
     * @param serverPort                 Port that the server will listen to connections on
     * @param tickRate                   How often to run the tick method on all game objects (In milliseconds)
     * @param amountOfTickBetweenUpdates How often to update the game to all clients
     * @param playerSpeed                How far the players travels each tick
     * @param map                        The GameMap that the server will start with
     */
    public GameServer(String serverName, int serverPort, int tickRate, int amountOfTickBetweenUpdates, int playerSpeed, GameMap map) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.tickRate = tickRate;
        this.amountOfTickBetweenUpdates = amountOfTickBetweenUpdates;
        this.serverPlayerSpeed = this.playerSpeed = playerSpeed;
        this.currentMap = map;

        gameStartCountdown = 5000;
        gameOverCountDown = 10000;

        serverConnection = new ServerConnection(serverPort);
        serverConnection.addListener(this);
        serverInformationSender = new ServerInformationSender(this);
        gameObjectSpawner = new GameObjectSpawner(gameObjects, map, tickRate);
        gameScore = new GameScore(players);
        changeMap(map);
        setState(GameState.Warmup);
    }

    public void start() {
        if (running) return;
        running = true;
        new Thread(serverConnection).start();
        new Thread(serverInformationSender).start();
        new Thread(this::gameLoop).start();
    }

    public void stop() {
        if (!running) return;
        running = false;
        serverConnection.stop();
        serverInformationSender.stop();
    }

    /**
     * Changes the active map on the server.
     *
     * @param map The GameMap to change to
     */
    public void changeMap(GameMap map) {
        if (map == null || map.equals(currentMap) || state != GameState.Warmup) return;

        currentMap = map;
        playerSpeed = (int) Math.round(serverPlayerSpeed * map.getPlayerSpeedMultiplier());

        gameObjectSpawner.changeMap(map);

        connectedClients.forEach((client, player) -> {
            client.send(map);
            player.setCurrentMap(map);
        });

        setState(GameState.Warmup);
    }

    /**
     * Main game loop that handles when it is time to tick all game objects or update game state to clients.
     */
    private void gameLoop() {
        int ticksSinceLastUpdate = 0;
        long tickRate = this.tickRate * 1000000;
        long previousTickTime = System.nanoTime();
        while (running) {
            long nowTime = System.nanoTime();
            long timeSinceLastTick = nowTime - previousTickTime;

            if (timeSinceLastTick > tickRate) {
                previousTickTime = System.nanoTime() - (timeSinceLastTick - tickRate);
                tick();
                ticksSinceLastUpdate += 1;
                if (ticksSinceLastUpdate >= amountOfTickBetweenUpdates) {
                    update();
                    ticksSinceLastUpdate = 0;
                }
            }

            // Wait until tickRate has passed before continuing
            while (nowTime - previousTickTime < tickRate) {
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {}

                nowTime = System.nanoTime();
            }
        }
    }

    /**
     * Called from game loop on a regular interval.
     * Sends all game objects to all connected clients so they can update their view of the game.
     */
    private void update() {
        GameServerUpdate update = new GameServerUpdate(state, gameObjects);

        for (Client client : connectedClients.keySet()) {
            update.player = connectedClients.get(client);
            client.send(update);
        }
    }

    /**
     * Called from game loop on a regular interval. Runs all tick methods in all game objects.
     * Which for example moves players forward.
     * <p>
     * Also handles Game State changes.
     */
    private void tick() {
        for (GameObject gameObject : gameObjects) {
            gameObject.tick();
        }

        switch (state) {
            case Running:
                gameScore.calculateScores();
                int alivePlayers = (int) players.stream().filter(player -> !player.isDead()).count();
                if (alivePlayers <= 1) setState(GameState.GameOver);
                break;
            case Warmup:
                respawnDeadPlayers();
                break;
            case GameOver:
                if (currentCountdown <= 0) setState(GameState.Warmup);
                else currentCountdown -= tickRate;
                break;
            case Countdown:
                if (currentCountdown <= 0) setState(GameState.Running);
                else currentCountdown -= tickRate;
                break;
        }

        gameObjectSpawner.tick();
    }

    private void resetGame() {
        gameObjects.clear();

        if (currentMap.getStartingPositions() != null) {
            startingPositions.set(currentMap.getStartingPositions());
        } else {
            startingPositions.generate(currentMap.getGrid(), currentMap.getPlayers());
        }

        connectedClients.forEach((client, player) -> {
            if (state != GameState.Warmup) {
                player.setReady(false);
            }
            player.reset();
            player.setSpeed(playerSpeed);
            player.setPoint(Utility.convertFromGrid(startingPositions.getNext()));
            gameObjects.add(player);
            gameObjects.add(player.getTrail());
        });
    }

    private Player newPlayer(String name) {
        if (connectedClients.size() > currentMap.getPlayers()) return null;
        Player player = new Player(name, gameObjects, currentMap);
        player.setListener(this);
        player.setId(ID.getNext());
        player.setSpeed(playerSpeed);
        if (state == GameState.Warmup) {
            player.reset();
            player.setSpeed(playerSpeed);
            player.setPoint(Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects));
            gameObjects.add(player);
            gameObjects.add(player.getTrail());
        }
        return player;
    }

    private void respawnDeadPlayers() {
        for (Player player : connectedClients.values()) {
            if (player.isDead()) {
                player.reset();
                player.setSpeed(playerSpeed);
                player.setPoint(Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects));
            }
        }
    }

    private void setState(GameState newState) {
        if (this.state == newState) return;
        switch (newState) {
            case Warmup:
                resetGame();
                break;
            case Running:
                gameScore.startRound();
                break;
            case GameOver:
                for (Player player : players) {
                    player.setNextDirection(Direction.Static);
                }
                currentCountdown = gameOverCountDown;
                newMessage(new GameOverMessage(gameScore.getRoundScores(), gameScore.getAccumulatedScores(), gameOverCountDown));
                break;
            case Countdown:
                resetGame();
                players.clear();
                players.addAll(connectedClients.values());
                currentCountdown = gameStartCountdown;
                newMessage(new NewGameMessage(gameStartCountdown));
                break;
        }
        this.state = newState;
    }

    public void onServerConnectionStarted() {
        System.out.println("SERVER STARTED SUCCESSFULLY");
    }

    public void onServerConnectionStopped() {
        System.out.println("SERVER STOPPED");
        stop();
    }

    /**
     * Handles everything that is received from connected clients:
     * Login - Creates a new Player
     * Input - Controls the Player
     *
     * @param client
     * @param value
     */
    public void onDataFromClient(Client client, Object value) {
        if (value instanceof Direction && connectedClients.containsKey(client)) {
            if (state == GameState.Running || state == GameState.Warmup) {
                Direction direction = (Direction) value;
                Player player = connectedClients.get(client);
                player.setNextDirection(direction);
            }
        } else if (value instanceof Action) {
            Player player = connectedClients.get(client);
            if (value == Action.UsePickup) {
                player.usePickup();
            } else if (state == GameState.Warmup) {
                if (value == Action.TogglePlayerColor) {
                    player.setColor(colors.exchangeColor(player.getColor()));
                    newMessage(new PlayerMessage(PlayerMessage.Event.ColorChange, player));
                } else if (value == Action.ToggleReady) {
                    boolean ready = !player.isReady();
                    player.setReady(ready);
                    newMessage(new PlayerMessage((ready) ? PlayerMessage.Event.Ready : PlayerMessage.Event.Unready, player));
                    updateReadyPlayers();
                }
            }
        } else if (value instanceof String && !connectedClients.containsKey(client)) {
            Player player = newPlayer((String) value);
            if (player != null) {
                player.setColor(colors.takeColor());
                connectedClients.put(client, player);
                client.send(new ConnectionMessage(currentMap, tickRate, tickRate * amountOfTickBetweenUpdates, player));
                newMessage(new PlayerMessage(PlayerMessage.Event.Connected, player));
                updateReadyPlayers();
            } else {
                client.send(new ConnectionMessage());
                System.out.println(value + " tried to connect but no slots are available.");
            }
        }
    }

    private void updateReadyPlayers() {
        int playersCount = connectedClients.size();
        int readyPlayers = (int) connectedClients.values().stream().filter(Player::isReady).count();
        newMessage(new ReadyPlayersMessage(readyPlayers, playersCount));
        if (readyPlayers == playersCount) setState(GameState.Countdown);
    }

    /**
     * Called when a client has disconnected from the server
     *
     * @param client The client that has disconnected from the server
     */
    public void onClientDisconnect(Client client) {
        Player player = connectedClients.remove(client);
        colors.giveBackColor(player.getColor());
        if (state != GameState.Running) {
            gameObjects.remove(player.getTrail());
            gameObjects.remove(player);
            updateReadyPlayers();
        }
        newMessage(new PlayerMessage(PlayerMessage.Event.Disconnected, player));
    }

    /**
     * Creates a byte array that conains info about this serverConnection that is used
     * in clients to view active servers on the local network
     *
     * @return byte array that conains info about this serverConnection - maximum length is 76 bytes
     */
    public byte[] getServerAliveUpdateMessage() {
        String string = serverName + "\n"
                + currentMap.getName() + "\n"
                + state + "\n"
                + serverPort + "\n"
                + connectedClients.size() + "\n"
                + currentMap.getPlayers() + "\0";

        return string.getBytes();
    }

    /**
     * @param message Message to be sent to all connected clients
     */
    public void newMessage(Message message) {
        connectedClients.forEachKey(1, client -> client.send(message));
    }
}
