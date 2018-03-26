package gameclient;

import common.GameObject;
import gameserver.GameServer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Johannes Bluml
 */
public class Game extends Canvas {
    public static final String TITLE = "Auto-Mataria";
    private final double scale;

    private ClientConnection client;
    private String serverIP = "localhost";
    private int serverPort = 32000;
    private BufferedImage background;

    public Game() {
        String playerName = JOptionPane.showInputDialog("Enter your username:", "Username");
        new Window(TITLE, this);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            env.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Orbitron Bold.ttf")));
            background = ImageIO.read(new File("resources/Stars.png"));
        } catch (IOException | FontFormatException e) {
            System.out.println("Failed to load resources.");
            e.printStackTrace();
        }

        Rectangle bounds = env.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        scale = Math.min(bounds.getWidth() / GameServer.WIDTH, bounds.getHeight() / GameServer.HEIGHT);

        try {
            Socket socket = new Socket(serverIP, serverPort);
            client = new ClientConnection(socket, gameObjects -> render((Collection<GameObject>) gameObjects));
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
        new Game();
    }

    private void render(Collection<GameObject> gameObjects) {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(2);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        g.drawImage(background, 0, 0, this);
        g.scale(scale, scale);
        gameObjects.forEach(gameObject -> gameObject.render(g));

        g.dispose();
        bs.show();
        Toolkit.getDefaultToolkit().sync();
    }
}
