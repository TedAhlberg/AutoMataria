package PickUp;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import gameclient.Resources;
import gameobjects.Pickup;
import gameobjects.Player;

/**
 * @author Dante H�kansson
 * 
 */

public class SelfSpeedUpPickUp extends Pickup {

    private int timer;

    public SelfSpeedUpPickUp(int x, int y) {
        super(x, y);

    }

    public void tick() {
    }

    public void render(Graphics2D g) {
       BufferedImage image = Resources.getImage("SelfSpeedUp2.png");
        g.drawImage(image, x, y, width, height, null);

    }

    public void use(Player player) {
        System.out.println(player.getSpeedPerSecond());
        int speed = player.getSpeedPerSecond();
        player.setSpeedPerSecond((int) (speed*2));
        System.out.println(player.getSpeedPerSecond());

    }

}
