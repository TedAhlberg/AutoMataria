package PickUp;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import gameclient.Resources;
import gameobjects.Pickup;
import gameobjects.Player;

public class SelfSpeedUpPickUp extends Pickup {

    private int timer;

    public SelfSpeedUpPickUp(int x, int y) {
        super(x, y);

    }

    public void tick() {
    }

    public void render(Graphics2D g) {
       BufferedImage image = Resources.getImage("SelfSpeedUp.png");
        g.drawImage(image, x, y, width, height, null);

    }

    public void use(Player player) {
        int speed = player.getSpeedPerSecond();
        speed *= 0.25;

    }

}
