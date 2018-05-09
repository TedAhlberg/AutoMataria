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

    private final ServerConnection serverConnection;
    private final GameObjectSpawner gameObjectSpawner;
    private final ServerInformationSender serverInformationSender;
    private final GameScore gameScore;
    private final GameServerSettings settings;
    private int currentPlayerSpeed, currentCountdown, currentMapPoolIndex;
    private boolean running;
    private GameState state;
    private GameMap currentMap;

    /**
     * A Controller that connects together the serverConnection part of Auto-Mataria.
     * The game loop, Clients that connect. Everything that happens in the actual game.
     *
     * @param settings Object containing all settings for this server
     */
    public GameServer(GameServerSettings settings) {
        this.settings = settings;

        serverConnection = new ServerConnection(settings.port);
        serverConnection.addListener(this);
        serverInformationSender = new ServerInformationSender(this);
        gameObjectSpawner = new GameObjectSpawner(gameObjects, currentMap, settings.tickRate);
        gameScore = new GameScore(this);
        changeMap(Maps.getInstance().get(settings.mapPool[0]));
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
        if (map == null) return;

        currentMap = map;
        currentPlayerSpeed = (int) Math.round(settings.playerSpeed * map.getPlayerSpeedMultiplier());
        gameScore.startGame(players, settings.roundLimit, settings.scoreLimit);
        gameObjectSpawner.changeMap(map);

        connectedClients.forEach((client, player) -> {
            client.send(map);
            player.setCurrentMap(map);
        });
    }

    /**
     * Changes to the next Map in the Map pool
     */
    public void changeToNextMap() {
        currentMapPoolIndex = (currentMapPoolIndex + 1) % settings.mapPool.length;
        String nextMapName = settings.mapPool[currentMapPoolIndex];
        GameMap nextMap = Maps.getInstance().get(nextMapName);
        changeMap(nextMap);
    }

    /**
     * Main game loop that handles when it is time to tick all game objects or update game state to clients.
     */
    private void gameLoop() {
        int ticksSinceLastUpdate = 0;
        long tickRate = settings.tickRate * 1000000;
        long previousTickTime = System.nanoTime();
        while (running) {
            long nowTime = System.nanoTime();
            long timeSinceLastTick = nowTime - previousTickTime;

            if (timeSinceLastTick > tickRate) {
                previousTickTime = System.nanoTime() - (timeSinceLastTick - tickRate);
                tick();
                ticksSinceLastUpdate += 1;
                if (ticksSinceLastUpdate >= settings.amountOfTickBetweenUpdates) {
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
                if (gameScore.isGameOver()) {
                    setState(GameState.GameOver);
                } else if (gameScore.isRoundComplete()) {
                    setState(GameState.RoundOver);
                }
                break;
            case Warmup:
                respawnDeadPlayers();
                break;
            case RoundOver:
                if (currentCountdown <= 0) setState(GameState.Countdown);
                else currentCountdown -= settings.tickRate;
                break;
            case GameOver:
                if (currentCountdown <= 0) {
                    changeToNextMap();
                    setState(GameState.Warmup);
                } else currentCountdown -= settings.tickRate;
                break;
            case Countdown:
                if (currentCountdown <= 0) setState(GameState.Running);
                else currentCountdown -= settings.tickRate;
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
            player.setSpeed(currentPlayerSpeed);
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
        player.setSpeed(currentPlayerSpeed);
        if (state == GameState.Warmup) {
            player.reset();
            player.setSpeed(currentPlayerSpeed);
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
                player.setSpeed(currentPlayerSpeed);
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
            case RoundOver:
                for (Player player : players) {
                    player.setNextDirection(Direction.Static);
                }
                currentCountdown = settings.roundOverCountdown < 1000 ? 1000 : settings.roundOverCountdown;
                break;
            case GameOver:
                for (Player player : players) {
                    player.setNextDirection(Direction.Static);
                }
                currentCountdown = settings.gameOverCountdown < 1000 ? 1000 : settings.gameOverCountdown;
                break;
            case Countdown:
                resetGame();
                players.clear();
                players.addAll(connectedClients.values());
                currentCountdown = settings.newGameCountdown < 1000 ? 1000 : settings.newGameCountdown;
                newMessage(new NewGameMessage(currentCountdown));
                break;
        }
        this.state = newState;
    }

    public void onServerConnectionStarted() {
        System.out.println("Server Connection started");
    }

    public void onServerConnectionStopped() {
        System.out.println("Server Connection ended");
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
                client.send(new ConnectionMessage(currentMap, settings.tickRate, settings.tickRate * settings.amountOfTickBetweenUpdates, player));
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
        if (playersCount < 2) return;
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
     * @return Returns the state of the current GameServer as a ServerInformation object
     */
    public ServerInformation getServerInformation() {
        return new ServerInformation(null, settings.name, currentMap.getName(), state.toString(), settings.port, connectedClients.size(), currentMap.getPlayers());
    }

    /**
     * @param message Message to be sent to all connected clients
     */
    public void newMessage(Message message) {
        connectedClients.forEachKey(1, client -> client.send(message));
    }
}
