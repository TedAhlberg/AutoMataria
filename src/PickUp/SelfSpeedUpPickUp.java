package PickUp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import common.GameMap;
import gameclient.Game;
import gameclient.Resources;
import gameclient.TestStartScreen;
import gameobjects.GameObject;
import gameobjects.PickUp;
import gameobjects.Player;

public class SelfSpeedUpPickUp extends PickUp {

    
    private BufferedImage icon;
    private int timer;
    
    

    public SelfSpeedUpPickUp(int x, int y, long visibleTime, ConcurrentLinkedQueue<GameObject> gameObjects) {
        super(x, y, y, gameObjects);
        icon = Resources.getImage("SelfSpeedUp.png");

    }
    public void tick() {
        pickedUp();
    }

    public void render(Graphics2D g) {
        g.drawImage(icon, x, y, width, height, null);

    }

    public void use(Player player) {
	   int speed = player.getSpeedPerSecond();
	   speed *= 0.25;
	   
		}
   
	}

