package gameclient.interfaces;

import gameobjects.Player;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Collection;

/**
 * @author Johannes Blüml
 */
public class ReadyPlayersPanel extends JPanel {
    private int scoreLimit;
    private int roundLimit;

    public ReadyPlayersPanel() {
        setLayout(new GridBagLayout());
    }

    public void setLimits(int roundLimit, int scoreLimit) {
        this.scoreLimit = scoreLimit;
        this.roundLimit = roundLimit;
    }

    public void update(Collection<Player> players, int ready, int amountPlayers) {
        removeAll();

        int row = 0;
        if (scoreLimit > 0) addTableRow(row++, "SCORE LIMIT", Integer.toString(scoreLimit));
        if (roundLimit > 0) addTableRow(row++, "ROUND LIMIT", Integer.toString(roundLimit));
        if (scoreLimit == 0 && roundLimit == 0) addTableRow(row++, "NEVER ENDING GAME");
        addRowSpacer(row++, 10);
        addTableRow(row++, "READY", ready + " / " + amountPlayers);
        addRowSpacer(row++, 10);

        JLabel titleLabel1 = new JLabel("PLAYERS");
        titleLabel1.setBorder(new MatteBorder(0, 0, 1, 0, Color.MAGENTA));
        GridBagConstraints gbc = getTableConstraints(0, row);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridwidth = 2;
        add(titleLabel1, gbc);

        row += 1;

        for (Player player : players) {

            JLabel nameLabel = new JLabel(player.getName().toUpperCase());
            nameLabel.setForeground(player.getColor());
            add(nameLabel, getTableConstraints(0, row));

            JLabel readyLabel = new JLabel(player.isReady() ? "READY" : "");
            readyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            readyLabel.setForeground(player.getColor());
            add(readyLabel, getTableConstraints(1, row));

            row += 1;
        }

        revalidate();
        repaint();
    }

    private void addRowSpacer(int row, int size) {
        GridBagConstraints gbc = getTableConstraints(0, row);
        gbc.gridwidth = 2;
        add(Box.createVerticalStrut(size), gbc);
    }

    private void addTableRow(int row, String column) {
        JLabel label = new JLabel(column);
        GridBagConstraints gbc = getTableConstraints(0, row);
        gbc.gridwidth = 2;
        add(label, gbc);
    }

    private void addTableRow(int row, String column1, String column2) {
        JLabel label1 = new JLabel(column1);
        add(label1, getTableConstraints(0, row));

        JLabel label2 = new JLabel(column2);
        label2.setHorizontalAlignment(SwingConstants.RIGHT);
        add(label2, getTableConstraints(1, row));
    }

    private GridBagConstraints getTableConstraints(int gridx, int gridy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.ipadx = 20;
        gbc.ipady = 10;
        if (gridx == 0) {
            gbc.anchor = GridBagConstraints.WEST;
        } else {
            gbc.anchor = GridBagConstraints.EAST;
        }
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }
}
