package gameserver;

import common.*;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Johannes Blüml
 */
public class GameServer implements ClientListener {
    private ConcurrentHashMap<Client, Player> players = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<>();

    private int fps = 60;
    private int tickRate = 1000 / fps;
    private int serverPort = 32000;
    private int playerSpeed = 4;

    private Random random = new Random();

    public GameServer() {
        ServerConnection server = new ServerConnection();
        server.start(serverPort);
        server.addListener(this);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                gameObjects.forEach(gameObject -> gameObject.tick());
                players.keySet().forEach(client -> client.send(gameObjects));
                try {
                    Thread.sleep(tickRate);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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
            Player player = new Player(random.nextInt(400), random.nextInt(400), (String) value, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)), gameObjects);
            player.setSpeed(playerSpeed);
            gameObjects.add(player);
            players.put(client, player);
            System.out.println(players);
        } else if (value instanceof Direction && players.containsKey(client)) {
            Direction direction = (Direction) value;
            Player player = players.get(client);
            player.setDirection(direction);
        }
    }

    @Override
    public void onClose(Client client) {
        Player player = players.remove(client);
        gameObjects.remove(player);
    }
}
