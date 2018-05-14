package gameclient.interfaces.gamescreen;

import gameclient.interfaces.Window;
import gameobjects.Player;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.HashMap;

/**
 * @author Johannes Blüml
 */
public class ScorePanel extends TablePanel {
    private int scoreLimit;
    private int roundLimit;
    private String serverName;

    public ScorePanel() {
        setLayout(new GridBagLayout());
    }

    public static void main(String[] args) {
        ScorePanel scorePanel = new ScorePanel();
        scorePanel.setBackground(Color.BLACK);

        gameclient.interfaces.Window frame = new Window("", new Dimension(300, 600));
        frame.setContentPane(scorePanel);
        frame.pack();
        frame.setVisible(true);

        scorePanel.update(new HashMap<>(), 66, 7, false);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setLimits(int roundLimit, int scoreLimit) {
        this.scoreLimit = scoreLimit;
        this.roundLimit = roundLimit;
    }

    public void update(HashMap<Player, Integer> scores, int highestScore, int playedRounds, boolean gameOver) {
        removeAll();

        if (serverName != null) {
            JLabel label = new JLabel(serverName);
            label.setForeground(new Color(0xFBBDFF));
            GridBagConstraints gbc = getTableConstraints(0);
            gbc.ipady = 0;
            gbc.gridwidth = 2;
            add(label, gbc);
            addRowSpacer(12);
        }
        if (scoreLimit > 0) addTableRow("SCORE LIMIT", highestScore + " / " + scoreLimit);
        int currentRound = (!gameOver) ? playedRounds + 1 : playedRounds;
        addTableRow("ROUND", roundLimit < 1 ? Integer.toString(currentRound) : currentRound + " / " + roundLimit);

        addRowSpacer(8);

        JLabel titleLabel1 = new JLabel("PLAYER SCORES");
        titleLabel1.setBorder(new MatteBorder(0, 0, 1, 0, Color.MAGENTA));
        GridBagConstraints gbc = getTableConstraints(0);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridwidth = 2;
        add(titleLabel1, gbc);

        for (Player player : scores.keySet()) {
            JLabel nameLabel = new JLabel(player.getName());
            nameLabel.setForeground(player.getColor());

            JLabel scoreLabel = new JLabel(Integer.toString(scores.get(player)));
            scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            scoreLabel.setForeground(player.getColor());

            add(nameLabel, getTableConstraints(0));
            add(scoreLabel, getTableConstraints(1));
        }
        addYFiller();

        revalidate();
        repaint();
    }
}
