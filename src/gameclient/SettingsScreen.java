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
    Player player;
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
    private JLabel lblMusic = new JLabel("ON");
    private JLabel lblSettings = new JLabel("SETTINGS");
    private JLabel lblControls = new JLabel("CONTROLS");
    private JLabel lblSounds = new JLabel("SOUND");
    private Color color;

    public SettingsScreen() {
        Player player = new Player(fileChangePressed, null, null);

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

        lblMusic.setFont(fontText);
        lblMusic.setForeground(color.white);

        btnChange.setWidth(60);
        btnChange.setHeight(25);

        btnChange.setMinimumSize(new Dimension(60, 25));
        btnChange.setPreferredSize(new Dimension(60, 25));

        btnMusic.setWidth(60);
        btnMusic.setHeight(25);

        btnMusic.setMinimumSize(new Dimension(60, 25));
        btnMusic.setPreferredSize(new Dimension(60, 25));

        add(lblUserName);
        add(btnMusic);
        add(lblMusic);

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
        g.setColor(Color.white);
        lblSettings.setLocation(10, 10);
        g.setFont(fontText);
        lblUserName.setLocation(30, 85);
        btnChange.setLocation(300, 85);
        btnMusic.setLocation(30, 150);
        lblMusic.setLocation(95, 150);

        lblControls.setLocation(30, 275);
        pnlKeyBinder.setLocation(30, 300);

    }

    public void addListeners() {
        btnKeyBinder.addActionListener(this);
        btnChange.addActionListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnMusic) {
           
            lblMusic.setText("OFF");

        }

        if (e.getSource() == btnChange) {
            user = JOptionPane.showInputDialog("Input new username");

            if (user != null) {
                lblUserName.setText("USER: " + user);
                repaint();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(500, 750));
        frame.add(new SettingsScreen());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
