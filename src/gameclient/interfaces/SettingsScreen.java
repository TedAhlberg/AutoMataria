package gameclient.interfaces;


import gameclient.*;
import gameclient.keyinput.KeyBindings;
import gameclient.keyinput.KeyBindingsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author Erik Lundow
 */
public class SettingsScreen extends JPanel implements ActionListener {
    private JPanel pnlHead = new JPanel(new GridBagLayout());
    private JPanel pnlUserName = new JPanel(new GridBagLayout());
    private JPanel pnlMusic = new JPanel(new GridBagLayout());
    private JPanel pnlScreen = new JPanel(new GridBagLayout());
    private JTextField tfUserName = new JTextField();
    private BufferedImage backgroundImage;

    private String fileKeyBindPressed = "SetKeyBinding_Pressed.png";
    private String fileKeyBindUnpressed = "SetKeyBinding_Unpressed.png";
    private String user = "Player";
    private String screen[] = {"WINDOWED", "MAXIMIZED", "FULLSCREEN"};

    private JComboBox<String> screenSize = new JComboBox<String>(screen);

    private Buttons btnChange = new Buttons("CHANGE");
    private Buttons btnKeyBinder = new Buttons(fileKeyBindPressed, fileKeyBindUnpressed);
    private Buttons btnMusic = new Buttons("MUSIC");
    private Buttons btnBack = new Buttons("BACK");
    private Buttons btnSFX = new Buttons("SFX");
    private Buttons btnScreen = new Buttons("CHANGE");

    private KeyBindings keyBindings = new KeyBindings();
    private KeyBindingsPanel pnlKeyBinder = new KeyBindingsPanel(keyBindings);

    private JLabel lblUserName = new JLabel("USER: " + readSettings());
    private JLabel lblMusic = new JLabel("ON");
    private JLabel lblSFX = new JLabel("ON");
    private JLabel lblSettings = new JLabel("SETTINGS");
    private JLabel lblControls = new JLabel("CONTROLS");
    private JLabel lblSpacing = new JLabel(" ");
    private JLabel lblScreenSettings = new JLabel("SCREEN MODE: " + screen);
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
        c.gridy = 0;
        c.ipady = 50;

        pnlUserName.add(lblUserName, c);
        pnlUserName.setOpaque(false);

        // Add text Field
        c = new GridBagConstraints();
        c.gridy = 0;
        c.weightx = 1;
        c.insets = new Insets(0, 25, 0, 25);
        c.fill = GridBagConstraints.HORIZONTAL;
        pnlUserName.add(tfUserName, c);

        // Add ChangeButton
        c = new GridBagConstraints();
        c.gridy = 0;
        c.ipadx = 10;
        c.ipady = 10;
        pnlUserName.add(btnChange, c);

        // Add UserNamePanel
        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridx = 1;
        c.weighty = 20;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
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


        //Add lblScreenSettings
        c = new GridBagConstraints();
        c.gridy = 1;
        c.ipadx = 50;
        c.ipady = 10;
        c.gridx = 1;
        lblScreenSettings.setOpaque(false);
        pnlScreen.add(screenSize, c);

//        //Add btnScreen
//        c = new GridBagConstraints();
//        c.gridy = 1;
//        c.gridx = 2;
//        c.ipadx = 10;
//        c.ipady = 10;
//        pnlScreen.add(btnScreen, c);

        //Add pnlScreen
        // Add MusicPanel
        c = new GridBagConstraints();
        c.ipady = 30;
        c.gridy = 6;
        c.gridx = 1;
        pnlScreen.setOpaque(false);
        pnlHead.add(pnlScreen, c);


        // Add musicButton
        c = new GridBagConstraints();
        c.gridy = 1;
        c.ipadx = 10;
        c.ipady = 10;
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
        c.ipadx = 10;
        c.ipady = 10;
        pnlMusic.add(btnSFX, c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 3;
        c.ipadx = 10;
        c.ipady = 10;
        pnlMusic.add(btnBack, c);

        // Add SFXLabel
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 5;
        c.ipadx = 10;

        pnlMusic.add(lblSFX, c);

        // Add MusicPanel
        c = new GridBagConstraints();
        c.ipady = 30;
        c.gridy = 7;
        c.gridx = 1;
        pnlHead.add(pnlMusic, c);

        // Add spacing

        c.gridy = 8;
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

    public void writeToFile(String username) {
        try {
            FileOutputStream fos = new FileOutputStream("resources/settings/username.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(username);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return readSettings();
    }

    public String readSettings() {
        try {
            FileInputStream fis = new FileInputStream("resources/settings/username.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            user = (String) ois.readObject();
            ois.close();
            return user;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "Player";


    }

    public void addListeners() {
        btnKeyBinder.addActionListener(this);
        btnChange.addActionListener(this);
        btnMusic.addActionListener(this);
        btnSFX.addActionListener(this);
        btnBack.addActionListener(this);
        screenSize.addActionListener(this);
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
        if (e.getSource() == screenSize) {
            if (screenSize.getSelectedItem().equals("MAXIMIZED")) {
                userInterface.setWindowMode(gameclient.Window.Mode.Maximized);
                repaint();
            } else if (screenSize.getSelectedItem().equals("WINDOWED")) {
                userInterface.setWindowMode(gameclient.Window.Mode.Windowed);
                repaint();
            } else if (screenSize.getSelectedItem().equals("FULLSCREEN")) {
                userInterface.setWindowMode(gameclient.Window.Mode.Fullscreen);
                repaint();
            }

        }

        if (e.getSource() == btnSFX) {
            if (lblSFX.getText() == "ON") {
                Audio.sfxOff();
                lblSFX.setText("OFF");
            } else {
                lblSFX.setText("ON");
                Audio.sfxOff();

            }
        }

        if (e.getSource() == btnChange) {
            lblUserName.setText("USER: " + tfUserName.getText());
            writeToFile(tfUserName.getText());
            user = tfUserName.getText();
            tfUserName.setText("");

        }

        if (e.getSource() == btnBack) {
            userInterface.changeToPreviousScreen();
        }

    }

}
