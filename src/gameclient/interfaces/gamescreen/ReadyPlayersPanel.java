package gameclient.interfaces.gamescreen;

import gameobjects.Player;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Collection;

/**
 * @author Johannes Blüml
 */
public class ReadyPlayersPanel extends TablePanel {
    private int scoreLimit;
    private int roundLimit;
    private String serverName;
    private String mapName;

    public ReadyPlayersPanel() {
        setLayout(new GridBagLayout());
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setLimits(int roundLimit, int scoreLimit) {
        this.scoreLimit = scoreLimit;
        this.roundLimit = roundLimit;
    }

    public void update(Collection<Player> players, int ready, int amountPlayers) {
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
        if (mapName != null) addTableRow("MAP", mapName);
        if (scoreLimit > 0) addTableRow("SCORE LIMIT", Integer.toString(scoreLimit));
        if (roundLimit > 0) addTableRow("ROUND LIMIT", Integer.toString(roundLimit));
        if (scoreLimit == 0 && roundLimit == 0) addTableRow("NEVER ENDING GAME");
        addRowSpacer(2);
        addTableRow("READY", ready + " / " + amountPlayers);
        addRowSpacer(8);

        JLabel titleLabel1 = new JLabel("PLAYERS");
        titleLabel1.setBorder(new MatteBorder(0, 0, 1, 0, Color.MAGENTA));
        GridBagConstraints gbc = getTableConstraints(0);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridwidth = 2;
        add(titleLabel1, gbc);

        for (Player player : players) {

            JLabel nameLabel = new JLabel(player.getName().toUpperCase());
            nameLabel.setForeground(player.getColor());
            add(nameLabel, getTableConstraints(0));

            JLabel readyLabel = new JLabel(player.isReady() ? "READY" : "");
            readyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            readyLabel.setForeground(player.getColor());
            add(readyLabel, getTableConstraints(1));
        }
        addYFiller();

        revalidate();
        repaint();
    }
}
