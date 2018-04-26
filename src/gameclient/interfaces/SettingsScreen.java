package gameclient.interfaces;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gameclient.Audio;
import gameclient.Buttons;
import gameclient.MusicManager;
import gameclient.Resources;
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
    private JTextField tfUserName = new JTextField();
    private BufferedImage backgroundImage;
    private Font fontHead = new Font("Orbitron", Font.BOLD, 50);
    private Font fontText = new Font("Orbitron", Font.BOLD, 25);
    private Font fontSmall = new Font("Orbitron", Font.BOLD, 20);

    private String fileKeyBindPressed = "SetKeyBinding_Pressed.png";
    private String fileKeyBindUnpressed = "SetKeyBinding_Unpressed.png";
    private String user = "Testperson";
    

    private Buttons btnChange = new Buttons("CHANGE");
    private Buttons btnKeyBinder = new Buttons(fileKeyBindPressed, fileKeyBindUnpressed);
    private Buttons btnMusic = new Buttons("MUSIC");
    private Buttons btnExit = new Buttons("EXIT");
    private Buttons btnSFX = new Buttons("SFX");

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
        pnlHead.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();

        // Add Headline
        

        c.gridy = 1;
        c.gridx = 1;
        c.weighty = 1;
        c.weightx = 11;
        c.ipady = 30;
        c.gridwidth = GridBagConstraints.REMAINDER;
        pnlHead.add(lblSettings, c);

        // Add UserNamelbl
        c = new GridBagConstraints();
        c.gridy=0;
        c.ipady = 50;

        pnlUserName.add(lblUserName, c);
        pnlUserName.setOpaque(false);

       
        //Add text Field
        c = new GridBagConstraints();
        c.gridy=0;
        c.weightx=1;
        c.insets = new Insets(0,25,0,25);
        c.fill=GridBagConstraints.HORIZONTAL;
        pnlUserName.add(tfUserName, c);
        
        
        // Add ChangeButton
        c = new GridBagConstraints();
        c.gridy=0;
        c.ipadx=10;
        c.ipady=10;
        pnlUserName.add(btnChange, c);

        // Add UserNamePanel
        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridx=1;
        c.weighty = 20;
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.HORIZONTAL;
        pnlHead.add(pnlUserName, c);

        // Add ControlLabel
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.ipady = 10;
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
        c.ipadx=10;
        c.ipady=10;
        c.gridx = 1;
        pnlMusic.setOpaque(false);
        pnlMusic.add(btnMusic, c);

        // Add musicLabel
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 2;
        c.ipadx = 10;

        pnlMusic.add(lblMusic, c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 3;
        c.ipadx = 10;

        pnlMusic.add(lblSpacing2, c);

        // Add SFXButton
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 4;
        c.ipadx=10;
        c.ipady=10;
        pnlMusic.add(btnSFX, c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 3;
        c.ipadx=10;
        c.ipady=10;
        pnlMusic.add(btnExit, c);
        
      
        // Add SFXLabel
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 5;
        c.ipadx = 10;

        pnlMusic.add(lblSFX, c);

        // Add MusicPanel
        c = new GridBagConstraints();
        c.ipady = 30;
        c.gridy = 6;
        c.gridx = 1;
        pnlHead.add(pnlMusic, c);

        // Add spacing
      
        c.gridy = 7;
        c.gridx = 1;
        c.weighty = 1;
        c.weightx = 11;
        c.gridwidth = GridBagConstraints.REMAINDER;
        pnlHead.add(lblSpacing, c);
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
            MusicManager.stop();
            if (lblMusic.getText() == "ON")
                lblMusic.setText("OFF");
            else {
                lblMusic.setText("ON");
                MusicManager.getInstance().menuTrack();
            }

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
            

                lblUserName.setText("USER: " + tfUserName.getText());
                tfUserName.setText("");

        }

        if (e.getSource() == btnExit) {
            userInterface.changeScreen("StartScreen");
        }

    }

}
