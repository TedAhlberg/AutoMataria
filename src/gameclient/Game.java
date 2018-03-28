package gameclient;

import common.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Johannes Bluml
 */
public class Game extends JPanel {
    public static final String TITLE = "Auto-Mataria";
    private CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private CopyOnWriteArraySet<Point> paintedPositions = new CopyOnWriteArraySet<>();
    private double scale;
    private GameMap map;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();

    private ClientConnection client;
    private String serverIP = "localhost";
    private int serverPort = 32000;
    private BufferedImage background;
    //private Audio backgroundMusic = new Audio("AM-trck1.mp3");

    public Game() {
        String playerName = JOptionPane.showInputDialog("Enter your username:", "Username");
        new Window(TITLE, this);
        //backgroundMusic.play();

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
                    gameObjects = new CopyOnWriteArrayList<>();
                    gameObjects.addAll((Collection<GameObject>) data);
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

        Timer t = new Timer(16, e -> this.repaint());
        t.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background == null) return;

        Graphics2D g2 = (Graphics2D) g;
        Graphics2D gBackground = (Graphics2D) background.getGraphics();

        g2.drawImage(background, 0, 0, this);
        g2.scale(scale, scale);
        gBackground.scale(scale, scale);
        for (GameObject gameObject : gameObjects) {
            if (gameObject == null) continue;
            if (gameObject instanceof Trail) {
                ((Trail) gameObject).removeAll(paintedPositions);
                paintedPositions.addAll(((Trail) gameObject).getAll());
                gameObject.render(gBackground);
            } else if (gameObject instanceof Wall) {
                gameObject.render(gBackground);
            } else {
                gameObject.render(g2);
            }
        }

        toolkit.sync();
    }
}
