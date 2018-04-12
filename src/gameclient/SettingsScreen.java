package gameclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gameclient.keyinput.KeyBindings;
import gameclient.keyinput.KeyBindingsPanel;
import gameobjects.Player;

/**
 * @author Erik Lundow
 */

public class SettingsScreen extends JPanel implements ActionListener {

    private GamePanel gamePanel;
    private Player player;
    private BufferedImage backgroundImage;
    private Font fontHead = new Font("Orbitron", Font.BOLD, 50);
    private Font fontText = new Font("Orbitron", Font.BOLD, 20);

    private String fileKeyBindPressed = "images/SetKeyBinding_Pressed.png";
    private String fileMusicPressed = "images/Music_Pressed.png";
    private String fileMusicUnpressed = "images/Music_Unpressed.png";
    private String fileKeyBindUnpressed = "images/SetKeyBinding_Unpressed.png";
    private String fileChangePressed = "images/Change_Pressed.png";
    private String fileChangeUnpressed = "images/Change_Unpressed.png";
    private String user = "Testperson";
    private Buttons btnChange = new Buttons(fileChangePressed, fileChangeUnpressed);
    private Buttons btnKeyBinder = new Buttons(fileKeyBindPressed, fileKeyBindUnpressed);
    private Buttons btnMusic = new Buttons(fileMusicPressed, fileMusicUnpressed);

    private KeyBindings keyBindings = new KeyBindings();
    private KeyBindingsPanel pnlKeyBinder = new KeyBindingsPanel(keyBindings);

    private JLabel lblUserName = new JLabel("USER: " + user);
    private JLabel lblChangeUserName = new JLabel("CHANGE USERNAME");
    private JLabel lblSettings = new JLabel("SETTINGS");
    private JLabel lblControls = new JLabel("CONTROLS");
    private JLabel lblSounds = new JLabel("SOUND");
    private Color color;

    public SettingsScreen(GamePanel hgamePanel) {
        this.gamePanel = gamePanel;

        setPreferredSize(new Dimension(500, 500));

        try {
            backgroundImage = ImageIO.read(new File("resources/Stars.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        lblSettings.setFont(fontHead);
        lblSettings.setForeground(Color.white);

        lblUserName.setFont(fontText);
        lblUserName.setForeground(Color.white);

        btnChange.setBounds(300, 85, 60, 25);
        btnChange.setSize(100, 100);

        lblControls.setFont(fontText);
        lblControls.setForeground(Color.WHITE);
        lblControls.setBounds(30, 155, 300, 20);

        lblSounds.setFont(fontText);
        lblSounds.setForeground(Color.white);
        lblSounds.setBounds(30, 190, 300, 20);

        btnChange.setWidth(60);
        btnChange.setHeight(25);

        btnChange.setMinimumSize(new Dimension(60, 25));
        btnChange.setPreferredSize(new Dimension(60, 25));

        btnKeyBinder.setWidth(200);
        btnKeyBinder.setHeight(15);

        btnMusic.setWidth(60);
        btnMusic.setHeight(25);

        btnKeyBinder.setBounds(230, 155, 200, 15);
        pnlKeyBinder.setForeground(null);

        btnMusic.setBounds(30, 275, 120, 50);
        btnChange.add(pnlKeyBinder);

        add(lblUserName);
        // add(btnMusic);
        // add(btnKeyBinder);
        // add(lblSounds);

        add(lblSettings);

        add(btnChange);
        add(pnlKeyBinder);
        add(lblControls);

        addListeners();
        repaint();

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        pnlKeyBinder.setLocation(30, 300);
        g.setColor(Color.white);
        lblSettings.setLocation(10, 10);
        g.setFont(fontText);
        lblUserName.setLocation(30, 85);
        lblChangeUserName.setLocation(30, 120);
        btnChange.setLocation(300, 85);
        lblControls.setLocation(30, 275);
        g.setColor(color);
        g.fillRect(30, 150, 20, 20);

    }

    public void addListeners() {
        btnKeyBinder.addActionListener(this);
        btnChange.addActionListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnKeyBinder) {

        }

        if (e.getSource() == btnChange) {
            user = JOptionPane.showInputDialog("Input new username");

            lblUserName.setText("USER: " + user);
            repaint();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        GamePanel gamePanel = new GamePanel();
        frame.setPreferredSize(new Dimension(750, 750));
        frame.add(new SettingsScreen(gamePanel));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
