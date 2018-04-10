package gameclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Erik Lundow
 */

public class SettingsScreen extends JPanel implements ActionListener {
    private BufferedImage backgroundImage;
    private JTextField tfName = new JTextField();
    private Font fontHead = new Font("Orbitron", Font.BOLD, 50);
    private Font fontText = new Font("Orbitron", Font.BOLD, 20);
    private Font fontTextSmall = new Font("Orbitron", Font.BOLD, 14);
    private JLabel lblUserName = new JLabel("CHANGE USERNAME");
    private JLabel lblSettings = new JLabel("SETTINGS");
    private JLabel lblControls = new JLabel("CONTROLS");
    private JLabel lblUp = new JLabel("UP : ARROWKEY_UP");
    private JLabel lblLeft = new JLabel("LEFT : ARROWKEY_LEFT");
    private JLabel lblDown = new JLabel("LEFT : ARROWKEY_DOWN");
    private JLabel lblRight = new JLabel("RIGHT : ARROWKEY_RIGHT");

    private String fileChangePressed = "images/SetKeyBinding_Pressed.png";
    private String fileMusicPressed = "images/Music_Pressed.png";
    private String fileMusicUnpressed = "images/Music_Unpressed.png";
    private String fileChangeUnpressed = "images/SetKeyBinding_Unpressed.png";

    private Buttons btnChangeUP = new Buttons(fileChangePressed, fileChangeUnpressed);
    private Buttons btnChangeLEFT = new Buttons(fileChangePressed, fileChangeUnpressed);
    private Buttons btnChangeRIGHT = new Buttons(fileChangePressed, fileChangeUnpressed);
    private Buttons btnChangeDOWN = new Buttons(fileChangePressed, fileChangeUnpressed);
    private Buttons btnMusic = new Buttons(fileMusicPressed, fileMusicUnpressed);
    

    public SettingsScreen() {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(500, 500));
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(500, 500));

        try {
            backgroundImage = ImageIO.read(new File("resources/Stars.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        lblSettings.setFont(fontHead);
        lblSettings.setForeground(Color.white);
        lblSettings.setBounds(10, 10, 300, 50);

        lblUserName.setFont(fontText);
        lblUserName.setForeground(Color.WHITE);
        lblUserName.setBounds(30, 85, 300, 20);

        tfName.setBounds(300, 85, 100, 20);

        lblControls.setFont(fontText);
        lblControls.setForeground(Color.WHITE);
        lblControls.setBounds(30, 120, 300, 20);

        lblUp.setFont(fontTextSmall);
        lblDown.setFont(fontTextSmall);
        lblLeft.setFont(fontTextSmall);
        lblRight.setFont(fontTextSmall);

        lblUp.setForeground(Color.white);
        lblLeft.setForeground(Color.white);
        lblDown.setForeground(Color.white);
        lblRight.setForeground(Color.white);

        btnChangeUP.setWidth(200);
        btnChangeUP.setHeight(15);
        btnChangeLEFT.setWidth(200);
        btnChangeLEFT.setHeight(15);
        btnChangeDOWN.setWidth(200);
        btnChangeDOWN.setHeight(15);
        btnChangeRIGHT.setWidth(200);
        btnChangeRIGHT.setHeight(15);
        btnMusic.setWidth(60);
        btnMusic.setHeight(25);

        lblUp.setBounds(250, 150, 300, 20);
        lblLeft.setBounds(250, 175, 300, 20);
        lblDown.setBounds(250, 200, 300, 20);
        lblRight.setBounds(250, 225, 300, 20);

        // btnChangeUP.setBounds(275, 150, 200, 15);
        btnChangeUP.setBounds(30, 150, 200, 15);
        btnChangeLEFT.setBounds(30, 175, 200, 15);
        btnChangeDOWN.setBounds(30, 200, 200, 15);
        btnChangeRIGHT.setBounds(30, 225, 200, 15);
        btnMusic.setBounds(30, 275, 120, 50);
        
        add(btnMusic);
        add(btnChangeUP);
        add(btnChangeLEFT);
        add(btnChangeDOWN);
        add(btnChangeRIGHT);
        add(lblSettings);
        add(lblUserName);
        add(tfName);
        add(lblControls);
        add(lblUp);
        add(lblLeft);
        add(lblDown);
        add(lblRight);

        addListeners();
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        // g.setFont(fontHead);
        // g.setColor(Color.white);
        // g.drawString("SETTINGS", 30, 50);
        // g.setFont(fontText);
        // g.drawString("Change Username", 30, 100);

    }
    
    public void addListeners() {
        btnMusic.addActionListener(this);
    }

    public static void main(String[] args) {
        new SettingsScreen();
    }

    


    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==btnMusic) {
            System.out.println("ÄNTLIGEN");
        }
        
    }

}
