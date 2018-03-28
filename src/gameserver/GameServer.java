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
    private final int tickRate, maxPlayers;
    private ServerConnection server;
    private boolean running = true;
    private final GameMap map;

    public GameServer(int serverPort, int updatesPerSecond, int maxPlayers, GameMap map) {
        this.tickRate = 1000 / updatesPerSecond;
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
        while (running) {
            //map.getGameObjects().forEach(gameObject -> gameObject.tick());
            Collection<GameObject> sendObjects = new LinkedList<>();
            for (GameObject gameObject : map.getGameObjects()) {
                if (gameObject instanceof Player) {
                    gameObject.tick();
                    sendObjects.add(((Player) gameObject).getLastTrail());
                }
                if (!(gameObject instanceof Wall)) {
                    sendObjects.add(gameObject);
                }
            }
            players.keySet().forEach(client -> client.send(sendObjects));
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
