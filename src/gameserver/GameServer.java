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
    public static final int GRIDSIZE = 100, WIDTH = 50 * GRIDSIZE, HEIGHT = 50 * GRIDSIZE;

    private ConcurrentHashMap<Client, Player> players = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<>();

    private int fps = 30;
    private int tickRate = 1000 / fps;
    private int serverPort = 32000;
    private int playerSpeed = GRIDSIZE / 4;

    private Random random = new Random();

    public GameServer() {
        ServerConnection server = new ServerConnection();
        server.start(serverPort);
        server.addListener(this);

        new Thread(() -> {
            while (true) {
                gameObjects.forEach(gameObject -> gameObject.tick());
                players.keySet().forEach(client -> client.send(gameObjects));
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
            int startPositionX = random.nextInt(GameServer.WIDTH / GameServer.GRIDSIZE) * GameServer.GRIDSIZE;
            int startPositionY = random.nextInt(GameServer.HEIGHT / GameServer.GRIDSIZE) * GameServer.GRIDSIZE;
            Player player = new Player(startPositionX, startPositionY, (String) value, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)), gameObjects);
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
