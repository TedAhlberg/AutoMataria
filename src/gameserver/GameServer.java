package gameserver;

import common.*;
import gameclient.Game;
import gameobjects.*;

import javax.swing.text.Position;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * @author Johannes Blüml
 */
public class GameServer implements ClientListener {
    private final GameColors colors = new GameColors();
    private final StartingPositions startingPositions = new StartingPositions();
    private final ConcurrentHashMap<Client, Player> connectedClients = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<GameObject> gameObjects = new ConcurrentLinkedQueue<>();
    private final int tickRate, updateRate, gameStartCountdown, gameOverCountDown;
    private final String serverName;
    private final int serverPort;
    private GameState state = GameState.Warmup;
    private GameMap currentMap;
    private ServerConnection server;
    private boolean running = true;
    private int currentCountdown;
    private LinkedList<Player> players = new LinkedList<>();

    public GameServer(String serverName, int serverPort, int tickRate, int updateRate, GameMap map) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.tickRate = tickRate;
        this.updateRate = updateRate;
        this.gameStartCountdown = 5000;
        this.gameOverCountDown = 10000;
        this.currentMap = map;

        server = new ServerConnection(serverPort);
        new Thread(server).start();
        server.addListener(this);

        new Thread(() -> gameLoop()).start();
    }

    public void stop() {
        running = false;
        server.stop();
    }

    private void gameLoop() {
        long tickRate = this.tickRate * 1000000;
        long updateRate = this.updateRate * 1000000;
        long previousTickTime = System.nanoTime();
        long previousUpdateTime = System.nanoTime();
        while (running) {
            long nowTime = System.nanoTime();
            long timeSinceLastTick = nowTime - previousTickTime;
            long timeSinceLastUpdate = nowTime - previousUpdateTime;

            if (timeSinceLastTick > tickRate) {
                previousTickTime = System.nanoTime() - (timeSinceLastTick - tickRate);
                tick();
            }

            if (timeSinceLastUpdate > updateRate) {
                previousUpdateTime = System.nanoTime() - (timeSinceLastUpdate - updateRate);
                update();
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
        GameServerUpdate update = new GameServerUpdate(state, getReadyPlayerPercentage(), gameObjects);
        for (Client client : connectedClients.keySet()) {
            client.send(update);
        }
    }

    private void tick() {
        for (GameObject gameObject : gameObjects) {
            gameObject.tick();
        }

        if (state == GameState.Warmup && getReadyPlayerPercentage() >= 1.0 && connectedClients.size() > 0) {
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
        } else if (state == GameState.Running) {
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
        } else if (state == GameState.GameOver) {
            if (currentCountdown <= 0) {
                System.out.println("SERVER STATE: Game Over -> Warmup");
                state = GameState.Warmup;
                startNewWarmup();
            } else {
                currentCountdown -= tickRate;
            }
        }
        placeGameMapObjects();
    }

    private void placeGameMapObjects() {
        if (currentMap.getGameMapObjects() == null) return;
        for (SpecialGameObject gameMapObject : currentMap.getGameMapObjects()) {
            GameObject gameObject = gameMapObject.getGameObject();
            if (gameObject.getId() == 0) {
                gameObject.setId(ID.getNext());
            }
            if (gameMapObject.getSpawnInterval() == 0) {
                if (!gameObjects.contains(gameObject)) {
                    if (!intersectsAnyGameObject(gameObject.getBounds())) {
                        int quarterGridPixel = Game.GRID_PIXEL_SIZE / 4;
                        gameObject.setX(gameObject.getX() - quarterGridPixel);
                        gameObject.setY(gameObject.getY() - quarterGridPixel);
                        System.out.println("Placing on map (Instant): " + gameObject + " Position: " + new Point(gameObject.getX(), gameObject.getY()));
                        gameObjects.add(gameObject);
                    }
                }
                continue;
            }
            if (gameMapObject.getTimer() <= 0) {
                if (gameObjects.contains(gameObject)) {
                    System.out.println("Removing from map (Timed out): " + gameObject + " Position: " + new Point(gameObject.getX(), gameObject.getY()));
                    gameObjects.remove(gameMapObject.getGameObject());
                    gameMapObject.setTimer(gameMapObject.getSpawnInterval());
                } else {
                    if (gameMapObject.isSpawnRandom()) {
                        Point point = findRandomMapPosition();
                        int quarterGridPixel = Game.GRID_PIXEL_SIZE / 4;
                        gameObject.setX(point.x - quarterGridPixel);
                        gameObject.setY(point.y - quarterGridPixel);
                    }
                    System.out.println("Placing on map (Spawn time): " + gameObject + " Position: " + new Point(gameObject.getX(), gameObject.getY()));
                    gameObjects.add(gameObject);
                    gameMapObject.setTimer(gameMapObject.getVisibleTime());
                }
            } else {
                gameMapObject.setTimer(gameMapObject.getTimer() - tickRate);
            }
        }
    }

    private void startNewWarmup() {
        System.out.println("Starting Warmup");
        Rectangle mapRectangle = getMapRectangle();
        Iterator<GameObject> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
            if (gameObject instanceof Trail) {
                ((Trail) gameObject).remove(mapRectangle);
            } else {
                iterator.remove();
            }
        }

        connectedClients.forEach((client, player) -> {
            player.setInvincible(true);
            player.setReady(false);
            player.setDead(false);
            player.setPickUp(null);
            player.setSpeed(currentMap.getPlayerSpeed());
            player.setDirection(Direction.Static);

            Point nextPosition = findRandomMapPosition();
            player.setX(nextPosition.x);
            player.setY(nextPosition.y);

            gameObjects.add(player);
            System.out.println("Placing player " + player.getName() + " at " + nextPosition);
        });
    }

    private void startNewGame() {
        System.out.println("Starting new game");
        if (currentMap.getStartingPositions() != null) {
            startingPositions.set(currentMap.getStartingPositions());
        } else {
            startingPositions.generate(currentMap.getGrid(), connectedClients.size());
        }

        Rectangle mapRectangle = getMapRectangle();
        Iterator<GameObject> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
            if (gameObject instanceof Player) {
                Player player = (Player) gameObject;

                player.setInvincible(false);
                player.setReady(false);
                player.setDead(false);
                player.setPickUp(null);
                player.setSpeed(currentMap.getPlayerSpeed());
                player.setDirection(Direction.Static);

                Point nextPosition = startingPositions.getNext();
                player.setX(nextPosition.x * Game.GRID_PIXEL_SIZE);
                player.setY(nextPosition.y * Game.GRID_PIXEL_SIZE);
                System.out.println("Placing player " + player.getName() + " at " + nextPosition);
            } else if (gameObject instanceof Trail) {
                ((Trail) gameObject).remove(mapRectangle);
            } else {
                iterator.remove();
            }
        }
    }

    private Rectangle getMapRectangle() {
        Dimension grid = currentMap.getGrid();
        return new Rectangle(grid.width * Game.GRID_PIXEL_SIZE, grid.height * Game.GRID_PIXEL_SIZE);
    }

    private boolean intersectsAnyGameObject(Rectangle rect) {
        for (GameObject object : gameObjects) {
            if ((object instanceof Player) && rect.getBounds().intersects(object.getBounds())) {
                return true;
            } else if ((object instanceof Wall) && ((Wall) object).intersects(rect.getBounds())) {
                return true;
            }
        }
        return false;
    }

    private double getReadyPlayerPercentage() {
        int players = connectedClients.size();
        int readyPlayers = 0;
        for (Player player : connectedClients.values()) {
            if (player.isReady()) {
                readyPlayers += 1;
            }
        }
        return ((double) readyPlayers / (double) players);
    }

    private Player newPlayer(String name) {
        if (connectedClients.size() > currentMap.getPlayers()) return null;
        Player player = new Player(name, gameObjects, currentMap);
        player.setId(ID.getNext());
        player.setSpeed(currentMap.getPlayerSpeed());
        player.setTickRate(tickRate);

        if (state == GameState.Warmup) {
            Point position = findRandomMapPosition();
            player.setX(position.x);
            player.setY(position.y);
            gameObjects.add(player);
            gameObjects.add(player.getTrail());
        }
        return player;
    }

    private Point findRandomMapPosition() {
        while (true) {
            Rectangle rectangle = new Rectangle(startingPositions.getOneRandom(currentMap.getGrid()));
            rectangle.x *= Game.GRID_PIXEL_SIZE;
            rectangle.y *= Game.GRID_PIXEL_SIZE;
            rectangle.width = Game.GRID_PIXEL_SIZE;
            rectangle.height = Game.GRID_PIXEL_SIZE;

            if (!intersectsAnyGameObject(rectangle)) {
                return rectangle.getLocation();
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
            player.setDirection(direction);
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
                System.out.println("Player connected: " + player);
                client.send(player);
                client.send(currentMap);
                client.send(gameObjects);
                connectedClients.put(client, player);
            } else {
                System.out.println("Client tried to connect but no slots are available.");
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
                + connectedClients.size() + "\0";

        return string.getBytes();
    }
}
