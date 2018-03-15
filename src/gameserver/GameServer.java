package gameserver;

import common.*;

import java.util.concurrent.*;

/**
 * @author Johannes Blüml
 */
public class GameServer implements ClientListener {
    private ConcurrentHashMap<Client, Player> players = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private int tickRate = 40;
    private int serverPort = 32000;
    private int playerSpeed = 7;

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
        if (value instanceof String) {
            Player player = new Player(0, 0, (String) value);
            gameObjects.add(player);
            players.put(client, player);
            System.out.println(players);
        } else if (value instanceof Direction && players.containsKey(client)) {
            Direction direction = (Direction) value;
            Player player = players.get(client);
            if (direction == Direction.Left) {
                player.setSpeedX(-playerSpeed);
                player.setSpeedY(0);
            } else if (direction == Direction.Right) {
                player.setSpeedX(playerSpeed);
                player.setSpeedY(0);
            } else if (direction == Direction.Up) {
                player.setSpeedX(0);
                player.setSpeedY(-playerSpeed);
            } else if (direction == Direction.Down) {
                player.setSpeedX(0);
                player.setSpeedY(playerSpeed);
            }
        }
    }

    @Override
    public void onClose(Client client) {
        Player player = players.remove(client);
        gameObjects.remove(player);
    }
}
