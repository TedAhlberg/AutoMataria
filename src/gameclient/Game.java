package gameclient;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import common.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Johannes Bluml
 */
public class Game extends Canvas {
    public static final String TITLE = "Auto-Mataria";
    private CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private double scale;
    private GameMap map;

    private ClientConnection client;
    private String serverIP = "localhost";
    private int serverPort = 32000;
    private BufferedImage background;
    private AudioPlayer backgroundMusic = AudioPlayer.getSound("resources/Music/AM-track1.wav");

    public Game() {
        String playerName = JOptionPane.showInputDialog("Enter your username:", "Username");
        new Window(TITLE, this);
        backgroundMusic.play();

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            env.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Orbitron Bold.ttf")));
        } catch (IOException | FontFormatException e) {
            System.out.println("Failed to load resources.");
            e.printStackTrace();
        }

        try {
            Socket socket = new Socket(serverIP, serverPort);
            client = new ClientConnection(socket, data -> {
                if (data instanceof GameMap) {
                    changeGameMap((GameMap) data);
                } else if (data instanceof Collection) {
                    gameObjects.clear();
                    gameObjects.addAll((Collection<GameObject>) data);
//                    for (GameObject gameObject : ((Collection<GameObject>) data)) {
//                        gameObjects.add(gameObject);
//                    }
                }
            });
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

    private void changeGameMap(GameMap map) {
        this.map = map;
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            background = ImageIO.read(new File(map.getBackground()));
        } catch (IOException e) {
            System.out.println("Failed to load resources.");
            e.printStackTrace();
            // TODO: Set a default background when failed to load.
        }

        Rectangle screen = env.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        this.scale = Math.min(screen.getWidth() / map.getWidth(), screen.getHeight() / map.getHeight());

        Graphics2D g = (Graphics2D) background.getGraphics();
        g.scale(this.scale, this.scale);
        g.setPaint(new Color(1, 1, 1, 0.2f));
        for (int i = 0; i <= map.getWidth(); i += map.getGridSize()) {
            g.drawLine(i, 0, i, map.getHeight());
        }
        for (int i = 0; i <= map.getHeight(); i += map.getGridSize()) {
            g.drawLine(0, i, map.getWidth(), i);
        }
        g.dispose();

        new Thread(() -> render()).start();
    }

    private void render() {
        while (true) {
            BufferStrategy bs = this.getBufferStrategy();
            if (bs == null) {
                this.createBufferStrategy(2);
                continue;
            }
            Graphics2D g = (Graphics2D) bs.getDrawGraphics();
            Graphics2D gBackground = (Graphics2D) background.getGraphics();

            g.drawImage(background, 0, 0, this);
            g.scale(scale, scale);
            gBackground.scale(scale, scale);
            for (GameObject gameObject : gameObjects) {
                if (gameObject == null) continue;
                if (gameObject instanceof Wall) {
                    gameObject.render(gBackground);
                } else {
                    gameObject.render(g);
                }
            }

            g.dispose();
            bs.show();
            Toolkit.getDefaultToolkit().sync();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
