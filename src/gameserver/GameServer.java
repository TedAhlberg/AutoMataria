package gameserver;

import common.*;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Johannes Blüml
 */
public class GameServer implements ClientListener {
    private ConcurrentHashMap<Client, Player> players = new ConcurrentHashMap<>();

    private int fps = 30;
    private int tickRate = 1000 / fps;
    private int serverPort = 32000;
    private GameMap map = new GameMap("default");
    private Random random = new Random();

    public GameServer() {
        ServerConnection server = new ServerConnection();
        server.start(serverPort);
        server.addListener(this);

        map.setWalls(Color.CYAN.darker().darker());

        new Thread(() -> {
            while (true) {
                map.getGameObjects().forEach(gameObject -> gameObject.tick());
                players.keySet().forEach(client -> client.send(map.getGameObjects()));
                try {
                    Thread.sleep(tickRate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        new GameServer();
    }

    @Override
    public void onConnect(Client client) {

    }

    @Override
    public void onData(Client client, Object value) {
        if (value instanceof String && !players.containsKey(client)) {
            Player player = map.newPlayer((String) value, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
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
    }
}
