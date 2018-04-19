package gameclient.interfaces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gameclient.Buttons;
import gameclient.Resources;
import gameclient.keyinput.KeyBindings;
import gameclient.keyinput.KeyBindingsPanel;

/**
 * @author Erik Lundow
 */

public class SettingsScreen extends JPanel implements ActionListener {

    private JPanel pnlHead = new JPanel(new GridBagLayout());
    private JPanel pnlUserName = new JPanel(new GridBagLayout());
    private BufferedImage backgroundImage;
    private Font fontHead = new Font("Orbitron", Font.BOLD, 50);
    private Font fontText = new Font("Orbitron", Font.BOLD, 25);

    private String fileKeyBindPressed = "SetKeyBinding_Pressed.png";
    private String fileMusicPressed = "Music_Pressed.png";
    private String fileMusicUnpressed = "Music_Unpressed.png";
    private String fileKeyBindUnpressed = "SetKeyBinding_Unpressed.png";
    private String fileChangePressed = "Change_Pressed.png";
    private String fileChangeUnpressed = "Change_Unpressed.png";
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
    private UserInterface userInterface;

    public SettingsScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        backgroundImage = Resources.getImage("Stars.png");
        // setLayout(new GridBagLayout());
        pnlHead.setPreferredSize(new Dimension(500, 500));
        pnlHead.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();

        // Add Headline
        lblSettings.setFont(fontHead);
        lblSettings.setForeground(Color.white);
        c.gridy = 1;
        c.gridx = 1;
        c.weighty = 1;
        c.weightx = 11;
        c.gridwidth = GridBagConstraints.REMAINDER;
        pnlHead.add(lblSettings, c);

        // Add UserNamePnl
        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridx = 1;
        c.weightx = 1;
        c.weighty = 20;
        c.anchor = GridBagConstraints.NORTH;

        // lblUserName.setVerticalAlignment(SwingConstants.TOP);
        lblUserName.setMaximumSize(new Dimension(60, 25));
        lblUserName.setFont(fontText);
        lblUserName.setForeground(Color.white);
        pnlHead.add(lblUserName, c);

        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridx = 2;
        c.weightx = 10;
        c.weighty = 20;

        c.anchor = GridBagConstraints.NORTHWEST;
        // btnChange.setVerticalAlignment(SwingConstants.TOP);
        btnChange.setWidth(60);
        btnChange.setHeight(25);
        pnlHead.add(btnChange, c);

        c = new GridBagConstraints();
        c.gridy = 3;
        c.gridx = 1;
        c.weightx = 11;
        c.gridwidth = GridBagConstraints.REMAINDER;

        System.out.println(pnlKeyBinder.getSize());
        pnlHead.add(pnlKeyBinder, c);

        add(pnlHead);

        // add(lblUserName);
        // add(btnMusic);
        // add(lblMusic);
        //
        // add(lblSettings);
        //
        // add(btnChange);
        // add(pnlKeyBinder);
        // add(lblControls);

        addListeners();
        repaint();

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        g.setColor(Color.white);
        // lblSettings.setLocation(10, 10);
        g.setFont(fontText);
        // lblUserName.setLocation(30, 85);
        // btnChange.setLocation(300, 85);
        // btnMusic.setLocation(30, 150);
        // lblMusic.setLocation(95, 150);
        //
        // lblControls.setLocation(30, 275);
        // pnlKeyBinder.setLocation(30, 300);

    }

    public void addListeners() {
        btnKeyBinder.addActionListener(this);
        btnChange.addActionListener(this);
        btnMusic.addActionListener(this);
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
//        JFrame frame = new JFrame();
//        frame.setPreferredSize(new Dimension(500, 750));
//        frame.add(new SettingsScreen());
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//        // frame.setResizable(false);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
