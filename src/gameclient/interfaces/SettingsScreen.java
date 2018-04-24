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

import gameclient.Audio;
import gameclient.Buttons;
import gameclient.Resources;
import gameclient.SoundFx;
import gameclient.keyinput.KeyBindings;
import gameclient.keyinput.KeyBindingsPanel;

/**
 * @author Erik Lundow
 */

public class SettingsScreen extends JPanel implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel pnlHead = new JPanel(new GridBagLayout());
    private JPanel pnlUserName = new JPanel(new GridBagLayout());
    private JPanel pnlMusic = new JPanel(new GridBagLayout());
    private BufferedImage backgroundImage;
    private Font fontHead = new Font("Orbitron", Font.BOLD, 50);
    private Font fontText = new Font("Orbitron", Font.BOLD, 25);
    private Font fontSmall = new Font("Orbitron", Font.BOLD, 20);

    private String fileKeyBindPressed = "SetKeyBinding_Pressed.png";
    private String fileMusicPressed = "Music_Pressed.png";
    private String fileMusicUnpressed = "Music_Unpressed.png";
    private String fileKeyBindUnpressed = "SetKeyBinding_Unpressed.png";
    private String fileChangePressed = "Change_Pressed.png";
    private String fileChangeUnpressed = "Change_Unpressed.png";
    private String fileExitPressed = "Exit_Pressed.png";
    private String fileExitUnpressed = "Exit_Unpressed.png";
    private String fileSfxPressed = "SFX_Pressed.png";
    private String fileSfxUnpressed = "SFX_Unpressed.png";
    private String user = "Testperson";

    private Buttons btnChange = new Buttons(fileChangePressed, fileChangeUnpressed);
    private Buttons btnKeyBinder = new Buttons(fileKeyBindPressed, fileKeyBindUnpressed);
    private Buttons btnMusic = new Buttons(fileMusicPressed, fileMusicUnpressed);
    private Buttons btnExit = new Buttons(fileExitPressed, fileExitUnpressed);
    private Buttons btnSFX = new Buttons(fileSfxPressed, fileSfxUnpressed);

    private KeyBindings keyBindings = new KeyBindings();
    private KeyBindingsPanel pnlKeyBinder = new KeyBindingsPanel(keyBindings);

    private JLabel lblUserName = new JLabel("USER: " + user);
    private JLabel lblMusic = new JLabel("ON");
    private JLabel lblSFX = new JLabel("ON");
    private JLabel lblSettings = new JLabel("SETTINGS");
    private JLabel lblControls = new JLabel("CONTROLS");
    private JLabel lblSpacing = new JLabel(" ");
    private JLabel lblSpacing2 = new JLabel("                                 ");
    private UserInterface userInterface;

    public SettingsScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        backgroundImage = Resources.getImage("Stars.png");
        // setLayout(new GridBagLayout());
        // pnlHead.setPreferredSize(new Dimension(500, 500));
        pnlHead.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();

        // Add Headline
        lblSettings.setFont(fontHead);
        lblSettings.setForeground(Color.white);
        c.gridy = 1;
        c.gridx = 1;
        c.weighty = 1;
        c.weightx = 11;
        c.ipady = 30;
        c.gridwidth = GridBagConstraints.REMAINDER;
        pnlHead.add(lblSettings, c);

        // Add UserNamelbl
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.WEST;

        c.ipadx = 20;
        c.ipady = 50;
        lblUserName.setFont(fontText);
        lblUserName.setForeground(Color.white);
        pnlUserName.setSize(60, 25);
        pnlUserName.add(lblUserName, c);
        pnlUserName.setOpaque(false);

        // Add ChangeButton
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 5;
        c.weightx = 3;
        c.weighty = 1;
        c.anchor = GridBagConstraints.EAST;
        c.ipadx = 10;
        btnChange.setWidth(60);
        btnChange.setHeight(25);
        pnlUserName.add(btnChange, c);

        // Add UserNamePanel
        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridx = 1;
        c.weightx = 1;
        c.weighty = 20;
        c.anchor = GridBagConstraints.NORTH;
        pnlHead.add(pnlUserName, c);

        // Add ControlLabel
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.ipady = 10;
        lblControls.setFont(fontText);
        lblControls.setForeground(Color.white);
        pnlHead.add(lblControls, c);

        // Add keybinderPanel
        c = new GridBagConstraints();
        c.gridy = 4;
        c.gridx = 1;
        c.weightx = 11;
        c.gridwidth = GridBagConstraints.REMAINDER;
        pnlHead.add(pnlKeyBinder, c);

        // Add musicButton
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 1;
        c.ipadx = 10;
        btnMusic.setWidth(60);
        btnMusic.setHeight(25);
        pnlMusic.setOpaque(false);
        pnlMusic.add(btnMusic, c);

        // Add musicLabel
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 2;
        c.ipadx = 10;
        lblMusic.setFont(fontSmall);
        lblMusic.setForeground(Color.white);
        ;
        pnlMusic.add(lblMusic, c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 3;
        c.ipadx = 10;
        ;
        pnlMusic.add(lblSpacing2, c);

        // Add SFXButton
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 4;
        c.ipadx = 10;
        btnSFX.setWidth(60);
        btnSFX.setHeight(25);
        pnlMusic.add(btnSFX, c);

        // Add SFXLabel
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 5;
        c.ipadx = 10;
        lblSFX.setFont(fontSmall);
        lblSFX.setForeground(Color.white);
        ;
        pnlMusic.add(lblSFX, c);

        // Add MusicPanel
        c = new GridBagConstraints();
        c.ipady = 30;
        c.gridy = 6;
        c.gridx = 1;
        pnlHead.add(pnlMusic, c);

        // Add spacing
        lblSpacing.setFont(fontHead);
        c.gridy = 7;
        c.gridx = 1;
        c.weighty = 1;
        c.weightx = 11;
        c.gridwidth = GridBagConstraints.REMAINDER;
        pnlHead.add(lblSpacing, c);

        c = new GridBagConstraints();
        c.gridy = 8;
        c.gridx = 1;
        c.anchor = GridBagConstraints.SOUTH;
        btnExit.setWidth(60);
        btnExit.setHeight(25);
        pnlHead.add(btnExit, c);
        add(pnlHead);

        addListeners();
        repaint();

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

    }

    public void addListeners() {
        btnKeyBinder.addActionListener(this);
        btnChange.addActionListener(this);
        btnMusic.addActionListener(this);
        btnSFX.addActionListener(this);
        btnExit.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnMusic) {
            if (lblMusic.getText() == "ON")
                lblMusic.setText("OFF");
            else
                lblMusic.setText("ON");

        }

        if (e.getSource() == btnSFX) {
            if (lblSFX.getText() == "ON") {
                Audio.sfxOff();
                lblSFX.setText("OFF");
            }

            else {
                lblSFX.setText("ON");
                Audio.sfxOff();

            }
        }

        if (e.getSource() == btnChange) {
            user = JOptionPane.showInputDialog("Input new username");
            if (user != null)
                lblUserName.setText("USER: " + user);

        }

        if (e.getSource() == btnExit) {
            userInterface.changeScreen("StartScreen");
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(500, 750));
        frame.add(new SettingsScreen(new UserInterface()));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
