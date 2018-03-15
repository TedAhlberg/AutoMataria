package gameclient;

import common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * @author Johannes Blüml
 */
public class Game extends Canvas {
    public static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
    public static final String TITLE = "Auto-Mataria";

    private ClientConnection client;
    private String serverIP = "127.0.0.1";
    private int serverPort = 32000;

    public Game() {
        new Window(WIDTH, HEIGHT, TITLE, this);
        String playerName = JOptionPane.showInputDialog("Enter your username:");

        try {
            Socket socket = new Socket(serverIP, serverPort);
            client = new ClientConnection(socket, gameObjects -> render((CopyOnWriteArrayList<GameObject>) gameObjects));
            client.send(playerName);
            this.addKeyListener(new KeyInput(client));
            this.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Sorry. I don't seem to be able to connect the server.");
            System.exit(1);
        }
    }

    public static int clamp(int var, int min, int max) {
        if (var >= max) return max;
        if (var <= min) return min;
        return var;
    }

    public static void main(String[] args) {
        new Game();
    }

    private void render(CopyOnWriteArrayList<GameObject> gameObjects) {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(2);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        gameObjects.forEach(gameObject -> gameObject.render(g));

        g.dispose();

        bs.show();

        Toolkit.getDefaultToolkit().sync();
    }
}
