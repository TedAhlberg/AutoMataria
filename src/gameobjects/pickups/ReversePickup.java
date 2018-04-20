package gameobjects.pickups;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import gameclient.Resources;
import gameobjects.GameObject;
import gameobjects.InstantPickup;
import gameobjects.Player;

public class ReversePickup extends InstantPickup  {
    
    private int timer;

    
    public void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects) {
        
        
    }

    
    public void render(Graphics2D g) {
        if (taken) {
            return;
        }
        BufferedImage image = Resources.getImage("ReversePickup.png");
        g.drawImage(image, x, y, width, height, null);
        
    }
    

}
