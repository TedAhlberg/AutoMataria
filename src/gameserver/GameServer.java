package gameserver;

import common.*;
import common.messages.*;
import gameobjects.*;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A Controller that connects together the serverConnection part of Auto-Mataria.
 * The game loop, Clients that connect. Everything that happens in the actual game.
 *
 * @author Johannes Blüml
 */
public class GameServer implements ClientListener, MessageListener {
    private final GameColors colors = new GameColors();
    private final StartingPositions startingPositions;
    private final ConcurrentHashMap<Client, Player> connectedClients = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<GameObject> gameObjects = new ConcurrentLinkedQueue<>();
    private final int tickRate;
    private final int amountOfTickBetweenUpdates;
    private final int gameStartCountdown;
    private final int gameOverCountDown;
    private final String serverName;
    private final int serverPort;
    private final int serverPlayerSpeed;
    private int playerSpeed;
    private GameState state;
    private GameMap currentMap;
    private ServerConnection serverConnection;
    private boolean running;
    private int currentCountdown;
    private LinkedList<Player> players = new LinkedList<>();
    private GameObjectSpawner gameObjectSpawner;
    private ServerInformationSender serverInformationSender;

    /**
     * A Controller that connects together the serverConnection part of Auto-Mataria.
     * The game loop, Clients that connect. Everything that happens in the actual game.
     *
     * @param serverName Name of the server
     * @param serverPort Port that the server will listen to connections on
     * @param tickRate How often to run the tick method on all game objects (In milliseconds)
     * @param amountOfTickBetweenUpdates How often to update the game to all clients
     * @param playerSpeed How far the players travels each tick
     * @param map The GameMap that the server will start with
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
        state = GameState.Warmup;

        serverConnection = new ServerConnection(serverPort);
        serverConnection.addListener(this);
        serverInformationSender = new ServerInformationSender(this);
        startingPositions = new StartingPositions();
        changeMap(map);
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
        playerSpeed = (int) Math.round(serverPlayerSpeed * map.getPlayerSpeedMultiplier());
        gameObjectSpawner = new GameObjectSpawner(gameObjects, map, tickRate);
        //TODO: Send new map to all clients so they can change the GamePanel gridSize
        startNewWarmup();
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
     *
     * Also handles Game State changes.
     */
    private void tick() {
        for (GameObject gameObject : gameObjects) {
            gameObject.tick();
        }

        switch (state) {
            case Running:
                int alivePlayers = 0;
                for (Player player : players) {
                    if (!player.isDead()) {
                        alivePlayers += 1;
                    }
                }
                if (alivePlayers <= 1) {
                    gameOver();
                }
                break;

            case Warmup:
                if (Utility.getReadyPlayerPercentage(connectedClients.values()) >= 1.0 && connectedClients.size() > 1) {
                    startWarmUp();
                } else {
                    respawnDeadPlayers();
                }
                break;

            case GameOver:
                if (currentCountdown <= 0) {
                    startNewWarmup();
                } else {
                    currentCountdown -= tickRate;
                }
                break;

            case Countdown:
                if (currentCountdown <= 0) {
                    startNewGame();
                } else {
                    currentCountdown -= tickRate;
                }
                break;
        }

        gameObjectSpawner.tick();
    }

    private void startWarmUp() {
        System.out.println("SERVER STATE: Warmup -> Countdown");
        players.clear();
        players.addAll(connectedClients.values());
        state = GameState.Countdown;
        currentCountdown = gameStartCountdown;

    }

    private void gameOver() {
        System.out.println("SERVER STATE: Running -> Game Over");
        state = GameState.GameOver;
        currentCountdown = gameOverCountDown;

    }

    private void startNewWarmup() {
        System.out.println("SERVER STATE: Game Over -> Warmup");
        state = GameState.Warmup;
        System.out.println("Starting Warmup");
        Rectangle mapRectangle = new Rectangle(Utility.convertFromGrid(currentMap.getGrid()));
        Iterator<GameObject> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
            if (gameObject instanceof Trail) {
                ((Trail) gameObject).remove(mapRectangle);
            } else {
                iterator.remove();
            }
        }
        if (currentMap.getStartingPositions() != null) {
            startingPositions.set(currentMap.getStartingPositions());
        } else {
            startingPositions.generate(currentMap.getGrid(), currentMap.getPlayers());
        }
        connectedClients.forEach((client, player) -> {
            player.setInvincible(true);
            player.setReady(false);
            player.setReversed(false);
            player.setDead(false);
            player.setPickUp(null);
            player.setSpeed(playerSpeed);
            player.setNextDirection(Direction.Static);
            player.setPoint(Utility.convertFromGrid(startingPositions.getNext()));
            gameObjects.add(player);
            System.out.println("Placing player " + player.getName() + " at " + player.getPoint());
        });
    }

    private void startNewGame() {
        System.out.println("SERVER STATE: Countdown -> Running");
        state = GameState.Running;
        System.out.println("Starting new game");
        if (currentMap.getStartingPositions() != null) {
            startingPositions.set(currentMap.getStartingPositions());
        } else {
            startingPositions.generate(currentMap.getGrid(), connectedClients.size());
        }

        Rectangle mapRectangle = new Rectangle(Utility.convertFromGrid(currentMap.getGrid()));
        Iterator<GameObject> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
            if (gameObject instanceof Player) {
                Player player = (Player) gameObject;
                player.setInvincible(false);
                player.setReversed(false);
                player.setReady(false);
                player.setDead(false);
                player.setPickUp(null);
                player.setSpeed(playerSpeed);
                player.setNextDirection(Direction.Static);
                player.setPoint(Utility.convertFromGrid(startingPositions.getNext()));
                System.out.println("Placing player " + player.getName() + " at " + player.getPoint());
            } else if (gameObject instanceof Trail) {
                ((Trail) gameObject).remove(mapRectangle);
            } else {
                iterator.remove();
            }
        }
    }

    private Player newPlayer(String name) {
        if (connectedClients.size() > currentMap.getPlayers()) return null;
        Player player = new Player(name, gameObjects, currentMap);
        player.setListener(this);
        player.setId(ID.getNext());
        player.setSpeed(playerSpeed);
        if (state == GameState.Warmup) {
            player.setInvincible(false);
            player.setReady(false);
            player.setReversed(false);
            player.setDead(false);
            player.setPickUp(null);
            player.setSpeed(playerSpeed);
            player.setNextDirection(Direction.Static);
            player.setPoint(Utility.convertFromGrid(startingPositions.getNext()));
            gameObjects.add(player);
            gameObjects.add(player.getTrail());
        }
        return player;
    }

    private void respawnDeadPlayers() {
        Rectangle mapRectangle = new Rectangle(Utility.convertFromGrid(currentMap.getGrid()));
        startingPositions.reset();
        for (Player player : connectedClients.values()) {

            if (player.isDead()) {
                player.getTrail().remove(mapRectangle);
                player.setDead(false);
                player.setInvincible(false);
                player.setReversed(false);
                player.setReady(false);
                player.setPickUp(null);
                player.setSpeed(playerSpeed);
                player.setNextDirection(Direction.Static);
                player.setPoint(Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects));
            }
        }
    }

    /**
     * Handles everything that is received from connected clients:
     * Login - Creates a new Player
     * Input - Controls the Player
     *
     * @param client
     * @param value
     */
    public void onData(Client client, Object value) {
        if (value instanceof Direction && connectedClients.containsKey(client)) {
            Direction direction = (Direction) value;
            Player player = connectedClients.get(client);
            player.setNextDirection(direction);
        } else if (value instanceof Action) {
            Player player = connectedClients.get(client);
            if (value == Action.UsePickup) {
                player.usePickUp();
            } else if (state == GameState.Warmup) {
                if (value == Action.TogglePlayerColor) {
                    player.setColor(colors.exchangeColor(player.getColor()));
                } else if (value == Action.ToggleReady) {
                    player.setReady(!player.isReady());
                }
            }
        } else if (value instanceof String && !connectedClients.containsKey(client)) {
            Player player = newPlayer((String) value);
            if (player != null) {
                player.setColor(colors.takeColor());
                connectedClients.put(client, player);
                client.send(new ConnectionMessage(currentMap, tickRate, tickRate * amountOfTickBetweenUpdates, player));
                System.out.println("Player connected: " + player);
            } else {
                client.send(new ConnectionMessage());
                System.out.println(value + " tried to connect but no slots are available.");
            }
        }
    }

    /**
     * Called when a client has disconnected from the server
     *
     * @param client The client that has disconnected from the server
     */
    public void onClose(Client client) {
        Player player = connectedClients.remove(client);
        colors.giveBackColor(player.getColor());
        if (state == GameState.Running) {
            player.setDead(true);
        } else {
            gameObjects.remove(player.getTrail());
            gameObjects.remove(player);
        }
        System.out.println("Player disconnected: " + player);
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
        connectedClients.forEach((client, player) -> client.send(message));
    }
}
