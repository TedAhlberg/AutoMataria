package gameclient.interfaces;

import gameobjects.Player;

import javax.swing.*;
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
        GridBagConstraints c;

        if (scoreLimit > 0) {
            add(new JLabel("SCORE LIMIT"), getTableConstraints(0, row));
            add(new JLabel(Integer.toString(scoreLimit)), getTableConstraints(1, row));
            row += 1;
        }

        if (!gameOver) playedRounds += 1;
        if (roundLimit > 0) {
            add(new JLabel("ROUND"), getTableConstraints(0, row));
            add(new JLabel(playedRounds + " / " + roundLimit), getTableConstraints(1, row));
            row += 1;
        } else {
            add(new JLabel("ROUND"), getTableConstraints(0, row));
            add(new JLabel(Integer.toString(playedRounds)), getTableConstraints(1, row));
            row += 1;
        }

        add(new JLabel(), getTableConstraints(0, row));
        row += 1;

        add(new JLabel("PLAYER"), getTableConstraints(0, row));
        add(new JLabel("SCORE"), getTableConstraints(1, row));
        row += 1;

        for (Player player : scores.keySet()) {
            JLabel nameLabel = new JLabel(player.getName());
            nameLabel.setForeground(player.getColor());

            JLabel scoreLabel = new JLabel(Integer.toString(scores.get(player)));
            scoreLabel.setForeground(player.getColor());

            add(nameLabel, getTableConstraints(0, row));
            add(scoreLabel, getTableConstraints(1, row));

            row += 1;
        }

        revalidate();
        repaint();
    }

    private void addTableRow(JLabel column1, JLabel column2, int row) {
        add(column1, getTableConstraints(0, row));
        add(column2, getTableConstraints(1, row));
    }

    private GridBagConstraints getTableConstraints(int gridx, int gridy) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        return c;
    }
}
