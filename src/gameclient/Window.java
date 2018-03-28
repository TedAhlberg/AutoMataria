package gameclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Johannes Blüml
 */
public class Window extends JFrame implements WindowListener {
    public Window(String title, Game game) {
        this.setTitle(title);
        this.add(game);
        this.setResizable(false);
        this.setUndecorated(true);
        this.addWindowListener(this);
        this.setBackground(Color.BLACK);
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {

    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }
}
