package gameserver;

import common.*;
import gameclient.Game;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Johannes Blüml
 */
public class GameServer implements ClientListener {
    private Timer timer;
    private ConcurrentHashMap<Client, Player> players = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<>();

    private int fps = 30;
    private int tickRate = 1000 / fps;
    private int serverPort = 32000;
    private int playerSpeed = 50;

    private Random random = new Random();

    public GameServer() {
        ServerConnection server = new ServerConnection();
        server.start(serverPort);
        server.addListener(this);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                tick();
            }
        }, 0, tickRate);
    }

    public static void main(String[] args) {
        new GameServer();
    }

    private void tick() {
        gameObjects.forEach(gameObject -> gameObject.tick());
        players.keySet().forEach(client -> client.send(gameObjects));
    }

    @Override
    public void onConnect(Client client) {

    }

    @Override
    public void onData(Client client, Object value) {
        if (value instanceof String && !players.containsKey(client)) {
            Player player = new Player(random.nextInt(Game.WIDTH / 10000) * 10000, random.nextInt(Game.HEIGHT / 10000) * 10000, (String) value, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)), gameObjects);
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
