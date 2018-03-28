package gameserver;

import common.*;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Johannes Blüml
 */
public class GameServer implements ClientListener {
    private final ConcurrentHashMap<Client, Player> players = new ConcurrentHashMap<>();
    private final int tickRate;
    private int updateRate;
    private final int maxPlayers;
    private ServerConnection server;
    private boolean running = true;
    private final GameMap map;

    public GameServer(int serverPort, int tickRate, int updateRate, int maxPlayers, GameMap map) {
        this.tickRate = tickRate;
        this.updateRate = (updateRate > tickRate) ? updateRate / tickRate : 1;
        this.maxPlayers = maxPlayers;
        this.map = map;

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
        int counter = 0;
        while (running) {
            counter += 1;
            for (GameObject gameObject : map.getGameObjects()) {
                gameObject.tick();
            }
            if (counter % updateRate == 0) {
                players.keySet().forEach(client -> client.send(map.getGameObjects()));
                counter = 0;
            }
            try {
                Thread.sleep(tickRate);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
