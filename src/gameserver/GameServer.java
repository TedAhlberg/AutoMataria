package gameserver;

import common.*;
import gameclient.Game;
import gameobjects.*;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Johannes Blüml
 */
public class GameServer implements ClientListener, MessageListener{
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
    private int playerSpeed;
    private GameState state = GameState.Warmup;
    private GameMap currentMap;
    private ServerConnection server;
    private boolean running = true;
    private int currentCountdown;
    private LinkedList<Player> players = new LinkedList<>();
    private GameObjectSpawner gameObjectSpawner;
    private ServerInformationSender serverSender = new ServerInformationSender(this);

    public GameServer(String serverName, int serverPort, int tickRate, int amountOfTickBetweenUpdates, int playerSpeed, GameMap map) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.tickRate = tickRate;
        this.amountOfTickBetweenUpdates = amountOfTickBetweenUpdates;
        this.playerSpeed = (int) Math.round(playerSpeed * map.getPlayerSpeedMultiplier());
        this.gameStartCountdown = 5000;
        this.gameOverCountDown = 10000;
        this.currentMap = map;

        server = new ServerConnection(serverPort);
        new Thread(server).start();
        server.addListener(this);

        gameObjectSpawner = new GameObjectSpawner(gameObjects, map, tickRate);
        startingPositions = new StartingPositions();
        startingPositions.generate(map.getGrid(), map.getPlayers());

        new Thread(() -> gameLoop()).start();
        serverSender.start();
    }

    public void stop() {
        running = false;
        server.stop();
    }

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

            // WAIT BEFORE CONTINUING WITH THE GAMELOOP
            while (nowTime - previousTickTime < tickRate) {
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {}

                nowTime = System.nanoTime();
            }
        }
    }

    private void update() {
        GameServerUpdate update = new GameServerUpdate(state, gameObjects);

        for (Client client : connectedClients.keySet()) {
            update.player = connectedClients.get(client);
            client.send(update);
        }
    }

    private void tick() {
        for (GameObject gameObject : gameObjects) {
            gameObject.tick();
        }

        if (state == GameState.Running) {
            int alivePlayers = 0;
            for (Player player : players) {
                if (!player.isDead()) {
                    alivePlayers += 1;
                }
            }
            if (alivePlayers <= 1) {
                System.out.println("SERVER STATE: Running -> Game Over");
                state = GameState.GameOver;
                currentCountdown = gameOverCountDown;
            }
        } else if(state == GameState.Warmup) {
            respawnDeadPlayers();
            
            
        } else if (state == GameState.Warmup && Utility.getReadyPlayerPercentage(connectedClients.values()) >= 1.0 && connectedClients.size() > 1) {
                System.out.println("SERVER STATE: Warmup -> Countdown");
                players.clear();
                players.addAll(connectedClients.values());
                state = GameState.Countdown;
                currentCountdown = gameStartCountdown;
        } else if (state == GameState.Countdown) {
            if (currentCountdown <= 0) {
                System.out.println("SERVER STATE: Countdown -> Running");
                state = GameState.Running;
                startNewGame();
            } else {
                currentCountdown -= tickRate;
            }
        } else if (state == GameState.GameOver) {
            if (currentCountdown <= 0) {
                System.out.println("SERVER STATE: Game Over -> Warmup");
                state = GameState.Warmup;
                startNewWarmup();
            } else {
                currentCountdown -= tickRate;
            }
        }
        gameObjectSpawner.tick();
    }

    private void startNewWarmup() {
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

    public void respawnDeadPlayers() {
        Rectangle mapRectangle = new Rectangle(Utility.convertFromGrid(currentMap.getGrid()));
        startingPositions.reset();
        for (Player player : connectedClients.values()) {

            if (player.isDead()) {
                player.getTrail().remove(mapRectangle);
                player.setDead(false);
                player.setReady(false);
                player.setPickUp(null);
                player.setSpeed(playerSpeed);
                player.setNextDirection(Direction.Static);
                player.setPoint(Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects));
            
            }
        }
    }
    
    

    @Override
    public void onConnect(Client client) {
        System.out.println("SERVER: Client connected.");
    }

    @Override
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

    @Override
    public void onClose(Client client) {
        Player player = connectedClients.remove(client);
        if (state == GameState.Running) {
            player.setDead(true);
        } else {
            gameObjects.remove(player.getTrail());
            gameObjects.remove(player);
        }
        System.out.println("Player disconnected: " + player);
    }

    /**
     * Creates a byte array that conains info about this server that is used
     * in clients to view active servers on the local network
     *
     * @return byte array that conains info about this server - maximum length is 76 bytes
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


    public void newMessage(Message message) {
        connectedClients.forEach( (client, player) -> { client.send(message); });
        
    }
}
