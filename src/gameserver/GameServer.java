package gameserver;

import common.*;
import gameobjects.GameObject;
import gameobjects.Player;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Johannes Blüml
 */
public class GameServer implements ClientListener {
    private final ConcurrentHashMap<Client, Player> players = new ConcurrentHashMap<>();
    private final int tickRate;
    private final int maxPlayers;
    private final GameMap map;
    private int updateRate;
    private ServerConnection server;
    private boolean running = true;

    public GameServer(int serverPort, int tickRate, int updateRate, int maxPlayers, GameMap map) {
        this.tickRate = tickRate;
        this.updateRate = updateRate;
        this.maxPlayers = maxPlayers;
        this.map = map;

        map.setServerTickRate(tickRate);

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
                for (GameObject gameObject : map.getGameObjects()) {
                    gameObject.tick();
                }
            }

            if (timeSinceLastUpdate > updateRate) {
                previousUpdateTime = System.nanoTime() - (timeSinceLastUpdate - updateRate);
                players.keySet().forEach(client -> client.send(map.getGameObjects()));
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

    @Override
    public void onConnect(Client client) {

    }

    @Override
    public void onData(Client client, Object value) {
        if (value instanceof String && !players.containsKey(client)) {
            Player player = map.newPlayer((String) value);
            if (player != null) {
                System.out.println("Player connected: " + player);
                client.send(player);
                client.send(map);
                players.put(client, player);
            } else {
                System.out.println("Client tried to connect but no slots are available.");
            }
        } else if (value instanceof Direction && players.containsKey(client)) {
            Direction direction = (Direction) value;
            Player player = players.get(client);
            player.setDirection(direction);
        }
    }

    @Override
    public void onClose(Client client) {
        Player player = players.remove(client);
        System.out.println("Player disconnected: " + player);
    }
}
