package gameclient.interfaces.gamescreen;

import javax.swing.*;
import java.awt.*;

/**
 * @author Johannes Blüml
 */
public abstract class TablePanel extends JPanel {

    protected void addYFiller() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.weighty = 1;
        add(Box.createVerticalStrut(0), gbc);
    }

    protected void addRowSpacer(int size) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        add(Box.createVerticalStrut(size), gbc);
    }

    protected void addTableRow(String column) {
        JLabel label = new JLabel(column);
        label.setForeground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = getTableConstraints(0);
        gbc.gridwidth = 2;
        add(label, gbc);
    }

    protected void addTableRow(String column1, String column2) {
        JLabel label1 = new JLabel(column1);
        label1.setForeground(Color.LIGHT_GRAY);
        add(label1, getTableConstraints(0));

        JLabel label2 = new JLabel(column2);
        label2.setForeground(Color.LIGHT_GRAY);
        label2.setHorizontalAlignment(SwingConstants.RIGHT);
        add(label2, getTableConstraints(1));
    }

    protected GridBagConstraints getTableConstraints(int gridx) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.ipady = 10;
        if (gridx == 0) {
            gbc.weightx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
        } else {
            gbc.anchor = GridBagConstraints.EAST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
        }
        return gbc;
    }
}
