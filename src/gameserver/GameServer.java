package gameserver;

import common.*;
import gameclient.Game;
import gameobjects.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Johannes Blüml
 */
public class GameServer implements ClientListener {
    private final GameColors colors = new GameColors();
    private final StartingPositions startingPositions = new StartingPositions();
    private final ConcurrentHashMap<Client, Player> connectedClients = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<GameObject> gameObjects = new ConcurrentLinkedQueue<>();
    private final int tickRate, updateRate;
    private GameState state = GameState.Warmup;
    private GameMap currentMap;
    private ServerConnection server;
    private boolean running = true;

    public GameServer(int serverPort, int tickRate, int updateRate, GameMap map) {
        this.tickRate = tickRate;
        this.updateRate = updateRate;
        this.currentMap = map;
        gameObjects.addAll(Arrays.asList(map.getStartingGameObjects()));

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
                for (GameObject gameObject : gameObjects) {
                    gameObject.tick();
                }
            }

            if (timeSinceLastUpdate > updateRate) {
                previousUpdateTime = System.nanoTime() - (timeSinceLastUpdate - updateRate);
                GameServerUpdate update = new GameServerUpdate(state, getReadyPlayerPercentage(), gameObjects);
                for (Client client : connectedClients.keySet()) {
                    client.send(update);
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
                readyPlayers+=1;
            }
        }
        return ((double) readyPlayers / (double) players);
    }

    private Player newPlayer(String name) {
        if (connectedClients.size() > currentMap.getPlayers()) return null;
        Player player = new Player(name, gameObjects, currentMap);
        player.setSpeed(currentMap.getPlayerSpeed());
        player.setSpeedPerSecond((1000 / tickRate) * currentMap.getPlayerSpeed());

        if (state == GameState.Warmup) {
            boolean hasFoundStartingPosition = false;
            while (!hasFoundStartingPosition) {
                Rectangle point = new Rectangle(startingPositions.getOneRandom(currentMap.getGrid()));
                point.x *= Game.GRID_PIXEL_SIZE;
                point.y *= Game.GRID_PIXEL_SIZE;
                point.width = player.getWidth();
                point.height = player.getHeight();

                if (!intersectsAnyGameObject(point)) {
                    hasFoundStartingPosition = true;
                    player.setX(point.x);
                    player.setY(point.y);
                }
            }
            gameObjects.add(player);
            gameObjects.add(player.getTrail());
        }
        return player;
    }

    @Override
    public void onConnect(Client client) {
        System.out.println("SERVER: Client connected.");
    }

    @Override
    public void onData(Client client, Object value) {
        if (value instanceof String && !connectedClients.containsKey(client)) {
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
        } else if (value instanceof Direction && connectedClients.containsKey(client)) {
            Direction direction = (Direction) value;
            Player player = connectedClients.get(client);
            player.setDirection(direction);
        } else if (value instanceof Action) {
            Player player = connectedClients.get(client);
            if (value == Action.TogglePlayerColor) {
                player.setColor(colors.exchangeColor(player.getColor()));
            } else if (value == Action.ToggleReady) {
                player.setReady(!player.isReady());
            }
        }
    }

    @Override
    public void onClose(Client client) {
        Player player = connectedClients.remove(client);
        player.setDead();
        player.setReady(false);
        System.out.println("Player disconnected: " + player);
    }
}
