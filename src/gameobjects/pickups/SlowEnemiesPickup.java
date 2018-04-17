package gameobjects.pickups;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import gameclient.Resources;
import gameobjects.GameObject;
import gameobjects.InstantPickup;
import gameobjects.Player;
import gameobjects.Trail;

public class SlowEnemiesPickup extends InstantPickup {
    private static final long serialVersionUID = 1;

    private int timer = 30;
    private ConcurrentLinkedQueue<GameObject> gameObjects;
    private Player player;

    public SlowEnemiesPickup() {
        this(0, 0);
    }

    public SlowEnemiesPickup(int x, int y) {
        super(x, y);
    }

    public void tick() {
        if (player == null) {
            return;
        }
        timer--;
        if (timer <= 0) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Player) {
                    if (!gameObject.equals(player)) {
                        System.out.println("före"+gameObject.getSpeed());
                        gameObject.setSpeed(gameObject.getSpeed() * 2);
                        System.out.print("efter"+gameObject.getSpeed());

                    }
                }
            }
            player.setPickUp(null);
            player = null;
        } 
    }

    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
        this.player = player;
        this.gameObjects = gameObjects;
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                if (!gameObject.equals(player)) {
                    int speed = gameObject.getSpeed();
                    gameObject.setSpeed((int) (speed * 0.5));
                }

            }
        }

    }

    @Override
    public void render(Graphics2D g) {
        BufferedImage image = Resources.getImage("SlowEnemiesPickup.png");
        g.drawImage(image, x, y, width, height, null);

    }

}
