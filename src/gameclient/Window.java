package gameclient;

import javax.swing.*;
import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class Window extends JFrame {
    public Window (int width, int height, String title, Game game) {
        this.setTitle(title);
        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(this.getPreferredSize());
        this.setMinimumSize(this.getPreferredSize());
        this.add(game);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
