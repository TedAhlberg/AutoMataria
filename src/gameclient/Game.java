package gameclient;

import common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * @author Johannes Bluml
 */
public class Game extends Canvas {
    public static final int WIDTH = 1000, HEIGHT = 1000;
    public static final String TITLE = "Auto-Mataria";

    private ClientConnection client;
    private String serverIP = "localhost";
    private int serverPort = 32000;

    private Image splashscreen = Toolkit.getDefaultToolkit().getImage("resources/Stars.png").getScaledInstance(WIDTH, HEIGHT, Image.SCALE_AREA_AVERAGING);

    public Game() {
        String playerName = JOptionPane.showInputDialog("Enter your username:", "Username");
        new Window(TITLE, this);

        try {
            Socket socket = new Socket(serverIP, serverPort);
            client = new ClientConnection(socket, gameObjects -> render((CopyOnWriteArrayList<GameObject>) gameObjects));
            client.send(playerName);
            this.addKeyListener(new KeyInput(client));
            this.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to connect server.");
            System.exit(1);
        }
    }

    public static int clamp(int var, int min, int max) {
        if (var >= max) return max;
        if (var <= min) return min;
        return var;
    }

    public static void main(String[] args) {
        // Load ORBITRON font
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Orbitron Bold.ttf")));
        } catch (IOException|FontFormatException e) {
            e.printStackTrace();
        }
        new Game();
    }

    private void render(CopyOnWriteArrayList<GameObject> gameObjects) {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(2);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        Rectangle bounds = g.getDeviceConfiguration().getBounds();
        double scale = Math.min(bounds.getWidth() / WIDTH, bounds.getHeight() / HEIGHT);
        g.scale(scale,scale);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.drawImage(splashscreen, 0, 0, this);

        gameObjects.forEach(gameObject -> gameObject.render(g));

        g.dispose();

        bs.show();

        Toolkit.getDefaultToolkit().sync();
    }
}
