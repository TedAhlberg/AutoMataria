package gameclient.interfaces;

import gameobjects.Player;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.HashMap;

/**
 * @author Johannes Blüml
 */
public class ScorePanel extends JPanel {
    public ScorePanel() {
        setLayout(new GridBagLayout());
    }

    public void update(HashMap<Player, Integer> scores, int scoreLimit, int roundLimit, int playedRounds, boolean gameOver) {
        removeAll();

        int row = 0;

        if (scoreLimit > 0) {
            addTableRow(row, "SCORE LIMIT", Integer.toString(scoreLimit));
            row += 1;
        }

        if (roundLimit > 0) {
            addTableRow(row, "ROUND", playedRounds + " / " + roundLimit);
            row += 1;
        } else {
            addTableRow(row, "ROUND", Integer.toString(playedRounds));
            row += 1;
        }

        addRowSpacer(row, 10);
        row += 1;

        JLabel titleLabel1 = new JLabel("PLAYER SCORES");
        titleLabel1.setBorder(new MatteBorder(0, 0, 1, 0, Color.MAGENTA));
        GridBagConstraints gbc = getTableConstraints(0, row);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridwidth = 2;
        add(titleLabel1, gbc);
        row += 1;

        for (Player player : scores.keySet()) {
            JLabel nameLabel = new JLabel(player.getName());
            nameLabel.setForeground(player.getColor());

            JLabel scoreLabel = new JLabel(Integer.toString(scores.get(player)));
            scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            scoreLabel.setForeground(player.getColor());

            add(nameLabel, getTableConstraints(0, row));
            add(scoreLabel, getTableConstraints(1, row));

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
